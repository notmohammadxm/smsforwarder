package com.smsforwarding.local.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.smsforwarding.local.data.local.ForwardLog
import com.smsforwarding.local.data.local.ForwardStatus
import java.text.DateFormat
import java.util.Date

@Composable
fun HistoryScreen(logs: List<ForwardLog>) {
 Column(Modifier.fillMaxSize().padding(16.dp)) {
  Text("سوابق فورواردینگ", style = MaterialTheme.typography.headlineSmall)
  Text("متن کامل پیامک ذخیره نمی‌شود.", color = MaterialTheme.colorScheme.onSurfaceVariant)
  Spacer(Modifier.height(12.dp))
  if (logs.isEmpty()) {
   Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
    Text("هنوز سابقه‌ای وجود ندارد.")
   }
  } else LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
   items(logs, key = { it.id }) { log ->
    ElevatedCard(Modifier.fillMaxWidth()) {
     Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
      Text(statusLabel(log.status), style = MaterialTheme.typography.titleMedium,
       color = if (log.status == ForwardStatus.FAILED || log.status == ForwardStatus.DELIVERY_FAILED ||
        log.status == ForwardStatus.PERMISSION_DENIED || log.status == ForwardStatus.BLOCKED_SENSITIVE)
        MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
      Text("مبدأ: ${log.sourceNumber}")
      Text("مقصد: ${log.destinationNumber}")
      Text("قوانین منطبق: ${log.matchingRuleIds.joinToString()}")
      Text(DateFormat.getDateTimeInstance().format(Date(log.receivedAt)),
       style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
      log.errorMessage?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error) }
     }
    }
   }
  }
 }
}

fun statusLabel(status: ForwardStatus): String = when (status) {
 ForwardStatus.QUEUED -> "در صف ارسال"
 ForwardStatus.SENDING -> "در حال ارسال"
 ForwardStatus.SENT -> "ارسال‌شده؛ تحویل هنوز تأیید نشده"
 ForwardStatus.DELIVERED -> "تحویل توسط شبکه تأیید شد"
 ForwardStatus.FAILED -> "ارسال ناموفق"
 ForwardStatus.DELIVERY_FAILED -> "تحویل تأیید نشد"
 ForwardStatus.PERMISSION_DENIED -> "مجوز ارسال وجود ندارد"
 ForwardStatus.BLOCKED_SENSITIVE -> "مسدودشده توسط محافظت پیام حساس"
}
