package com.anix.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SkeletonEffect(
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(8.dp)
) {
    val transition = rememberInfiniteTransition(label = "skeleton")
    val shimmerTranslate by transition.animateFloat(
        initialValue = -1000f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    val brush = Brush.linearGradient(
        colors = listOf(
            Color.LightGray.copy(alpha = 0.6f),
            Color.White.copy(alpha = 0.8f),
            Color.LightGray.copy(alpha = 0.6f)
        ),
        start = Offset(shimmerTranslate - 200, 0f),
        end = Offset(shimmerTranslate, 0f)
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(brush, shape)
    )
}

@Composable
fun SkeletonBox(
    width: Dp? = 100.dp,
    height: Dp = 20.dp,
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(4.dp)
) {
    val mod = if (width != null) modifier.width(width).height(height) else modifier.height(height)
    SkeletonEffect(modifier = mod, shape = shape)
}

@Composable
fun SkeletonCircle(size: Dp = 40.dp) {
    SkeletonEffect(
        modifier = Modifier.size(size),
        shape = CircleShape
    )
}

@Composable
fun SkeletonCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SkeletonCircle(size = 48.dp)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            SkeletonBox(width = 150.dp, height = 16.dp)
            Spacer(modifier = Modifier.height(4.dp))
            SkeletonBox(width = 200.dp, height = 12.dp)
        }
    }
}

@Composable
fun SkeletonPostCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SkeletonCircle(size = 36.dp)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                SkeletonBox(width = 120.dp, height = 14.dp)
                Spacer(modifier = Modifier.height(4.dp))
                SkeletonBox(width = 80.dp, height = 10.dp)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        SkeletonBox(width = 300.dp, height = 14.dp)
        Spacer(modifier = Modifier.height(4.dp))
        SkeletonBox(width = 200.dp, height = 14.dp)
        Spacer(modifier = Modifier.height(8.dp))
        SkeletonBox(width = null, height = 200.dp, modifier = Modifier.fillMaxWidth())
    }
}
