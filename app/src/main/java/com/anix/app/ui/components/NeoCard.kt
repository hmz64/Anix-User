package com.anix.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.anix.app.core.util.liquidGlass

@Composable
fun NeoCard(
    modifier:     Modifier   = Modifier,
    onClick:      (() -> Unit)? = null,
    cornerRadius: Dp         = 16.dp,
    showGlow:     Boolean    = false,
    content:      @Composable ColumnScope.() -> Unit
) {
    val clickMod = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(clickMod)
            .liquidGlass(
                shape      = RoundedCornerShape(cornerRadius),
                blurRadius = 24f,
                alpha      = 0.10f,
                showGlow   = showGlow
            )
            .padding(16.dp),
        content = content
    )
}

@Composable
fun NeoCardElevated(
    modifier:     Modifier = Modifier,
    cornerRadius: Dp       = 16.dp,
    content:      @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .liquidGlass(
                shape      = RoundedCornerShape(cornerRadius),
                blurRadius = 28f,
                alpha      = 0.16f,
                showGlow   = true
            )
            .padding(16.dp),
        content = content
    )
}
