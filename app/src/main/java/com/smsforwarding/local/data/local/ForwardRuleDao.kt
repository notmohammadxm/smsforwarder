package com.smsforwarding.local.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ForwardRuleDao {
 @Query("SELECT * FROM forward_rules ORDER BY updatedAt DESC")
 fun observeAll(): Flow<List<ForwardRule>>
 @Query("SELECT * FROM forward_rules ORDER BY updatedAt DESC")
 suspend fun getAll(): List<ForwardRule>
 @Query("SELECT * FROM forward_rules WHERE enabled = 1 ORDER BY id")
 suspend fun getEnabled(): List<ForwardRule>
 @Query("SELECT * FROM forward_rules WHERE id = :id LIMIT 1")
 suspend fun getById(id: Long): ForwardRule?
 @Insert(onConflict = OnConflictStrategy.ABORT)
 suspend fun insert(rule: ForwardRule): Long
 @Update suspend fun update(rule: ForwardRule)
 @Delete suspend fun delete(rule: ForwardRule)
 @Query("UPDATE forward_rules SET enabled = :enabled, updatedAt = :updatedAt WHERE id = :id")
 suspend fun setEnabled(id: Long, enabled: Boolean, updatedAt: Long)
}
