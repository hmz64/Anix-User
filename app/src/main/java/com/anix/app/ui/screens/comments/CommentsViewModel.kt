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
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val comments: List<Comment> = emptyList(),
    val sendingComment: Boolean = false,
    val currentPage: Int = 1,
    val hasMore: Boolean = true,
    val replyingTo: Comment? = null,
    val reportingCommentId: String? = null
)

class CommentsViewModel : ViewModel() {
    private val repo = ServiceLocator.getUserRepository()
    private val perPage = 30

    private val _uiState = MutableStateFlow(CommentsUiState())
    val uiState: StateFlow<CommentsUiState> = _uiState.asStateFlow()

    fun loadComments(episodeId: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null, currentPage = 1)
        viewModelScope.launch {
            repo.getComments(episodeId).onSuccess { comments ->
                _uiState.value = _uiState.value.copy(
                    comments = comments, isLoading = false,
                    hasMore = comments.size >= perPage
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun loadMore(episodeId: String) {
        if (_uiState.value.isLoadingMore || !_uiState.value.hasMore) return
        _uiState.value = _uiState.value.copy(isLoadingMore = true)
        val nextPage = _uiState.value.currentPage + 1
        viewModelScope.launch {
            repo.getComments(episodeId).onSuccess { newComments ->
                val all = _uiState.value.comments + newComments
                _uiState.value = _uiState.value.copy(
                    comments = all, isLoadingMore = false,
                    currentPage = nextPage, hasMore = newComments.size >= perPage
                )
            }.onFailure {
                _uiState.value = _uiState.value.copy(isLoadingMore = false)
            }
        }
    }

    fun sendComment(episodeId: String, content: String) {
        val parentId = _uiState.value.replyingTo?.id
        _uiState.value = _uiState.value.copy(sendingComment = true, replyingTo = null)
        viewModelScope.launch {
            repo.createComment(episodeId, content, parentId).onSuccess { comment ->
                val updated = _uiState.value.comments + comment
                _uiState.value = _uiState.value.copy(comments = updated, sendingComment = false)
            }.onFailure {
                _uiState.value = _uiState.value.copy(sendingComment = false)
            }
        }
    }

    fun setReplyTo(comment: Comment?) {
        _uiState.value = _uiState.value.copy(replyingTo = comment)
    }

    fun reportComment(episodeId: String, commentId: String, reason: String) {
        _uiState.value = _uiState.value.copy(reportingCommentId = null)
        viewModelScope.launch {
            repo.reportComment(episodeId, commentId, reason)
        }
    }

    fun showReportDialog(commentId: String) {
        _uiState.value = _uiState.value.copy(reportingCommentId = commentId)
    }

    fun dismissReportDialog() {
        _uiState.value = _uiState.value.copy(reportingCommentId = null)
    }
}
