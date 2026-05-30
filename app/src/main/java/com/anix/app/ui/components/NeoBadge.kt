package com.anix.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anix.app.core.theme.*

@Composable
fun NeoBadge(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Primary,
    textColor: Color = Color.White
) {
    val (gradient, borderColor) = when (backgroundColor) {
        Primary, AccentBlue      -> Pair(
            Brush.horizontalGradient(listOf(AccentBlue, Color(0xFF0055CC))),
            AccentBlue.copy(alpha = 0.6f)
        )
        AccentOrange             -> Pair(
            Brush.horizontalGradient(listOf(AccentOrange, Color(0xFFCC4400))),
            AccentOrange.copy(alpha = 0.6f)
        )
        Color.Red, GlassError    -> Pair(
            Brush.horizontalGradient(listOf(GlassError, Color(0xFFCC2200))),
            GlassError.copy(alpha = 0.6f)
        )
        Color(0xFF4CAF50), GlassSuccess -> Pair(
            Brush.horizontalGradient(listOf(GlassSuccess, Color(0xFF1A8C3A))),
            GlassSuccess.copy(alpha = 0.6f)
        )
        Color.DarkGray           -> Pair(
            Brush.horizontalGradient(listOf(Color(0xFF444444), Color(0xFF222222))),
            Color(0x66FFFFFF)
        )
        else                     -> Pair(
            Brush.horizontalGradient(listOf(backgroundColor, backgroundColor.copy(red = (backgroundColor.red * 0.7f).coerceIn(0f, 1f), green = (backgroundColor.green * 0.7f).coerceIn(0f, 1f), blue = (backgroundColor.blue * 0.7f).coerceIn(0f, 1f)))),
            backgroundColor.copy(alpha = 0.6f)
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(50.dp))
            .background(gradient)
            .border(1.dp, borderColor, RoundedCornerShape(50.dp))
            .padding(horizontal = 10.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            letterSpacing = 0.3.sp
        )
    }
}
