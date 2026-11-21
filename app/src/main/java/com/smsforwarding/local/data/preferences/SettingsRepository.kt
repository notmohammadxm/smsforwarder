package com.smsforwarding.local.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "sms_forwarding_settings")

class SettingsRepository(private val context: Context) {
 private val globalKey = booleanPreferencesKey("global_forwarding_enabled")
 private val protectionKey = booleanPreferencesKey("block_sensitive_messages")

 val globalEnabled: Flow<Boolean> = context.settingsDataStore.data.map { it[globalKey] ?: false }
 val blockSensitiveMessages: Flow<Boolean> =
  context.settingsDataStore.data.map { it[protectionKey] ?: true }

 suspend fun setGlobalEnabled(enabled: Boolean) {
  context.settingsDataStore.edit { it[globalKey] = enabled }
 }
 suspend fun setBlockSensitiveMessages(enabled: Boolean) {
  context.settingsDataStore.edit { it[protectionKey] = enabled }
 }
}
