package com.smsforwarding.local.data.repository

import com.smsforwarding.local.data.local.ForwardLog
import com.smsforwarding.local.data.local.ForwardLogDao
import com.smsforwarding.local.data.local.ForwardRule
import com.smsforwarding.local.data.local.ForwardRuleDao
import com.smsforwarding.local.data.local.ForwardStatus
import kotlinx.coroutines.flow.Flow

class ForwardingRepository(private val rules: ForwardRuleDao, private val logs: ForwardLogDao) {
 fun observeRules(): Flow<List<ForwardRule>> = rules.observeAll()
 fun observeLogs(): Flow<List<ForwardLog>> = logs.observeLatest()
 suspend fun getAllRules(): List<ForwardRule> = rules.getAll()
 suspend fun getEnabledRules(): List<ForwardRule> = rules.getEnabled()
 suspend fun getRule(id: Long): ForwardRule? = rules.getById(id)
 suspend fun saveRule(rule: ForwardRule) {
  if (rule.id == 0L) rules.insert(rule) else rules.update(rule)
 }
 suspend fun deleteRule(rule: ForwardRule) = rules.delete(rule)
 suspend fun setRuleEnabled(id: Long, enabled: Boolean, now: Long) =
  rules.setEnabled(id, enabled, now)
 suspend fun addLogIfAbsent(log: ForwardLog): Long = logs.insertIfAbsent(log)
 suspend fun prepareSend(id: Long, total: Int) = logs.prepareSend(id, total)
 suspend fun recordSentPart(id: Long, failed: Int, now: Long, code: Int?, message: String?) =
  logs.recordSentPart(id, failed, now, code, message)
 suspend fun recordDeliveredPart(id: Long, failed: Int, code: Int?, message: String?) =
  logs.recordDeliveredPart(id, failed, code, message)
 suspend fun setFailure(id: Long, status: ForwardStatus, code: Int?, message: String?) =
  logs.setFailure(id, status, code, message)
 suspend fun markSensitiveBlocked(id: Long, message: String) =
  logs.markSensitiveBlocked(id, message)
}
