package com.anix.app.ui.screens.player

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
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
    var resumeEpisodeId by mutableStateOf<String?>(null)
        private set
    var resumeAnimeId by mutableStateOf<String?>(null)
        private set
    var savedPosition by mutableLongStateOf(0L)
        private set

    fun playMedia(
        mediaItem: MediaItem,
        title: String = "Now Playing",
        positionMs: Long = 0L,
        episodeId: String? = null,
        animeId: String? = null
    ) {
        player.setMediaItem(mediaItem)
        player.prepare()
        if (positionMs > 0) {
            player.seekTo(positionMs)
        }
        player.play()
        currentTitle = title
        isMiniPlayerVisible = true
        isPlaying = true
        resumeEpisodeId = episodeId
        resumeAnimeId = animeId
        savedPosition = 0L
    }

    fun savePositionMs(positionMs: Long) {
        savedPosition = positionMs
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
        resumeEpisodeId = null
        resumeAnimeId = null
    }

    override fun onCleared() {
        player.release()
        super.onCleared()
    }
}
