package com.anix.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anix.app.core.theme.BorderBlack
import com.anix.app.core.theme.Primary
import com.anix.app.core.theme.Surface

@Composable
fun NeoChip(
    text: String,
    selected: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    selectedColor: Color = Primary,
    unselectedColor: Color = Surface
) {
    val shape = RoundedCornerShape(20.dp)
    val bgColor = if (selected) selectedColor else unselectedColor
    val textColor = if (selected) Color.White else Color.Black
    Text(
        text = text,
        modifier = modifier
            .background(bgColor, shape)
            .drawBehind {
                drawRoundRect(
                    color = BorderBlack,
                    cornerRadius = CornerRadius(20.dp.toPx(), 20.dp.toPx()),
                    style = Stroke(width = 2.dp.toPx())
                )
            }
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = textColor
    )
}
