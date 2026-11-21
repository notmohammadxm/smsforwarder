package com.smsforwarding.local.sms

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.smsforwarding.local.SmsForwardingApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SmsStatusReceiver : BroadcastReceiver() {
 override fun onReceive(context: Context, intent: Intent) {
  val logId = intent.getLongExtra(SmsForwardingManager.EXTRA_LOG_ID, -1L)
  if (logId <= 0L) return
  val callbackResult = resultCode
  val app = context.applicationContext as SmsForwardingApp
  val pending = goAsync()
  CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
   try {
    val failed = if (callbackResult == Activity.RESULT_OK) 0 else 1
    val message = if (failed == 0) null else describeFailure(callbackResult)
    when (intent.action) {
     SmsForwardingManager.ACTION_SENT -> app.container.repository.recordSentPart(
      logId, failed, System.currentTimeMillis(), callbackResult.takeIf { failed == 1 }, message
     )
     SmsForwardingManager.ACTION_DELIVERED -> app.container.repository.recordDeliveredPart(
      logId, failed, callbackResult.takeIf { failed == 1 }, message
     )
    }
   } finally {
    pending.finish()
   }
  }
 }

 private fun describeFailure(code: Int): String = when (code) {
  4 -> "شبکه تلفن همراه در دسترس نیست."
  2 -> "رادیوی تلفن همراه خاموش است."
  3 -> "ساختار پیامک نامعتبر است."
  5 -> "محدودیت ارسال پیامک اعمال شده است."
  else -> "ارسال یا تحویل پیامک توسط اپراتور تأیید نشد (کد $code)."
 }
}
