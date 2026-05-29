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
    val error: String? = null,
    val posts: List<SocialPost> = emptyList(),
    val isCreatingPost: Boolean = false
)

class SocialFeedViewModel : ViewModel() {
    private val repo = ServiceLocator.getSocialRepository()

    private val _uiState = MutableStateFlow(SocialFeedUiState())
    val uiState: StateFlow<SocialFeedUiState> = _uiState.asStateFlow()

    init {
        loadFeed()
    }

    fun loadFeed() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            repo.getSocialFeed().onSuccess { posts ->
                _uiState.value = _uiState.value.copy(posts = posts, isLoading = false)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun createPost(content: String, image: String? = null) {
        _uiState.value = _uiState.value.copy(isCreatingPost = true)
        viewModelScope.launch {
            repo.createPost(content, image).onSuccess {
                loadFeed()
            }.onFailure {
                _uiState.value = _uiState.value.copy(isCreatingPost = false)
            }
        }
    }
}
