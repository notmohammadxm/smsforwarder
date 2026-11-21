package com.smsforwarding.local.sms

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.telephony.SmsManager
import androidx.core.content.ContextCompat
import com.smsforwarding.local.data.local.ForwardStatus
import com.smsforwarding.local.data.repository.ForwardingRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SmsForwardingManager(
 private val context: Context,
 private val repository: ForwardingRepository
) {
 private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

 suspend fun send(logId: Long, destination: String, message: String) {
  if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) !=
   PackageManager.PERMISSION_GRANTED
  ) {
   markFailure(logId, ForwardStatus.PERMISSION_DENIED, null, "مجوز ارسال پیامک داده نشده است.")
   return
  }
  try {
   val manager = SmsManager.getDefault()
   val parts = manager.divideMessage(message)
   if (parts.isEmpty()) {
    markFailure(logId, ForwardStatus.FAILED, null, "متن پیامک خالی است.")
    return
   }
   repository.prepareSend(logId, parts.size)
   val sent = parts.mapIndexed { index, _ -> callbackPendingIntent(ACTION_SENT, logId, index) }
   val delivered = parts.mapIndexed { index, _ -> callbackPendingIntent(ACTION_DELIVERED, logId, index) }
   if (parts.size == 1) {
    manager.sendTextMessage(destination, null, parts[0], sent[0], delivered[0])
   } else {
    manager.sendMultipartTextMessage(destination, null, ArrayList(parts), ArrayList(sent), ArrayList(delivered))
   }
  } catch (error: SecurityException) {
   markFailure(logId, ForwardStatus.PERMISSION_DENIED, null, error.message ?: "اجازه ارسال پیامک رد شد.")
  } catch (error: IllegalArgumentException) {
   markFailure(logId, ForwardStatus.FAILED, null, error.message ?: "شماره مقصد نامعتبر است.")
  } catch (error: Exception) {
   markFailure(logId, ForwardStatus.FAILED, null, error.message ?: "خطای ارسال پیامک.")
  }
 }

 private fun callbackPendingIntent(action: String, logId: Long, part: Int): PendingIntent {
  val intent = Intent(context, SmsStatusReceiver::class.java).apply {
   this.action = action
   data = Uri.parse("smsforward://$action/$logId/$part")
   putExtra(EXTRA_LOG_ID, logId)
   putExtra(EXTRA_PART_INDEX, part)
  }
  val offset = if (action == ACTION_SENT) 0 else 500
  return PendingIntent.getBroadcast(
   context, (logId * 1000L + part + offset).toInt(), intent,
   PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
  )
 }

 private fun markFailure(id: Long, status: ForwardStatus, code: Int?, message: String) {
  ioScope.launch { repository.setFailure(id, status, code, message) }
 }

 companion object {
  const val ACTION_SENT = "com.smsforwarding.local.SMS_SENT"
  const val ACTION_DELIVERED = "com.smsforwarding.local.SMS_DELIVERED"
  const val EXTRA_LOG_ID = "forward_log_id"
  const val EXTRA_PART_INDEX = "part_index"
 }
}
