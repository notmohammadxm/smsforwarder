package com.smsforwarding.local.ui
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
 primary = Color(0xFF6750A4), onPrimary = Color.White, secondary = Color(0xFF4B635A),
 tertiary = Color(0xFF7A526F), background = Color(0xFFF8F6FC),
 surface = Color(0xFFFFFBFF), error = Color(0xFFBA1A1A)
)
private val DarkColors = darkColorScheme(
 primary = Color(0xFFD0BCFF), onPrimary = Color(0xFF381E72), secondary = Color(0xFFB1CCC0),
 tertiary = Color(0xFFE7B9D8), background = Color(0xFF141218), surface = Color(0xFF1D1B20),
 error = Color(0xFFFFB4AB)
)
@Composable
fun SmsForwardingTheme(content: @Composable () -> Unit) {
 MaterialTheme(colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors, content = content)
}
