package com.smsforwarding.local.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

enum class MatchMode { OR, AND }
enum class MatchType { CONTAINS, WHOLE_WORD }

@Entity(
 tableName = "forward_rules",
 indices = [Index(value = ["enabled"]), Index(value = ["sourceNumber"])]
)
data class ForwardRule(
 @PrimaryKey(autoGenerate = true) val id: Long = 0,
 val name: String,
 val sourceNumber: String,
 val keywords: List<String>,
 val matchMode: MatchMode,
 val matchType: MatchType,
 val destinationNumber: String,
 val enabled: Boolean,
 val includeSenderInfo: Boolean,
 val createdAt: Long,
 val updatedAt: Long
)
