package com.smsforwarding.local.domain
import org.junit.Assert.assertEquals
import org.junit.Test
class SmsMessageAssemblerTest {
 @Test fun multipartSegmentsAreReassembledInTimestampOrder() {
  val result = SmsMessageAssembler().assemble(listOf(
   SmsSegment("+989121234567", "world", 102L),
   SmsSegment("+989121234567", "hello ", 101L)
  ))
  assertEquals(1, result.size)
  assertEquals("hello world", result.single().body)
  assertEquals(101L, result.single().receivedAt)
 }
 @Test fun differentSendersRemainSeparateMessages() {
  val result = SmsMessageAssembler().assemble(listOf(
   SmsSegment("Bank", "one", 1L), SmsSegment("Service", "two", 2L)
  ))
  assertEquals(2, result.size)
 }
}
