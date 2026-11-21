package com.smsforwarding.local.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ForwardLogDao {
 @Query("SELECT * FROM forward_logs ORDER BY receivedAt DESC LIMIT 250")
 fun observeLatest(): Flow<List<ForwardLog>>
 @Query("SELECT * FROM forward_logs ORDER BY receivedAt DESC LIMIT 250")
 suspend fun getLatest(): List<ForwardLog>
 @Insert(onConflict = OnConflictStrategy.IGNORE)
 suspend fun insertIfAbsent(log: ForwardLog): Long

 @Query("""
  UPDATE forward_logs SET status = 'SENDING',
   sentPartCount = 0, sentPartTotal = :total, sentPartFailureCount = 0,
   deliveredPartCount = 0, deliveredPartTotal = :total, deliveredPartFailureCount = 0,
   errorCode = NULL, errorMessage = NULL
  WHERE id = :id
 """)
 suspend fun prepareSend(id: Long, total: Int)

 @Query("""
  UPDATE forward_logs SET
   sentPartCount = sentPartCount + 1,
   sentPartFailureCount = sentPartFailureCount + :failed,
   status = CASE WHEN sentPartCount + 1 >= sentPartTotal
      THEN CASE WHEN sentPartFailureCount + :failed > 0 THEN 'FAILED' ELSE 'SENT' END
      ELSE 'SENDING' END,
   sentAt = CASE WHEN sentPartCount + 1 >= sentPartTotal THEN :now ELSE sentAt END,
   errorCode = CASE WHEN :failed = 1 THEN :errorCode ELSE errorCode END,
   errorMessage = CASE WHEN :failed = 1 THEN :errorMessage ELSE errorMessage END
  WHERE id = :id
 """)
 suspend fun recordSentPart(id: Long, failed: Int, now: Long, errorCode: Int?, errorMessage: String?)

 @Query("""
  UPDATE forward_logs SET
   deliveredPartCount = deliveredPartCount + 1,
   deliveredPartFailureCount = deliveredPartFailureCount + :failed,
   status = CASE WHEN deliveredPartCount + 1 >= deliveredPartTotal
      THEN CASE WHEN deliveredPartFailureCount + :failed > 0
           THEN 'DELIVERY_FAILED' ELSE 'DELIVERED' END
      ELSE status END,
   errorCode = CASE WHEN :failed = 1 THEN :errorCode ELSE errorCode END,
   errorMessage = CASE WHEN :failed = 1 THEN :errorMessage ELSE errorMessage END
  WHERE id = :id AND sentPartTotal > 0
 """)
 suspend fun recordDeliveredPart(id: Long, failed: Int, errorCode: Int?, errorMessage: String?)

 @Query("""
  UPDATE forward_logs SET status = :status, errorCode = :errorCode, errorMessage = :errorMessage
  WHERE id = :id
 """)
 suspend fun setFailure(id: Long, status: ForwardStatus, errorCode: Int?, errorMessage: String?)

 @Query("UPDATE forward_logs SET status = 'BLOCKED_SENSITIVE', errorMessage = :message WHERE id = :id")
 suspend fun markSensitiveBlocked(id: Long, message: String)
}
