package com.anix.app.ui.screens.player

import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.anix.app.core.theme.Background
import com.anix.app.core.theme.BorderBlack
import com.anix.app.core.theme.Primary
import com.anix.app.ui.components.LoadingIndicator
import kotlinx.coroutines.delay

@Composable
fun VideoPlayerScreen(
    episodeId: String,
    onBack: () -> Unit,
    viewModel: VideoPlayerViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showControls by remember { mutableStateOf(true) }
    var controlsJob by remember { mutableStateOf(false) }

    // Immersive mode
    val window = remember {
        (context as? android.app.Activity)?.window
    }
    LaunchedEffect(Unit) {
        window?.let { w ->
            w.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            WindowCompat.setDecorFitsSystemWindows(w, false)
            WindowInsetsControllerCompat(w, w.decorView).let { ctrl ->
                ctrl.hide(WindowInsetsCompat.Type.systemBars())
                ctrl.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            window?.let { w ->
                w.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                WindowCompat.setDecorFitsSystemWindows(w, true)
                WindowInsetsControllerCompat(w, w.decorView).show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    LaunchedEffect(episodeId) {
        viewModel.loadEpisode(episodeId)
    }

    // Auto-hide controls
    LaunchedEffect(showControls) {
        if (showControls) {
            delay(3000)
            showControls = false
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black).clickable {
        showControls = !showControls
    }) {
        if (uiState.isLoading) {
            LoadingIndicator()
        } else if (uiState.videoUrl.isNotEmpty()) {
            val exoPlayer = remember {
                ExoPlayer.Builder(context).build().apply {
                    playWhenReady = true
                    addListener(object : Player.Listener {
                        override fun onIsPlayingChanged(isPlaying: Boolean) {
                            viewModel.setPlaying(isPlaying)
                            viewModel.updateProgress(episodeId, currentPosition, false)
                        }
                    })
                }
            }

            LaunchedEffect(uiState.videoUrl) {
                val mediaItem = MediaItem.fromUri(uiState.videoUrl)
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
            }

            DisposableEffect(Unit) {
                onDispose {
                    exoPlayer.release()
                }
            }

            AndroidView(
                factory = {
                    PlayerView(context).apply {
                        player = exoPlayer
                        useController = false
                        setShowNextButton(false)
                        setShowPreviousButton(false)
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Custom Controls Overlay
            if (showControls) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                ) {
                    // Back button
                    Text(
                        text = "← Back",
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                            .background(Color.White, RoundedCornerShape(6.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .clickable {
                                exoPlayer.release()
                                onBack()
                            },
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelLarge
                    )

                    // Center controls
                    Row(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .clickable(enabled = false) {},
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FastRewind,
                            contentDescription = "Rewind 10s",
                            modifier = Modifier
                                .size(48.dp)
                                .clickable {
                                    exoPlayer.seekTo((exoPlayer.currentPosition - 10000).coerceAtLeast(0))
                                },
                            tint = Color.White
                        )
                        Icon(
                            imageVector = if (exoPlayer.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (exoPlayer.isPlaying) "Pause" else "Play",
                            modifier = Modifier
                                .size(64.dp)
                                .clickable {
                                    if (exoPlayer.isPlaying) {
                                        exoPlayer.pause()
                                    } else {
                                        exoPlayer.play()
                                    }
                                },
                            tint = Color.White
                        )
                        Icon(
                            imageVector = Icons.Filled.FastForward,
                            contentDescription = "Forward 10s",
                            modifier = Modifier
                                .size(48.dp)
                                .clickable {
                                    exoPlayer.seekTo((exoPlayer.currentPosition + 10000).coerceAtMost(exoPlayer.duration))
                                },
                            tint = Color.White
                        )
                    }

                    // Bottom controls (progress)
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Slider(
                            value = if (exoPlayer.duration > 0) (exoPlayer.currentPosition.toFloat() / exoPlayer.duration.toFloat()).coerceIn(0f, 1f) else 0f,
                            onValueChange = { fraction ->
                                exoPlayer.seekTo((fraction * exoPlayer.duration).toLong())
                            },
                            colors = SliderDefaults.colors(
                                thumbColor = Primary,
                                activeTrackColor = Primary,
                                inactiveTrackColor = Color.Gray.copy(alpha = 0.5f)
                            )
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = formatTime(exoPlayer.currentPosition),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White
                            )
                            Text(
                                text = formatTime(exoPlayer.duration),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

private fun formatTime(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}
