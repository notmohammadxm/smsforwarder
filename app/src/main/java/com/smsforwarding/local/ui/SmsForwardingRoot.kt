package com.smsforwarding.local.ui
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Rule
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument

private data class Tab(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
private val tabs = listOf(
 Tab("dashboard", "خانه", Icons.Default.Home),
 Tab("rules", "قوانین", Icons.Default.Rule),
 Tab("history", "سوابق", Icons.Default.History),
 Tab("settings", "تنظیمات", Icons.Default.Settings)
)
@Composable
fun SmsForwardingRoot(viewModel: MainViewModel) {
 val state by viewModel.uiState.collectAsStateWithLifecycle()
 val nav = rememberNavController()
 val entry by nav.currentBackStackEntryAsState()
 val route = entry?.destination?.route
 val showTabs = tabs.any { it.route == route }
 Scaffold(bottomBar = {
  if (showTabs) NavigationBar {
   tabs.forEach { tab ->
    NavigationBarItem(
     selected = route == tab.route,
     onClick = {
      nav.navigate(tab.route) {
       popUpTo(nav.graph.findStartDestination().id) { saveState = true }
       launchSingleTop = true
       restoreState = true
      }
     },
     icon = { Icon(tab.icon, contentDescription = null) },
     label = { Text(tab.label) }
    )
   }
  }
 }) { padding ->
  NavHost(navController = nav, startDestination = "dashboard", modifier = Modifier.padding(padding)) {
   composable("dashboard") {
    DashboardScreen(state, viewModel::setGlobalEnabled,
     { nav.navigate("rules") }, { nav.navigate("history") },
     { nav.navigate("permissions") }, { nav.navigate("simulation") })
   }
   composable("rules") {
    RulesScreen(state.rules, { nav.navigate("editor/0") },
     { nav.navigate("editor/${it.id}") }, viewModel::setRuleEnabled, viewModel::deleteRule)
   }
   composable("editor/{ruleId}", arguments = listOf(navArgument("ruleId") { type = NavType.LongType })) { backStack ->
    val id = backStack.arguments?.getLong("ruleId") ?: 0L
    val rule = state.rules.firstOrNull { it.id == id }
    RuleEditorScreen(rule, id != 0L && rule == null, { nav.popBackStack() }, {
     viewModel.saveRule(it); nav.popBackStack()
    })
   }
   composable("history") { HistoryScreen(state.logs) }
   composable("settings") {
    SettingsScreen(state.blockSensitiveMessages, viewModel::setBlockSensitive,
     { nav.navigate("permissions") }, { nav.navigate("simulation") })
   }
   composable("permissions") { PermissionDiagnosticsScreen { nav.popBackStack() } }
   composable("simulation") { SimulationScreen(viewModel, state) }
  }
 }
}
