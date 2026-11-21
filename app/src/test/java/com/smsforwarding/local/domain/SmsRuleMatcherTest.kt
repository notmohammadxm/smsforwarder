package com.smsforwarding.local.domain

import com.smsforwarding.local.data.local.ForwardRule
import com.smsforwarding.local.data.local.MatchMode
import com.smsforwarding.local.data.local.MatchType
import org.junit.Assert.*
import org.junit.Test

class SmsRuleMatcherTest {
 private val matcher = SmsRuleMatcher()
 private fun rule(
  id: Long = 1, source: String = "+989121234567", keywords: List<String> = emptyList(),
  mode: MatchMode = MatchMode.OR, type: MatchType = MatchType.CONTAINS,
  enabled: Boolean = true, destination: String = "+989351112233"
 ) = ForwardRule(id, "test", source, keywords, mode, type, destination, enabled, false, 1L, 1L)

 @Test fun normalizesPersianArabicLettersAndDiacritics() {
  assertEquals("علی کریم", matcher.normalizeText("عَلِي كَريم"))
  assertEquals("سلام دنیا", matcher.normalizeText("سلام   دنیا"))
 }
 @Test fun englishMatchingIsCaseInsensitive() {
  assertEquals("payment received", matcher.normalizeText("PAYMENT   Received"))
 }
 @Test fun orModeMatchesAnyKeyword() {
  val configured = rule(keywords = listOf("invoice", "paid"), mode = MatchMode.OR)
  assertTrue(matcher.matches(configured, configured.sourceNumber, "The invoice has arrived"))
 }
 @Test fun andModeRequiresEveryKeyword() {
  val configured = rule(keywords = listOf("invoice", "paid"), mode = MatchMode.AND)
  assertFalse(matcher.matches(configured, configured.sourceNumber, "The invoice has arrived"))
  assertTrue(matcher.matches(configured, configured.sourceNumber, "Invoice paid"))
 }
 @Test fun containsMatchingFindsSubstrings() {
  val configured = rule(keywords = listOf("bank"), type = MatchType.CONTAINS)
  assertTrue(matcher.matches(configured, configured.sourceNumber, "banking notification"))
 }
 @Test fun wholeWordMatchingRejectsPartOfAnotherWord() {
  val configured = rule(keywords = listOf("bank"), type = MatchType.WHOLE_WORD)
  assertFalse(matcher.matches(configured, configured.sourceNumber, "banking notification"))
  assertTrue(matcher.matches(configured, configured.sourceNumber, "Your BANK is ready"))
 }
 @Test fun numericSenderComparisonIsExactAfterFormattingNormalization() {
  assertTrue(matcher.senderMatches("+98 (912) 123-4567", "+989121234567"))
  assertFalse(matcher.senderMatches("+989121234567", "+989121234568"))
 }
 @Test fun alphanumericSenderComparisonIsCaseInsensitiveAndExact() {
  assertTrue(matcher.senderMatches("Bank-Info", "bank-info"))
  assertFalse(matcher.senderMatches("Bank-Info", "Bank-Info2"))
 }
 @Test fun disabledRuleAndGlobalDisableNeverMatch() {
  val configured = rule(keywords = listOf("paid"))
  assertFalse(matcher.matches(configured.copy(enabled = false), configured.sourceNumber, "paid"))
  assertFalse(matcher.matches(configured, configured.sourceNumber, "paid", globalEnabled = false))
 }
 @Test fun emptyKeywordListMatchesSenderOnly() {
  val configured = rule()
  assertTrue(matcher.matches(configured, configured.sourceNumber, "any message"))
  assertFalse(matcher.matches(configured, "+989121234568", "any message"))
 }
 @Test fun multipleIndependentRulesCanMatchOneMessage() {
  val one = rule(id = 1, keywords = listOf("paid"))
  val two = rule(id = 2, keywords = listOf("invoice"))
  val matched = listOf(one, two).filter { matcher.matches(it, one.sourceNumber, "invoice paid") }
  assertEquals(listOf(1L, 2L), matched.map { it.id })
 }
}
