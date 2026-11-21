package com.smsforwarding.local.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun SettingsScreen(
 blockSensitive: Boolean,
 onProtectionChange: (Boolean) -> Unit,
 onPermissions: () -> Unit,
 onSimulation: () -> Unit
) {
 var showWarning by remember { mutableStateOf(false) }
 Column(Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
  Text("تنظیمات و حریم خصوصی", style = MaterialTheme.typography.headlineSmall)
  ElevatedCard(Modifier.fillMaxWidth()) {
   Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
    Row(verticalAlignment = Alignment.CenterVertically) {
     Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
     Text("  محافظت از پیام‌های حساس", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
     Switch(
      checked = blockSensitive,
      onCheckedChange = { if (it) onProtectionChange(true) else showWarning = true }
     )
    }
    Text("به‌صورت پیش‌فرض پیام‌های مشکوک به OTP، رمز ورود و بازیابی حساب مسدود می‌شوند. متن پیام در تاریخچه ذخیره نمی‌شود.")
   }
  }
  Card(Modifier.fillMaxWidth()) {
   Column(Modifier.padding(16.dp)) {
    Text("ذخیره‌سازی محلی", style = MaterialTheme.typography.titleMedium)
    Text("قوانین، تنظیمات و فراداده تاریخچه فقط در پایگاه‌داده محلی برنامه نگهداری می‌شوند. هیچ سرویس ابری یا ارسال از راه دور وجود ندارد.")
   }
  }
  OutlinedButton(onClick = onPermissions, modifier = Modifier.fillMaxWidth()) { Text("تشخیص مجوزهای پیامک") }
  OutlinedButton(onClick = onSimulation, modifier = Modifier.fillMaxWidth()) { Text("آزمایش موتور تطبیق") }
  Text("ارسال پیامک ممکن است هزینه اپراتور داشته باشد. فقط شماره‌های مشخص‌شده در قوانین پردازش می‌شوند.",
   style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
 }
 if (showWarning) {
  AlertDialog(
   onDismissRequest = { showWarning = false },
   title = { Text("هشدار امنیتی") },
   text = { Text("با خاموش کردن این محافظت، پیام‌های حاوی رمز یک‌بارمصرف، کد تأیید و کدهای ورود نیز در صورت تطبیق قانون به مقصد ارسال می‌شوند. این کار می‌تواند امنیت حساب‌های شما را کاهش دهد. آیا ادامه می‌دهید؟") },
   confirmButton = {
    TextButton(onClick = { onProtectionChange(false); showWarning = false }) { Text("خاموش کردن محافظت") }
   },
   dismissButton = { TextButton(onClick = { showWarning = false }) { Text("انصراف") } }
  )
 }
}

@Composable
fun PermissionDiagnosticsScreen(onBack: () -> Unit) {
 val context = LocalContext.current
 var revision by remember { mutableIntStateOf(0) }
 val requestedPermissions = remember {
  listOf(
   Manifest.permission.RECEIVE_SMS to "دریافت پیامک ورودی",
   Manifest.permission.SEND_SMS to "ارسال پیامک به مقصد قانون"
  )
 }
 val statuses = remember(revision) {
  requestedPermissions.map { (permission, label) ->
   Triple(label, ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED, permission)
  }
 }
 val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
  revision++
 }
 Column(Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
  Row(verticalAlignment = Alignment.CenterVertically) {
   IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "بازگشت") }
   Text("تشخیص مجوزها", style = MaterialTheme.typography.headlineSmall)
  }
  Text("برنامه فقط مجوزهای RECEIVE_SMS و SEND_SMS را درخواست می‌کند؛ READ_SMS استفاده نمی‌شود.")
  statuses.forEach { (label, granted, permission) ->
   ElevatedCard(Modifier.fillMaxWidth()) {
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
     Text(label, style = MaterialTheme.typography.titleMedium)
     Text(if (granted) "مجاز" else "اعطا نشده",
      color = if (granted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
     Text(permission, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
     if (!granted) Text(
      "در بعضی نسخه‌های اندروید، روش‌های نصب یا سیاست‌های دستگاه، مجوزهای SMS ممکن است توسط سیستم یا نصب‌کننده رد شوند. برنامه این محدودیت را دور نمی‌زند.",
      style = MaterialTheme.typography.bodySmall
     )
    }
   }
  }
  Button(
   onClick = { launcher.launch(requestedPermissions.map { it.first }.toTypedArray()) },
   modifier = Modifier.fillMaxWidth()
  ) { Text("درخواست مجوزهای لازم") }
  Text("اگر سیستم اجازه نداد، وضعیت «اعطا نشده» نمایش داده می‌شود و ارسال پیامک موفق فرض نمی‌شود.",
   style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
 }
}

@Composable
fun SimulationScreen(viewModel: MainViewModel, state: AppUiState) {
 val result by viewModel.simulation.collectAsStateWithLifecycle()
 var sender by remember { mutableStateOf("") }
 var body by remember { mutableStateOf("") }
 Column(Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
  Text("شبیه‌سازی قانون", style = MaterialTheme.typography.headlineSmall)
  Text("این صفحه فقط موتور تطبیق را اجرا می‌کند و هیچ پیامکی ارسال نمی‌کند.",
   color = MaterialTheme.colorScheme.onSurfaceVariant)
  OutlinedTextField(sender, { sender = it }, label = { Text("شماره فرستنده آزمایشی") },
   modifier = Modifier.fillMaxWidth(), singleLine = true)
  OutlinedTextField(body, { body = it }, label = { Text("متن پیامک نمونه") },
   modifier = Modifier.fillMaxWidth(), minLines = 4)
  Button(
   onClick = { viewModel.simulate(sender, body) },
   enabled = sender.isNotBlank() && body.isNotBlank(),
   modifier = Modifier.fillMaxWidth()
  ) { Text("اجرای شبیه‌سازی") }
  result?.let { outcome ->
   ElevatedCard(Modifier.fillMaxWidth()) {
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
     Text(
      when {
       !state.globalEnabled -> "فورواردینگ کلی خاموش است؛ هیچ قانونی اجرا نمی‌شود."
       outcome.matchingRules.isEmpty() -> "هیچ قانونی منطبق نشد."
       outcome.blockedAsSensitive -> "قوانین منطبق شدند، اما محافظت پیام حساس مانع ارسال می‌شود."
       else -> "${outcome.matchingRules.size} قانون منطبق شد."
      },
      style = MaterialTheme.typography.titleMedium
     )
     outcome.matchingRules.forEach { rule -> Text("• ${rule.name}: ${rule.destinationNumber}") }
    }
   }
  }
 }
}
