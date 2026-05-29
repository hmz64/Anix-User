package com.anix.app.ui.screens.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anix.app.core.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class VideoPlayerUiState(
    val videoUrl: String = "",
    val isLoading: Boolean = true,
    val error: String? = null,
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0,
    val duration: Long = 0,
    val isFullscreen: Boolean = false,
    val showControls: Boolean = true
)

class VideoPlayerViewModel : ViewModel() {
    private val animeRepo = ServiceLocator.getAnimeRepository()
    private val userRepo = ServiceLocator.getUserRepository()

    private val _uiState = MutableStateFlow(VideoPlayerUiState())
    val uiState: StateFlow<VideoPlayerUiState> = _uiState.asStateFlow()

    fun loadEpisode(episodeId: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            animeRepo.resolveStream(episodeId).onSuccess { url ->
                _uiState.value = _uiState.value.copy(videoUrl = url, isLoading = false)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun updateProgress(episodeId: String, position: Long, completed: Boolean = false) {
        viewModelScope.launch {
            userRepo.updateProgress(episodeId, position, completed)
        }
    }

    fun setPlaying(playing: Boolean) {
        _uiState.value = _uiState.value.copy(isPlaying = playing)
    }

    fun setFullscreen(fullscreen: Boolean) {
        _uiState.value = _uiState.value.copy(isFullscreen = fullscreen)
    }

    fun setShowControls(show: Boolean) {
        _uiState.value = _uiState.value.copy(showControls = show)
    }
}
