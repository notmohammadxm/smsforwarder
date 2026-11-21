package com.smsforwarding.local.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ForwardToInbox
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.smsforwarding.local.data.local.ForwardStatus
import java.text.DateFormat
import java.util.Date

@Composable
fun DashboardScreen(
 state: AppUiState,
 onGlobalChange: (Boolean) -> Unit,
 onRules: () -> Unit,
 onHistory: () -> Unit,
 onPermissions: () -> Unit,
 onSimulation: () -> Unit
) {
 val active = state.rules.count { it.enabled }
 val successful = state.logs.count { it.status == ForwardStatus.SENT || it.status == ForwardStatus.DELIVERED }
 val failed = state.logs.count {
  it.status == ForwardStatus.FAILED || it.status == ForwardStatus.DELIVERY_FAILED ||
   it.status == ForwardStatus.PERMISSION_DENIED || it.status == ForwardStatus.BLOCKED_SENSITIVE
 }
 Column(
  modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
  verticalArrangement = Arrangement.spacedBy(16.dp)
 ) {
  Text("فوروارد پیامک", style = MaterialTheme.typography.headlineMedium)
  Text("مدیریت محلی و شفاف پیامک‌های ورودی", color = MaterialTheme.colorScheme.onSurfaceVariant)
  ElevatedCard(Modifier.fillMaxWidth()) {
   Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
    Row(verticalAlignment = Alignment.CenterVertically) {
     Icon(Icons.Default.ForwardToInbox, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
     Text("  وضعیت کلی فورواردینگ", style = MaterialTheme.typography.titleLarge)
     Spacer(Modifier.weight(1f))
     Switch(checked = state.globalEnabled, onCheckedChange = onGlobalChange)
    }
    Text(
     if (state.globalEnabled) "فعال — قوانین روشن بررسی می‌شوند."
     else "متوقف — هیچ پیامکی به مقصدها ارسال نخواهد شد.",
     color = if (state.globalEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    )
    Text("پایش رویدادمحور سیستم؛ بدون polling پس‌زمینه", style = MaterialTheme.typography.bodySmall)
   }
  }
  Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
   MetricCard("قوانین فعال", active.toString(), Icons.Default.Security, Modifier.weight(1f))
   MetricCard("ارسال موفق", successful.toString(), Icons.Default.CheckCircle, Modifier.weight(1f))
   MetricCard("ناموفق / مسدود", failed.toString(), Icons.Default.Error, Modifier.weight(1f))
  }
  Card(Modifier.fillMaxWidth()) {
   Column(Modifier.padding(16.dp)) {
    Text("آخرین فعالیت", style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(8.dp))
    val latest = state.logs.firstOrNull()
    if (latest == null) {
     Text("هنوز فعالیتی ثبت نشده است.", color = MaterialTheme.colorScheme.onSurfaceVariant)
    } else {
     Text(statusLabel(latest.status), color = MaterialTheme.colorScheme.primary)
     Text("از ${latest.sourceNumber} به ${latest.destinationNumber}")
     Text(DateFormat.getDateTimeInstance().format(Date(latest.receivedAt)),
      style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
    TextButton(onClick = onHistory) { Text("مشاهده سوابق") }
   }
  }
  Card(Modifier.fillMaxWidth()) {
   Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
    Text("مدیریت و بررسی", style = MaterialTheme.typography.titleMedium)
    TextButton(onClick = onRules) { Text("مدیریت قوانین") }
    TextButton(onClick = onPermissions) { Text("بررسی مجوزهای SMS") }
    TextButton(onClick = onSimulation) { Text("شبیه‌سازی قانون بدون ارسال واقعی") }
   }
  }
  Text("هزینه پیامک ارسالی ممکن است طبق تعرفه اپراتور محاسبه شود.",
   style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
 }
}

@Composable
private fun MetricCard(
 label: String,
 value: String,
 icon: androidx.compose.ui.graphics.vector.ImageVector,
 modifier: Modifier
) {
 Card(modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)) {
  Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
   Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
   Text(value, style = MaterialTheme.typography.headlineSmall)
   Text(label, style = MaterialTheme.typography.labelMedium)
  }
 }
}
