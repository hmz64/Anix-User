package com.anix.app

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.Modifier
import com.anix.app.core.theme.AnixTheme
import com.anix.app.core.theme.LiquidGlassBackground
import com.anix.app.ui.navigation.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { }.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            AnixTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LiquidGlassBackground)
                        .navigationBarsPadding()
                ) {
                    AppNavigation()
                }
            }
        }
    }
}
