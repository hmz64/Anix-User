package com.anix.app.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.anix.app.ui.screens.player.PlayerViewModel
import kotlin.math.roundToInt

@Composable
fun FloatingMiniPlayer(viewModel: PlayerViewModel) {
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }

    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenHeightDp = configuration.screenHeightDp.dp

    val dismissThresholdPx = with(density) { (screenHeightDp * 0.75f).toPx() }
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val cardWidthPx = with(density) { 280.dp.toPx() }
    val cardHeightPx = with(density) { 64.dp.toPx() }

    val snapOffsetX = remember(offsetX) {
        val cardCenter = screenWidthPx - cardWidthPx / 2 + offsetX
        if (cardCenter < screenWidthPx / 2) {
            -(screenWidthPx - cardWidthPx - with(density) { 24.dp.toPx() })
        } else {
            0f
        }
    }

    val animatedOffsetX by animateFloatAsState(
        targetValue = snapOffsetX,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "snapX"
    )

    val dismissProgress = (offsetY / dismissThresholdPx).coerceIn(0f, 1f)
    val cardAlpha = 1f - (dismissProgress * 0.8f)

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
            modifier = Modifier
                .padding(bottom = 80.dp, end = 12.dp)
                .width(280.dp)
                .height(64.dp)
                .offset {
                    IntOffset(animatedOffsetX.roundToInt(), offsetY.roundToInt())
                }
                .graphicsLayer { alpha = cardAlpha }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            if (offsetY > dismissThresholdPx) {
                                viewModel.closeMiniPlayer()
                                return@detectDragGestures
                            }
                            val center = screenWidthPx - cardWidthPx / 2 + offsetX
                            offsetX = if (center < screenWidthPx / 2) {
                                -(screenWidthPx - cardWidthPx - with(density) { 24.dp.toPx() })
                            } else {
                                0f
                            }
                            offsetY = 0f
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                        }
                    )
                }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.DarkGray)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = viewModel.currentTitle,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = { viewModel.togglePlayPause() }) {
                    Icon(
                        imageVector = if (viewModel.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (viewModel.isPlaying) "Pause" else "Play",
                        tint = Color.White
                    )
                }

                IconButton(onClick = { viewModel.closeMiniPlayer() }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
