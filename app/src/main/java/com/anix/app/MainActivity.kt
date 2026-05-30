package com.anix.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.anix.app.core.theme.AnixTheme
import com.anix.app.core.theme.LiquidGlassBackground
import com.anix.app.ui.navigation.AppNavigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AnixTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LiquidGlassBackground)
                ) {
                    AppNavigation()
                }
            }
        }
    }
}
