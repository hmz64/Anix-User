package com.anix.app.ui.screens.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anix.app.core.di.ServiceLocator
import com.anix.app.data.models.Notification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class NotificationsUiState(
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val notifications: List<Notification> = emptyList(),
    val currentPage: Int = 1,
    val hasMore: Boolean = true
)

class NotificationsViewModel : ViewModel() {
    private val repo = ServiceLocator.getNotificationRepository()
    private val perPage = 20

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null, currentPage = 1)
        viewModelScope.launch {
            repo.getNotifications(page = 1, limit = perPage)
                .onSuccess { notifs ->
                    _uiState.value = _uiState.value.copy(
                        notifications = notifs, isLoading = false,
                        hasMore = notifs.size >= perPage
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
                }
        }
    }

    fun loadMore() {
        if (_uiState.value.isLoadingMore || !_uiState.value.hasMore) return
        _uiState.value = _uiState.value.copy(isLoadingMore = true)
        val nextPage = _uiState.value.currentPage + 1
        viewModelScope.launch {
            repo.getNotifications(page = nextPage, limit = perPage)
                .onSuccess { notifs ->
                    val all = _uiState.value.notifications + notifs
                    _uiState.value = _uiState.value.copy(
                        notifications = all, isLoadingMore = false,
                        currentPage = nextPage, hasMore = notifs.size >= perPage
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(isLoadingMore = false)
                }
        }
    }

    fun markAllRead() {
        val updated = _uiState.value.notifications.map { it.copy(read = true) }
        _uiState.value = _uiState.value.copy(notifications = updated)
        viewModelScope.launch {
            repo.markAllRead()
        }
    }
}