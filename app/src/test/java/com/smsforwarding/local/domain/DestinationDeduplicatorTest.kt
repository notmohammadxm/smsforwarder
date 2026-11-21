package com.smsforwarding.local.domain

import com.smsforwarding.local.data.local.ForwardRule
import com.smsforwarding.local.data.local.MatchMode
import com.smsforwarding.local.data.local.MatchType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DestinationDeduplicatorTest {
 private fun rule(id: Long, destination: String, include: Boolean = false) = ForwardRule(
  id, "rule$id", "+989121234567", listOf("alert"), MatchMode.OR, MatchType.CONTAINS,
  destination, true, include, 1L, 1L
 )
 @Test fun sameDestinationIsGroupedAndRuleIdsAreCombined() {
  val grouped = DestinationDeduplicator().group(listOf(
   rule(10, "+98 935 111 2233"), rule(11, "+989351112233", include = true),
   rule(12, "+989351112234")
  ))
  assertEquals(2, grouped.size)
  val first = grouped.first { it.destinationNumber == "+98 935 111 2233" }
  assertEquals(listOf(10L, 11L), first.matchingRuleIds)
  assertTrue(first.includeSenderInfo)
 }
}
