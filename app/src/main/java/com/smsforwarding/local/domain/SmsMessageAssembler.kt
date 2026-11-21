package com.smsforwarding.local.domain

data class SmsSegment(val sender: String, val body: String, val timestampMillis: Long)
data class AssembledSms(val sender: String, val body: String, val receivedAt: Long)

class SmsMessageAssembler {
 fun assemble(segments: List<SmsSegment>): List<AssembledSms> =
  segments.mapIndexed { index, part -> index to part }
   .groupBy { it.second.sender.trim().replace(Regex("\\s+"), "").uppercase() }
   .values.map { group ->
    val ordered = group.sortedWith(compareBy<Pair<Int, SmsSegment>> { it.second.timestampMillis }.thenBy { it.first })
     .map { it.second }
    AssembledSms(
     sender = ordered.first().sender,
     body = ordered.joinToString("") { it.body },
     receivedAt = ordered.minOf { it.timestampMillis }
    )
   }
}
