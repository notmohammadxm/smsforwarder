package com.smsforwarding.local.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.smsforwarding.local.data.local.ForwardRule
import com.smsforwarding.local.data.local.MatchMode
import com.smsforwarding.local.data.local.MatchType

@Composable
fun RulesScreen(
 rules: List<ForwardRule>,
 onAdd: () -> Unit,
 onEdit: (ForwardRule) -> Unit,
 onToggle: (Long, Boolean) -> Unit,
 onDelete: (ForwardRule) -> Unit
) {
 var deleting by remember { mutableStateOf<ForwardRule?>(null) }
 Column(Modifier.fillMaxSize().padding(16.dp)) {
  Row(verticalAlignment = Alignment.CenterVertically) {
   Column(Modifier.weight(1f)) {
    Text("قوانین فورواردینگ", style = MaterialTheme.typography.headlineSmall)
    Text("${rules.size} قانون تعریف‌شده", color = MaterialTheme.colorScheme.onSurfaceVariant)
   }
   Button(onClick = onAdd) {
    Icon(Icons.Default.Add, contentDescription = null)
    Text(" افزودن")
   }
  }
  Spacer(Modifier.height(12.dp))
  if (rules.isEmpty()) {
   Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    Text("هنوز قانونی ندارید. برای شروع یک قانون بسازید.")
   }
  } else {
   LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
    items(rules, key = { it.id }) { rule ->
     ElevatedCard(Modifier.fillMaxWidth()) {
      Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
       Row(verticalAlignment = Alignment.CenterVertically) {
        Text(rule.name, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
        Switch(checked = rule.enabled, onCheckedChange = { onToggle(rule.id, it) })
       }
       Text("فرستنده: ${rule.sourceNumber}", style = MaterialTheme.typography.bodyMedium)
       Text("مقصد: ${rule.destinationNumber}", style = MaterialTheme.typography.bodyMedium)
       Text(
        if (rule.keywords.isEmpty()) "بدون فیلتر کلیدواژه"
        else "کلیدواژه‌ها: ${rule.keywords.joinToString("، ")} · ${if (rule.matchMode == MatchMode.OR) "هرکدام" else "همه"}",
        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant
       )
       Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
        IconButton(onClick = { onEdit(rule) }) {
         Icon(Icons.Default.Edit, contentDescription = "ویرایش")
        }
        IconButton(onClick = { deleting = rule }) {
         Icon(Icons.Default.Delete, contentDescription = "حذف")
        }
       }
      }
     }
    }
   }
  }
 }
 deleting?.let { rule ->
  AlertDialog(
   onDismissRequest = { deleting = null },
   title = { Text("حذف قانون") },
   text = { Text("قانون «${rule.name}» حذف شود؟") },
   confirmButton = {
    TextButton(onClick = { onDelete(rule); deleting = null }) { Text("حذف") }
   },
   dismissButton = { TextButton(onClick = { deleting = null }) { Text("انصراف") } }
  )
 }
}

