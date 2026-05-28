package com.anix.app.ui.screens.player
import androidx.compose.foundation.border

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.anix.app.core.di.ServiceLocator
import com.anix.app.core.theme.Background
import com.anix.app.core.theme.BorderBlack
import com.anix.app.core.theme.Primary
import com.anix.app.core.theme.Surface
import com.anix.app.data.models.Episode
import com.anix.app.data.models.EpisodeStream
import com.anix.app.ui.components.ErrorState
import com.anix.app.ui.components.LoadingIndicator
import com.anix.app.ui.components.NeoButton

@Composable
fun VideoPlayerScreen(
    episodeId: String,
    onBack: () -> Unit
) {
    var episode by remember { mutableStateOf<Episode?>(null) }
    var streams by remember { mutableStateOf<List<EpisodeStream>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableFloatStateOf(0f) }
    var totalDuration by remember { mutableFloatStateOf(100f) }
    var selectedQuality by remember { mutableStateOf("1080p") }
    var showQualitySelector by remember { mutableStateOf(false) }

    LaunchedEffect(episodeId) {
        val repo = ServiceLocator.getAnimeRepository()
        repo.getEpisodeDetail(episodeId).onSuccess {
            episode = it
        }.onFailure { error = it.message }
        repo.getEpisodeStreams(episodeId).onSuccess {
            streams = it
        }
        isLoading = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Video Area (placeholder)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(Color(0xFF1A1A2E)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (isPlaying) {
                        Icon(
                            imageVector = Icons.Default.Pause,
                            contentDescription = "Pause",
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.9f))
                                .border(BorderStroke(2.dp, BorderBlack), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                                .clickable { isPlaying = false },
                            tint = Color.Black
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.9f))
                                .border(BorderStroke(2.dp, BorderBlack), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                                .clickable { isPlaying = true },
                            tint = Color.Black
                        )
                    }
                }

                // Top overlay
                Text(
                    text = episode?.title ?: "Episode",
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                // Back button
                Text(
                    text = "← Back",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .clickable { onBack() },
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            // Controls
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Surface)
                    .border(BorderStroke(2.dp, BorderBlack), RoundedCornerShape(0.dp))
                    .padding(8.dp)
            ) {
                Column {
                    // Seek Bar
                    Slider(
                        value = currentPosition,
                        onValueChange = { currentPosition = it },
                        valueRange = 0f..totalDuration,
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = Primary,
                            activeTrackColor = Primary,
                            inactiveTrackColor = Color.LightGray
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatTime(currentPosition.toInt()),
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = formatTime(totalDuration.toInt()),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Control Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.SkipPrevious, contentDescription = "Previous", tint = Color.Black)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        IconButton(
                            onClick = { isPlaying = !isPlaying },
                            modifier = Modifier
                                .size(48.dp)
                                .background(Primary, RoundedCornerShape(8.dp))
                                .border(BorderStroke(2.dp, BorderBlack), RoundedCornerShape(8.dp))
                        ) {
                            Icon(
                                if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.SkipNext, contentDescription = "Next", tint = Color.Black)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Quality Selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        NeoButton(
                            text = selectedQuality,
                            onClick = { showQualitySelector = !showQualitySelector },
                            backgroundColor = Surface,
                            textColor = Color.Black
                        )
                    }

                    if (showQualitySelector && streams.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            streams.forEach { stream ->
                                Text(
                                    text = stream.quality,
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .background(
                                            if (stream.quality == selectedQuality) Primary else Surface,
                                            RoundedCornerShape(4.dp)
                                        )
                                        .border(BorderStroke(1.dp, BorderBlack), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 12.dp, vertical = 4.dp)
                                        .clickable {
                                            selectedQuality = stream.quality
                                            showQualitySelector = false
                                        },
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (stream.quality == selectedQuality) Color.White else Color.Black
                                )
                            }
                        }
                    }
                }
            }

            // Episode Info
            if (episode != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Background)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Episode ${episode!!.number}: ${episode!!.title}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = episode!!.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        if (isLoading) {
            LoadingIndicator()
        }
    }
}

private fun formatTime(seconds: Int): String {
    val min = seconds / 60
    val sec = seconds % 60
    return String.format("%02d:%02d", min, sec)
}
