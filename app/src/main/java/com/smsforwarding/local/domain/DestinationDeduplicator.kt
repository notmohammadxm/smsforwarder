package com.smsforwarding.local.domain

import com.smsforwarding.local.data.local.ForwardRule
import java.util.Locale

data class DestinationMatch(
 val destinationNumber: String,
 val matchingRuleIds: List<Long>,
 val includeSenderInfo: Boolean
)

class DestinationDeduplicator {
 fun group(rules: List<ForwardRule>): List<DestinationMatch> =
  rules.groupBy { normalizeDestination(it.destinationNumber) }
   .filterKeys(String::isNotBlank)
   .map { (_, group) ->
    DestinationMatch(
     destinationNumber = group.first().destinationNumber,
     matchingRuleIds = group.map { it.id }.distinct(),
     includeSenderInfo = group.any { it.includeSenderInfo }
    )
   }

 private fun normalizeDestination(value: String): String {
  val input = value.trim()
  return if (input.all { it.isDigit() || it in "+()-. /\\t" }) {
   input.filter(Char::isDigit).map { c ->
    val number = c.digitToIntOrNull() ?: c.code - '0'.code
    if (number in 0..9) ('0'.code + number).toChar() else c
   }.joinToString("")
  } else input.uppercase(Locale.ROOT)
 }
}
