package com.smsforwarding.local.domain

class SensitiveMessageGuard {
 private val digits = "[0-9۰-۹٠-٩]"
 private val englishPatterns = listOf(
  Regex("\\b(?:otp|one[- ]time password|verification code|security code|authentication code|login code|recovery code|2fa|mfa)\\b", RegexOption.IGNORE_CASE),
  Regex("\\b(?:your )?code (?:is )?[:：-]?\\s*$digits{4,8}\\b", RegexOption.IGNORE_CASE),
  Regex("\\b(?:do not|don't|never) share (?:this )?(?:code|password)\\b", RegexOption.IGNORE_CASE)
 )
 private val persianPatterns = listOf(
  Regex("(?:رمز\\s*(?:پویا|یک\\s*بار\\s*مصرف|یکبار\\s*مصرف)|کد\\s*(?:تأیید|تایید|ورود|امنیتی|اعتبارسنجی)|احراز\\s*هویت)"),
  Regex("(?:کد|رمز)\\s*[:：-]?\\s*$digits{4,8}")
 )
 fun isSensitive(message: String): Boolean =
  englishPatterns.any { it.containsMatchIn(message) } ||
   persianPatterns.any { it.containsMatchIn(message) }
}
