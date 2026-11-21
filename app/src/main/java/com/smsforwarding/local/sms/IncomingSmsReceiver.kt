package com.smsforwarding.local.sms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.smsforwarding.local.SmsForwardingApp
import com.smsforwarding.local.domain.SmsSegment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class IncomingSmsReceiver : BroadcastReceiver() {
 override fun onReceive(context: Context, intent: Intent) {
  if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return
  val segments = Telephony.Sms.Intents.getMessagesFromIntent(intent)
   ?.map { sms ->
    SmsSegment(
     sender = sms.originatingAddress.orEmpty(),
     body = sms.messageBody.orEmpty(),
     timestampMillis = sms.timestampMillis
    )
   }.orEmpty()
  if (segments.isEmpty()) return
  val app = context.applicationContext as SmsForwardingApp
  val pending = goAsync()
  CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
   try {
    app.container.assembler.assemble(segments).forEach { app.container.incomingProcessor.process(it) }
   } catch (_: Exception) {
    // Message bodies are intentionally excluded from crash logs and persistent history.
   } finally {
    pending.finish()
   }
  }
 }
}
