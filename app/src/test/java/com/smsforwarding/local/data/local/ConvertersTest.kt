package com.smsforwarding.local.data.local

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ConvertersTest {
 @Test fun stringAndRuleIdListsRoundTrip() {
  val converter = Converters()
  assertEquals(listOf("پرداخت", "invoice"), converter.jsonToStrings(converter.stringsToJson(listOf("پرداخت", "invoice"))))
  assertEquals(listOf(4L, 7L, 42L), converter.jsonToLongs(converter.longsToJson(listOf(4L, 7L, 42L))))
 }
 @Test fun enumConvertersRoundTrip() {
  val converter = Converters()
  assertEquals(MatchMode.AND, converter.stringToMatchMode(converter.matchModeToString(MatchMode.AND)))
  assertEquals(MatchType.WHOLE_WORD, converter.stringToMatchType(converter.matchTypeToString(MatchType.WHOLE_WORD)))
  assertEquals(ForwardStatus.DELIVERED, converter.stringToStatus(converter.statusToString(ForwardStatus.DELIVERED)))
 }
}
