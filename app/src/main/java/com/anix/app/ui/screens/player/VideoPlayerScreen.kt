package com.anix.app.ui.screens.player

import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.anix.app.ui.components.LoadingIndicator

@Composable
fun VideoPlayerScreen(
    episodeId: String,
    onBack: () -> Unit,
    viewModel: VideoPlayerViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(episodeId) {
        viewModel.loadEpisode(episodeId)
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        if (uiState.isLoading) {
            LoadingIndicator()
        } else if (uiState.videoUrl.isNotEmpty()) {
            val context = androidx.compose.ui.platform.LocalContext.current
            val exoPlayer = remember {
                ExoPlayer.Builder(context).build()
            }

            LaunchedEffect(uiState.videoUrl) {
                val mediaItem = MediaItem.fromUri(uiState.videoUrl)
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
                exoPlayer.playWhenReady = true
            }

            DisposableEffect(Unit) {
                onDispose { exoPlayer.release() }
            }

            AndroidView(
                factory = {
                    PlayerView(context).apply {
                        player = exoPlayer
                        useController = true
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
