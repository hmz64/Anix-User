package com.anix.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anix.app.core.theme.*

@Composable
fun NeoChip(
    text: String,
    selected: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    selectedColor: Color = Primary,
    unselectedColor: Color = Surface
) {
    val bgColor by animateColorAsState(
        targetValue = if (selected) selectedColor else Color.White.copy(alpha = 0.08f),
        animationSpec = tween(250),
        label = "chip_bg"
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) selectedColor else GlassBorder,
        animationSpec = tween(250),
        label = "chip_border"
    )
    val txtColor by animateColorAsState(
        targetValue = if (selected) Color.White else Color(0xB3FFFFFF),
        animationSpec = tween(250),
        label = "chip_text"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(50.dp))
            .background(bgColor.copy(alpha = if (selected) 1f else 0.08f))
            .border(1.dp, borderColor, RoundedCornerShape(50.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(
            text = text,
            color = txtColor,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 13.sp
        )
    }
}
