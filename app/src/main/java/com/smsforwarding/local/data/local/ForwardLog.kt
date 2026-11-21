package com.smsforwarding.local.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class ForwardStatus {
 QUEUED, SENDING, SENT, DELIVERED, FAILED, DELIVERY_FAILED, PERMISSION_DENIED, BLOCKED_SENSITIVE
}

@Entity(
 tableName = "forward_logs",
 indices = [
  Index(value = ["receivedAt"]),
  Index(value = ["messageFingerprint", "destinationNumber"], unique = true)
 ]
)
data class ForwardLog(
 @PrimaryKey(autoGenerate = true) val id: Long = 0,
 val matchingRuleIds: List<Long>,
 val sourceNumber: String,
 val destinationNumber: String,
 val receivedAt: Long,
 val sentAt: Long?,
 val status: ForwardStatus,
 val errorCode: Int?,
 val errorMessage: String?,
 val messageFingerprint: String,
 val sentPartCount: Int = 0,
 val sentPartTotal: Int = 0,
 val sentPartFailureCount: Int = 0,
 val deliveredPartCount: Int = 0,
 val deliveredPartTotal: Int = 0,
 val deliveredPartFailureCount: Int = 0
)
