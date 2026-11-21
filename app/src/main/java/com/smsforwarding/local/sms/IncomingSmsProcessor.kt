package com.smsforwarding.local.sms

import com.smsforwarding.local.data.local.ForwardLog
import com.smsforwarding.local.data.local.ForwardStatus
import com.smsforwarding.local.data.preferences.SettingsRepository
import com.smsforwarding.local.data.repository.ForwardingRepository
import com.smsforwarding.local.domain.AssembledSms
import com.smsforwarding.local.domain.DestinationDeduplicator
import com.smsforwarding.local.domain.SensitiveMessageGuard
import com.smsforwarding.local.domain.SmsRuleMatcher
import kotlinx.coroutines.flow.first
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class IncomingSmsProcessor(
 private val settings: SettingsRepository,
 private val repository: ForwardingRepository,
 private val matcher: SmsRuleMatcher,
 private val guard: SensitiveMessageGuard,
 private val deduplicator: DestinationDeduplicator,
 private val smsManager: SmsForwardingManager
) {
 suspend fun process(message: AssembledSms) {
  if (!settings.globalEnabled.first()) return
  val matchingRules = repository.getEnabledRules().filter { rule ->
   matcher.matches(rule, message.sender, message.body, globalEnabled = true)
  }
  if (matchingRules.isEmpty()) return

  val fingerprint = fingerprint(message)
  val blockSensitive = settings.blockSensitiveMessages.first() && guard.isSensitive(message.body)
  deduplicator.group(matchingRules).forEach { target ->
   val logId = repository.addLogIfAbsent(
    ForwardLog(
     matchingRuleIds = target.matchingRuleIds,
     sourceNumber = message.sender,
     destinationNumber = target.destinationNumber,
     receivedAt = message.receivedAt,
     sentAt = null,
     status = if (blockSensitive) ForwardStatus.BLOCKED_SENSITIVE else ForwardStatus.QUEUED,
     errorCode = null,
     errorMessage = if (blockSensitive) "برای محافظت از رمزها و کدهای ورود، این پیام ارسال نشد." else null,
     messageFingerprint = fingerprint
    )
   )
   if (logId <= 0L) return@forEach
   if (blockSensitive) {
    repository.markSensitiveBlocked(logId, "برای محافظت از رمزها و کدهای ورود، این پیام ارسال نشد.")
    return@forEach
   }
   val body = if (target.includeSenderInfo) {
    val time = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(message.receivedAt))
    "از: ${message.sender} | زمان: $time\n${message.body}"
   } else message.body
   smsManager.send(logId, target.destinationNumber, body)
  }
 }

 private fun fingerprint(message: AssembledSms): String {
  val value = "${message.sender}|${message.receivedAt}|${message.body}"
  return MessageDigest.getInstance("SHA-256").digest(value.toByteArray(Charsets.UTF_8))
   .joinToString("") { "%02x".format(it) }
 }
}
