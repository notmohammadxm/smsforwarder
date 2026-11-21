package com.smsforwarding.local.domain

import com.smsforwarding.local.data.local.ForwardRule
import com.smsforwarding.local.data.local.MatchMode
import com.smsforwarding.local.data.local.MatchType
import java.text.Normalizer
import java.util.Locale

class SmsRuleMatcher {
 fun matches(rule: ForwardRule, sender: String, body: String, globalEnabled: Boolean = true): Boolean {
  if (!globalEnabled || !rule.enabled) return false
  if (!senderMatches(rule.sourceNumber, sender)) return false
  val keywords = rule.keywords.map(::normalizeText).filter(String::isNotBlank)
  if (keywords.isEmpty()) return true
  val content = normalizeText(body)
  val results = keywords.map { keyword ->
   when (rule.matchType) {
    MatchType.CONTAINS -> content.contains(keyword)
    MatchType.WHOLE_WORD -> containsWholeWord(content, keyword)
   }
  }
  return when (rule.matchMode) {
   MatchMode.OR -> results.any { it }
   MatchMode.AND -> results.all { it }
  }
 }

 fun senderMatches(expected: String, actual: String): Boolean =
  normalizeSender(expected).let { it.isNotBlank() && it == normalizeSender(actual) }

 fun normalizeText(value: String): String {
  val normalized = Normalizer.normalize(value, Normalizer.Form.NFKC)
   .replace('ي', 'ی').replace('ك', 'ک').replace('\u0649', 'ی')
   .replace(Regex("[\\u064B-\\u065F\\u0670\\u06D6-\\u06ED]"), "")
   .replace('\u200c', ' ').lowercase(Locale.ROOT)
  return normalized.replace(Regex("\\s+"), " ").trim()
 }

 fun normalizeSender(value: String): String {
  val input = value.trim()
  if (input.isEmpty()) return ""
  val numericLike = input.all { it.isDigit() || it in "+()-. /\\t" }
  return if (numericLike) input.filter(Char::isDigit).map { digit ->
   val n = digit.digitToIntOrNull() ?: digit.code - '0'.code
   if (n in 0..9) ('0'.code + n).toChar() else digit
  }.joinToString("") else input.replace(Regex("\\s+"), "").uppercase(Locale.ROOT)
 }

 private fun containsWholeWord(content: String, keyword: String): Boolean {
  val expression = Regex("(?<![\\p{L}\\p{N}_])${Regex.escape(keyword)}(?![\\p{L}\\p{N}_])")
  return expression.containsMatchIn(content)
 }
}
