package com.anix.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.anix.app.core.theme.Error
import com.anix.app.core.theme.TextMuted
import com.anix.app.core.util.liquidGlass

@Composable
fun ErrorState(
    message: String,
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .liquidGlass(
                shape = RoundedCornerShape(16.dp),
                blurRadius = 20f,
                alpha = 0.06f,
                showGlow = false
            )
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Oops!",
            style = androidx.compose.material3.MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = Error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = TextMuted
        )
        Spacer(modifier = Modifier.height(24.dp))
        if (onRetry != null) {
            NeoButton(
                text = "Retry",
                onClick = onRetry
            )
        }
    }
}
