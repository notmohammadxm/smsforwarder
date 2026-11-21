package com.smsforwarding.local.domain
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
class SensitiveMessageGuardTest {
 private val guard = SensitiveMessageGuard()
 @Test fun recognizesEnglishOtpAndVerificationPhrases() {
  assertTrue(guard.isSensitive("Your OTP is 123456"))
  assertTrue(guard.isSensitive("Your verification code is 123456"))
 }
 @Test fun recognizesPersianVerificationMessages() {
  assertTrue(guard.isSensitive("کد تایید شما: ۱۲۳۴۵۶"))
  assertTrue(guard.isSensitive("رمز پویا: 123456"))
 }
 @Test fun normalBusinessMessagesAreNotBlocked() {
  assertFalse(guard.isSensitive("Your package will arrive tomorrow"))
 }
}
