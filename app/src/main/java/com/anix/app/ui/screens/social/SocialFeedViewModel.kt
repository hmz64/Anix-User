package com.anix.app.ui.screens.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anix.app.core.di.ServiceLocator
import com.anix.app.data.models.SocialPost
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SocialFeedUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val posts: List<SocialPost> = emptyList(),
    val isCreatingPost: Boolean = false,
    val currentPage: Int = 1,
    val hasMore: Boolean = true
)

class SocialFeedViewModel : ViewModel() {
    private val repo = ServiceLocator.getSocialRepository()
    private val perPage = 20

    private val _uiState = MutableStateFlow(SocialFeedUiState())
    val uiState: StateFlow<SocialFeedUiState> = _uiState.asStateFlow()

    init {
        loadFeed()
    }

    fun loadFeed() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null, currentPage = 1)
        viewModelScope.launch {
            repo.getSocialFeed(page = 1, limit = perPage)
                .onSuccess { posts ->
                    _uiState.value = _uiState.value.copy(
                        posts = posts, isLoading = false, hasMore = posts.size >= perPage
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
                }
        }
    }

    fun refresh() {
        _uiState.value = _uiState.value.copy(isRefreshing = true, currentPage = 1)
        viewModelScope.launch {
            repo.getSocialFeed(page = 1, limit = perPage)
                .onSuccess { posts ->
                    _uiState.value = _uiState.value.copy(
                        posts = posts, isRefreshing = false, hasMore = posts.size >= perPage
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(isRefreshing = false)
                }
        }
    }

    fun loadMore() {
        if (_uiState.value.isLoadingMore || !_uiState.value.hasMore) return
        _uiState.value = _uiState.value.copy(isLoadingMore = true)
        val nextPage = _uiState.value.currentPage + 1
        viewModelScope.launch {
            repo.getSocialFeed(page = nextPage, limit = perPage)
                .onSuccess { posts ->
                    val all = _uiState.value.posts + posts
                    _uiState.value = _uiState.value.copy(
                        posts = all, isLoadingMore = false, currentPage = nextPage,
                        hasMore = posts.size >= perPage
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(isLoadingMore = false)
                }
        }
    }

    fun toggleLike(postId: String) {
        val updated = _uiState.value.posts.map { p ->
            if (p.id == postId) {
                p.copy(isLiked = !p.isLiked, likeCount = if (p.isLiked) p.likeCount - 1 else p.likeCount + 1)
            } else p
        }
        _uiState.value = _uiState.value.copy(posts = updated)
        viewModelScope.launch {
            repo.likePost(postId)
        }
    }

    fun createPost(content: String, image: String? = null) {
        _uiState.value = _uiState.value.copy(isCreatingPost = true)
        viewModelScope.launch {
            repo.createPost(content, image)
                .onSuccess {
                    loadFeed()
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(isCreatingPost = false)
                }
        }
    }
}