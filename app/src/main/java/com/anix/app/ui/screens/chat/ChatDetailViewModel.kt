package com.anix.app.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anix.app.core.di.ServiceLocator
import com.anix.app.data.models.Message
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatDetailUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val messages: List<Message> = emptyList()
)

class ChatDetailViewModel : ViewModel() {
    private val repo = ServiceLocator.getChatRepository()
    private val authRepo = ServiceLocator.getAuthRepository()

    private val _uiState = MutableStateFlow(ChatDetailUiState())
    val uiState: StateFlow<ChatDetailUiState> = _uiState.asStateFlow()

    var currentUserId: String? = null
        private set

    init {
        viewModelScope.launch {
            authRepo.me().onSuccess { user ->
                currentUserId = user.id.toString()
            }
        }
    }

    fun loadMessages(conversationId: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            repo.getConversationMessages(conversationId).onSuccess { msgs ->
                _uiState.value = _uiState.value.copy(messages = msgs, isLoading = false)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun sendMessage(conversationId: String, content: String) {
        viewModelScope.launch {
            repo.sendMessage(conversationId, content).onSuccess { msg ->
                val updated = _uiState.value.messages + msg
                _uiState.value = _uiState.value.copy(messages = updated)
            }
        }
    }

    fun markRead(conversationId: String) {
        viewModelScope.launch {
            repo.markConversationRead(conversationId)
        }
    }
}
