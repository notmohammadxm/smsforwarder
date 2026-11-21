package com.smsforwarding.local
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.smsforwarding.local.ui.MainViewModel
import com.smsforwarding.local.ui.SmsForwardingRoot
import com.smsforwarding.local.ui.SmsForwardingTheme

class MainActivity : ComponentActivity() {
 private val viewModel: MainViewModel by viewModels {
  MainViewModel.Factory((application as SmsForwardingApp).container)
 }
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  setContent {
   SmsForwardingTheme {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
     Surface(Modifier.fillMaxSize()) { SmsForwardingRoot(viewModel) }
    }
   }
  }
 }
}
