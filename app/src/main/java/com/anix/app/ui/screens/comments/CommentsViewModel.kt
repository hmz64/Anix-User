package com.anix.app.ui.screens.comments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anix.app.core.di.ServiceLocator
import com.anix.app.data.models.Comment
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CommentsUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val comments: List<Comment> = emptyList(),
    val sendingComment: Boolean = false
)

class CommentsViewModel : ViewModel() {
    private val repo = ServiceLocator.getUserRepository()

    private val _uiState = MutableStateFlow(CommentsUiState())
    val uiState: StateFlow<CommentsUiState> = _uiState.asStateFlow()

    fun loadComments(episodeId: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            repo.getEpisodeComments(episodeId).onSuccess { comments ->
                _uiState.value = _uiState.value.copy(comments = comments, isLoading = false)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun sendComment(episodeId: String, content: String) {
        _uiState.value = _uiState.value.copy(sendingComment = true)
        viewModelScope.launch {
            repo.addComment(episodeId, content).onSuccess { comment ->
                val updated = _uiState.value.comments + comment
                _uiState.value = _uiState.value.copy(comments = updated, sendingComment = false)
            }.onFailure {
                _uiState.value = _uiState.value.copy(sendingComment = false)
            }
        }
    }
}
