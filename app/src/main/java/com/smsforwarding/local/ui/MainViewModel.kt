package com.smsforwarding.local.ui
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.smsforwarding.local.AppContainer
import com.smsforwarding.local.data.local.*
import com.smsforwarding.local.domain.SmsRuleMatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AppUiState(
 val globalEnabled: Boolean = false,
 val blockSensitiveMessages: Boolean = true,
 val rules: List<ForwardRule> = emptyList(),
 val logs: List<ForwardLog> = emptyList()
)
data class SimulationResult(val matchingRules: List<ForwardRule>, val blockedAsSensitive: Boolean)

class MainViewModel(private val container: AppContainer) : ViewModel() {
 val uiState: StateFlow<AppUiState> = combine(
  container.settings.globalEnabled,
  container.settings.blockSensitiveMessages,
  container.repository.observeRules(),
  container.repository.observeLogs()
 ) { enabled, protection, rules, logs -> AppUiState(enabled, protection, rules, logs) }
  .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppUiState())

 private val _simulation = MutableStateFlow<SimulationResult?>(null)
 val simulation: StateFlow<SimulationResult?> = _simulation.asStateFlow()

 fun setGlobalEnabled(enabled: Boolean) = viewModelScope.launch { container.settings.setGlobalEnabled(enabled) }
 fun setBlockSensitive(enabled: Boolean) = viewModelScope.launch { container.settings.setBlockSensitiveMessages(enabled) }
 fun setRuleEnabled(id: Long, enabled: Boolean) = viewModelScope.launch {
  container.repository.setRuleEnabled(id, enabled, System.currentTimeMillis())
 }
 fun saveRule(rule: ForwardRule) = viewModelScope.launch {
  val now = System.currentTimeMillis()
  container.repository.saveRule(rule.copy(createdAt = if (rule.id == 0L) now else rule.createdAt, updatedAt = now))
 }
 fun deleteRule(rule: ForwardRule) = viewModelScope.launch { container.repository.deleteRule(rule) }

 fun simulate(sender: String, body: String) {
  val state = uiState.value
  val matcher = SmsRuleMatcher()
  val matches = state.rules.filter { matcher.matches(it, sender, body, state.globalEnabled) }
  _simulation.value = SimulationResult(
   matches,
   state.blockSensitiveMessages && container.guard.isSensitive(body) && matches.isNotEmpty()
  )
 }
 fun clearSimulation() { _simulation.value = null }

 class Factory(private val container: AppContainer) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
   if (modelClass.isAssignableFrom(MainViewModel::class.java)) return MainViewModel(container) as T
   throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
  }
 }
}
