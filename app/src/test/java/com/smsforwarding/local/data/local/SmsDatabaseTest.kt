package com.smsforwarding.local.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SmsDatabaseTest {
 @Test fun storesRulesAndMetadataAndRejectsDuplicateFingerprint() = runBlocking {
  val context = ApplicationProvider.getApplicationContext<Context>()
  val database = Room.inMemoryDatabaseBuilder(context, SmsDatabase::class.java)
   .allowMainThreadQueries().build()
  try {
   val ruleId = database.forwardRuleDao().insert(ForwardRule(
    name = "Bank alerts", sourceNumber = "Bank", keywords = listOf("paid"),
    matchMode = MatchMode.OR, matchType = MatchType.CONTAINS,
    destinationNumber = "+989351112233", enabled = true, includeSenderInfo = false,
    createdAt = 10L, updatedAt = 10L
   ))
   assertNotEquals(0L, ruleId)
   assertEquals("Bank alerts", database.forwardRuleDao().getById(ruleId)?.name)
   val entry = ForwardLog(
    matchingRuleIds = listOf(ruleId), sourceNumber = "Bank", destinationNumber = "+989351112233",
    receivedAt = 11L, sentAt = null, status = ForwardStatus.QUEUED, errorCode = null,
    errorMessage = null, messageFingerprint = "sha256-test-fingerprint"
   )
   assertNotEquals(0L, database.forwardLogDao().insertIfAbsent(entry))
   assertEquals(1, database.forwardLogDao().getLatest().size)
   assertEquals(-1L, database.forwardLogDao().insertIfAbsent(entry))
  } finally {
   database.close()
  }
 }
}
