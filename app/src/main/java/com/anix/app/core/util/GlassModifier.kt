package com.anix.app.core.util

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.dp

fun Modifier.liquidGlass(
    shape:       Shape   = RoundedCornerShape(16.dp),
    blurRadius:  Float   = 24f,
    alpha:       Float   = 0.10f,
    borderAlpha: Float   = 0.25f,
    showGlow:    Boolean = false,
): Modifier = this
    .clip(shape)
    .drawBehind {
        drawIntoCanvas { canvas ->
            val paint = Paint().apply {
                asFrameworkPaint().apply {
                    isAntiAlias = true
                    color = android.graphics.Color.argb((alpha * 255).toInt(), 255, 255, 255)
                    if (showGlow) {
                        setShadowLayer(32f, 0f, 0f, android.graphics.Color.argb(90, 10, 132, 255))
                    }
                }
            }
            canvas.drawRoundRect(
                left    = 0f,
                top     = 0f,
                right   = size.width,
                bottom  = size.height,
                radiusX = 16.dp.toPx(),
                radiusY = 16.dp.toPx(),
                paint   = paint
            )
        }
    }
    .background(Color.White.copy(alpha = alpha), shape)
    .border(1.dp, Color.White.copy(alpha = borderAlpha), shape)
