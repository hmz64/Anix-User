package com.anix.app.ui.screens.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anix.app.core.di.ServiceLocator
import com.anix.app.core.network.ApiClient
import com.anix.app.data.models.AnimeSeries
import com.anix.app.data.models.Comment
import com.anix.app.data.models.Episode
import com.anix.app.data.models.User
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
    val showControls: Boolean = true,
    val anime: AnimeSeries? = null,
    val episode: Episode? = null,
    val episodes: List<Episode> = emptyList(),
    val watchedEpisodeIds: Set<String> = emptySet(),
    val comments: List<Comment> = emptyList(),
    val commentsLoading: Boolean = false,
    val commentText: String = "",
    val submittingComment: Boolean = false,
    val sortMode: String = "top",
    val currentQuality: String = "720p",
    val playbackSpeed: Float = 1.0f,
    val currentUserId: String? = null,
    val currentUserAvatar: String? = null
)

class VideoPlayerViewModel : ViewModel() {
    private val animeRepo = ServiceLocator.getAnimeRepository()
    private val userRepo = ServiceLocator.getUserRepository()
    private val authRepo = ServiceLocator.getAuthRepository()

    private val _uiState = MutableStateFlow(VideoPlayerUiState())
    val uiState: StateFlow<VideoPlayerUiState> = _uiState.asStateFlow()

    private var progressJob: Job? = null

    private var currentEpisodeId: String? = null

    init {
        viewModelScope.launch {
            authRepo.me().onSuccess { user ->
                _uiState.value = _uiState.value.copy(
                    currentUserId = user.id.toString(),
                    currentUserAvatar = user.avatar
                )
            }
        }
    }

    fun loadEpisode(episodeId: String, animeId: String?) {
        if (currentEpisodeId == episodeId && _uiState.value.videoUrl.isNotEmpty()) return
        currentEpisodeId = episodeId
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            animeRepo.getEpisodeStreams(episodeId).onSuccess { streams ->
                val stream = streams.firstOrNull()
                val url = ApiClient.resolveUrl(stream?.url) ?: ""
                val quality = stream?.quality ?: "720p"
                _uiState.value = _uiState.value.copy(
                    videoUrl = url,
                    currentQuality = quality,
                    isLoading = false
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }

        if (animeId != null) {
            loadAnimeDetail(animeId, episodeId)
        }
        loadComments(episodeId, _uiState.value.sortMode)
    }

    private fun loadAnimeDetail(animeId: String, currentEpisodeId: String) {
        viewModelScope.launch {
            animeRepo.getAnimeDetail(animeId).onSuccess { anime ->
                _uiState.value = _uiState.value.copy(
                    anime = anime,
                    episodes = anime.episodes,
                    episode = anime.episodes.find { it.id == currentEpisodeId }
                )
            }
        }
    }

    fun selectEpisode(episode: Episode) {
        _uiState.value = _uiState.value.copy(
            episode = episode,
            isLoading = true,
            error = null,
            comments = emptyList()
        )
        viewModelScope.launch {
            animeRepo.getEpisodeStreams(episode.id).onSuccess { streams ->
                val stream = streams.firstOrNull()
                _uiState.value = _uiState.value.copy(
                    videoUrl = ApiClient.resolveUrl(stream?.url) ?: "",
                    currentQuality = stream?.quality ?: "720p",
                    isLoading = false
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
        loadComments(episode.id, _uiState.value.sortMode)
    }

    fun loadComments(episodeId: String, sortMode: String = "top") {
        _uiState.value = _uiState.value.copy(commentsLoading = true, sortMode = sortMode)
        viewModelScope.launch {
            userRepo.getComments(episodeId).onSuccess { comments ->
                val sorted = if (sortMode == "new") comments.sortedByDescending { it.createdAt } else comments
                _uiState.value = _uiState.value.copy(comments = sorted, commentsLoading = false)
            }.onFailure {
                _uiState.value = _uiState.value.copy(commentsLoading = false)
            }
        }
    }

    fun setCommentText(text: String) {
        _uiState.value = _uiState.value.copy(commentText = text)
    }

    fun submitComment(episodeId: String) {
        val text = _uiState.value.commentText.trim()
        if (text.isEmpty()) return
        _uiState.value = _uiState.value.copy(submittingComment = true)
        viewModelScope.launch {
            userRepo.createComment(episodeId, text).onSuccess { comment ->
                val updated = _uiState.value.comments + comment
                _uiState.value = _uiState.value.copy(
                    comments = updated,
                    commentText = "",
                    submittingComment = false
                )
            }.onFailure {
                _uiState.value = _uiState.value.copy(submittingComment = false)
            }
        }
    }

    fun deleteComment(episodeId: String, commentId: String) {
        viewModelScope.launch {
            userRepo.deleteComment(episodeId, commentId).onSuccess {
                val updated = _uiState.value.comments.filter { it.id != commentId }
                _uiState.value = _uiState.value.copy(comments = updated)
            }
        }
    }

    fun submitReport(type: String, message: String, episodeId: String) {
        viewModelScope.launch {
            userRepo.submitReport(type, message, episodeId)
        }
    }

    fun updateProgress(episodeId: String, position: Long, completed: Boolean = false) {
        viewModelScope.launch {
            userRepo.updateProgress(episodeId, position, completed)
        }
    }

    fun startProgressUpdates(episodeId: String) {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (true) {
                delay(10_000)
                val pos = _uiState.value.currentPosition
                if (pos > 0) {
                    userRepo.updateProgress(episodeId, pos)
                }
            }
        }
    }

    fun stopProgressUpdates() {
        progressJob?.cancel()
        progressJob = null
    }

    fun setPlaying(playing: Boolean) {
        _uiState.value = _uiState.value.copy(isPlaying = playing)
    }

    fun setCurrentPosition(pos: Long) {
        _uiState.value = _uiState.value.copy(currentPosition = pos)
    }

    fun setDuration(dur: Long) {
        _uiState.value = _uiState.value.copy(duration = dur)
    }

    fun setFullscreen(fullscreen: Boolean) {
        _uiState.value = _uiState.value.copy(isFullscreen = fullscreen)
    }

    fun setShowControls(show: Boolean) {
        _uiState.value = _uiState.value.copy(showControls = show)
    }

    fun setPlaybackSpeed(speed: Float) {
        _uiState.value = _uiState.value.copy(playbackSpeed = speed)
    }

    fun setCurrentQuality(quality: String) {
        _uiState.value = _uiState.value.copy(currentQuality = quality)
    }

    fun setSortMode(mode: String) {
        _uiState.value = _uiState.value.copy(sortMode = mode)
    }

    fun markWatched(episodeId: String) {
        val updated = _uiState.value.watchedEpisodeIds + episodeId
        _uiState.value = _uiState.value.copy(watchedEpisodeIds = updated)
    }

    override fun onCleared() {
        super.onCleared()
        progressJob?.cancel()
    }
}
