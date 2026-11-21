package com.smsforwarding.local

import android.app.Application
import androidx.room.Room
import com.smsforwarding.local.data.local.SmsDatabase
import com.smsforwarding.local.data.preferences.SettingsRepository
import com.smsforwarding.local.data.repository.ForwardingRepository
import com.smsforwarding.local.domain.DestinationDeduplicator
import com.smsforwarding.local.domain.SensitiveMessageGuard
import com.smsforwarding.local.domain.SmsMessageAssembler
import com.smsforwarding.local.domain.SmsRuleMatcher
import com.smsforwarding.local.sms.IncomingSmsProcessor
import com.smsforwarding.local.sms.SmsForwardingManager

class SmsForwardingApp : Application() {
 lateinit var container: AppContainer
  private set
 override fun onCreate() {
  super.onCreate()
  container = AppContainer(this)
 }
}

class AppContainer(application: Application) {
 val database: SmsDatabase = Room.databaseBuilder(
  application, SmsDatabase::class.java, "sms-forwarding.db"
 ).build()
 val settings = SettingsRepository(application)
 val repository = ForwardingRepository(database.forwardRuleDao(), database.forwardLogDao())
 val matcher = SmsRuleMatcher()
 val guard = SensitiveMessageGuard()
 val deduplicator = DestinationDeduplicator()
 val assembler = SmsMessageAssembler()
 val smsManager = SmsForwardingManager(application, repository)
 val incomingProcessor = IncomingSmsProcessor(
  settings, repository, matcher, guard, deduplicator, smsManager
 )
}