@Composable
fun RuleEditorScreen(
 rule: ForwardRule?,
 isLoading: Boolean,
 onBack: () -> Unit,
 onSave: (ForwardRule) -> Unit
) {
 var name by remember(rule?.id) { mutableStateOf(rule?.name.orEmpty()) }
 var source by remember(rule?.id) { mutableStateOf(rule?.sourceNumber.orEmpty()) }
 var keywords by remember(rule?.id) { mutableStateOf(rule?.keywords?.joinToString(", ").orEmpty()) }
 var destination by remember(rule?.id) { mutableStateOf(rule?.destinationNumber.orEmpty()) }
 var mode by remember(rule?.id) { mutableStateOf(rule?.matchMode ?: MatchMode.OR) }
 var type by remember(rule?.id) { mutableStateOf(rule?.matchType ?: MatchType.CONTAINS) }
 var enabled by remember(rule?.id) { mutableStateOf(rule?.enabled ?: true) }
 var includeSender by remember(rule?.id) { mutableStateOf(rule?.includeSenderInfo ?: false) }
 var error by remember { mutableStateOf<String?>(null) }

 LaunchedEffect(rule?.id) {
  if (rule != null) {
   name = rule.name; source = rule.sourceNumber
   keywords = rule.keywords.joinToString(", "); destination = rule.destinationNumber
   mode = rule.matchMode; type = rule.matchType
   enabled = rule.enabled; includeSender = rule.includeSenderInfo
  }
 }
 if (isLoading) {
  Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
  return
 }
 Column(
  Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp),
  verticalArrangement = Arrangement.spacedBy(12.dp)
 ) {
  Row(verticalAlignment = Alignment.CenterVertically) {
   Text(if (rule == null) "قانون جدید" else "ویرایش قانون", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.weight(1f))
   TextButton(onClick = onBack) { Text("بازگشت") }
  }
  OutlinedTextField(name, { name = it }, label = { Text("نام قانون") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
  OutlinedTextField(source, { source = it }, label = { Text("شماره فرستنده مبدأ") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
  OutlinedTextField(
   keywords, { keywords = it },
   label = { Text("کلیدواژه‌ها (با ویرگول جدا کنید)") },
   supportingText = { Text("خالی باشد، همه پیام‌های فرستنده منطبق می‌شوند.") },
   modifier = Modifier.fillMaxWidth(), minLines = 2
  )
  Text("منطق کلیدواژه", style = MaterialTheme.typography.titleSmall)
  Row(verticalAlignment = Alignment.CenterVertically) {
   RadioButton(mode == MatchMode.OR, { mode = MatchMode.OR })
   Text("OR — تطبیق هرکدام", Modifier.clickable { mode = MatchMode.OR })
   Spacer(Modifier.width(16.dp))
   RadioButton(mode == MatchMode.AND, { mode = MatchMode.AND })
   Text("AND — تطبیق همه", Modifier.clickable { mode = MatchMode.AND })
  }
  Text("نوع تطبیق", style = MaterialTheme.typography.titleSmall)
  Row(verticalAlignment = Alignment.CenterVertically) {
   RadioButton(type == MatchType.CONTAINS, { type = MatchType.CONTAINS })
   Text("شامل عبارت", Modifier.clickable { type = MatchType.CONTAINS })
   Spacer(Modifier.width(16.dp))
   RadioButton(type == MatchType.WHOLE_WORD, { type = MatchType.WHOLE_WORD })
   Text("کلمه کامل", Modifier.clickable { type = MatchType.WHOLE_WORD })
  }
  OutlinedTextField(destination, { destination = it }, label = { Text("شماره مقصد") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
  Row(verticalAlignment = Alignment.CenterVertically) {
   Text("فعال بودن این قانون", Modifier.weight(1f))
   Switch(enabled, { enabled = it })
  }
  Row(verticalAlignment = Alignment.CenterVertically) {
   Column(Modifier.weight(1f)) {
    Text("افزودن اطلاعات فرستنده و زمان")
    Text("پیشوند به متن پیامک اضافه می‌شود.", style = MaterialTheme.typography.bodySmall)
   }
   Switch(includeSender, { includeSender = it })
  }
  error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
  Button(
   onClick = {
    if (name.isBlank() || source.isBlank() || destination.isBlank()) {
     error = "نام قانون، فرستنده و مقصد الزامی هستند."
    } else {
     val now = System.currentTimeMillis()
     onSave(ForwardRule(
      id = rule?.id ?: 0L,
      name = name.trim(),
      sourceNumber = source.trim(),
      keywords = keywords.split(',').map { it.trim() }.filter { it.isNotEmpty() }.distinct(),
      matchMode = mode,
      matchType = type,
      destinationNumber = destination.trim(),
      enabled = enabled,
      includeSenderInfo = includeSender,
      createdAt = rule?.createdAt ?: now,
      updatedAt = now
     ))
    }
   },
   modifier = Modifier.fillMaxWidth()
  ) { Text("ذخیره قانون") }
 }
}
