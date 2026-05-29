package com.anix.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.anix.app.data.models.HeatwavePoint

@Composable
fun AdvancedPlayerTimeline(
    totalDurationMs: Long,
    currentPositionMs: Long,
    bufferedPositionMs: Long,
    heatwaveData: List<HeatwavePoint>,
    onSeekPerformed: (Long) -> Unit
) {
    var isDragging by remember { mutableStateOf(false) }
    var dragFraction by remember { mutableStateOf(0f) }
    var isFineTuning by remember { mutableStateOf(false) }
    var timelineWidthPx by remember { mutableStateOf(1f) }

    val activeFraction = if (isDragging) dragFraction else {
        if (totalDurationMs > 0) currentPositionMs.toFloat() / totalDurationMs else 0f
    }
    val bufferFraction = if (totalDurationMs > 0) bufferedPositionMs.toFloat() / totalDurationMs else 0f
    val peakPoint = remember(heatwaveData) { heatwaveData.maxByOrNull { it.score } }

    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Box(modifier = Modifier.fillMaxWidth().height(24.dp)) {
            peakPoint?.let {
                Text(
                    text = "\u25B2 Paling banyak diputar",
                    color = Color.Yellow,
                    fontSize = 10.sp,
                    modifier = Modifier.offset(x = with(LocalDensity.current) {
                        (timelineWidthPx * it.fraction).toDp() - 50.dp
                    })
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            isDragging = true
                            dragFraction = (offset.x / timelineWidthPx).coerceIn(0f, 1f)
                        },
                        onDragEnd = {
                            isDragging = false
                            onSeekPerformed((dragFraction * totalDurationMs).toLong())
                            isFineTuning = false
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            dragFraction = (change.position.x / timelineWidthPx).coerceIn(0f, 1f)
                            if (dragAmount.y < -15f) isFineTuning = true
                            if (dragAmount.y > 15f) isFineTuning = false
                        }
                    )
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                timelineWidthPx = size.width
                val midY = size.height / 2f
                val trackHeight = 4.dp.toPx()
                val width = size.width

                if (heatwaveData.size >= 2) {
                    val path = Path().apply {
                        moveTo(0f, size.height)
                        heatwaveData.forEach { pt ->
                            lineTo(width * pt.fraction, size.height - pt.score * (size.height * 0.6f))
                        }
                        lineTo(width, size.height)
                        close()
                    }
                    drawPath(path, color = Color.Red.copy(alpha = 0.2f))
                }

                drawLine(Color.DarkGray, Offset(0f, midY), Offset(width, midY), trackHeight)
                drawLine(Color.LightGray, Offset(0f, midY), Offset(width * bufferFraction, midY), trackHeight)
                drawLine(Color.Red, Offset(0f, midY), Offset(width * activeFraction, midY), trackHeight)
                drawCircle(
                    color = Color.Red,
                    radius = if (isDragging) 9.dp.toPx() else 5.dp.toPx(),
                    center = Offset(width * activeFraction, midY)
                )
            }
        }

        AnimatedVisibility(visible = isDragging) {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                val seekTargetSec = ((activeFraction * totalDurationMs) / 1000).toLong()
                Text(
                    text = "${seekTargetSec / 60}:${"%02d".format(seekTargetSec % 60)}",
                    color = Color.White,
                    fontSize = 13.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                if (isFineTuning) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Fine-Tune Mode \u2014 geser bawah untuk keluar",
                        color = Color.Gray, fontSize = 10.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    LazyRow(
                        modifier = Modifier.fillMaxWidth().height(56.dp).padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        val frameCount = 20
                        items(frameCount) { i ->
                            val color = if (i % 2 == 0) Color(0xFF1A237E) else Color(0xFF0D47A1)
                            Canvas(modifier = Modifier.width(72.dp).fillMaxHeight()) {
                                drawRect(color = color)
                            }
                        }
                    }
                }
            }
        }
    }
}
