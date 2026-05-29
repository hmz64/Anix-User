package com.anix.app.ui.screens.player

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

class PlayerViewModel(application: Application) : AndroidViewModel(application) {
    val player: ExoPlayer = ExoPlayer.Builder(application).build()

    var isMiniPlayerVisible by mutableStateOf(false)
        private set
    var isPlaying by mutableStateOf(false)
        private set
    var currentTitle by mutableStateOf("Now Playing")
        private set

    fun playMedia(mediaItem: MediaItem, title: String = "Now Playing") {
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
        currentTitle = title
        isMiniPlayerVisible = true
        isPlaying = true
    }

    fun togglePlayPause() {
        if (player.isPlaying) player.pause() else player.play()
        isPlaying = player.isPlaying
    }

    fun closeMiniPlayer() {
        player.stop()
        player.clearMediaItems()
        isMiniPlayerVisible = false
        isPlaying = false
    }

    override fun onCleared() {
        player.release()
        super.onCleared()
    }
}
