package com.anix.app.ui.screens.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anix.app.core.di.ServiceLocator
import com.anix.app.data.models.Conversation
import com.anix.app.data.models.FriendRequest
import com.anix.app.data.models.SearchUsersResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatListUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val conversations: List<Conversation> = emptyList(),
    val friendRequests: List<FriendRequest> = emptyList(),
    val selectedTab: Int = 0,
    val searchQuery: String = "",
    val searchResults: List<SearchUsersResponse> = emptyList(),
    val isSearching: Boolean = false
)

class ChatListViewModel : ViewModel() {
    private val chatRepo = ServiceLocator.getChatRepository()
    private val userRepo = ServiceLocator.getUserRepository()

    private val _uiState = MutableStateFlow(ChatListUiState())
    val uiState: StateFlow<ChatListUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadConversations()
        loadFriendRequests()
    }

    fun loadConversations() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            chatRepo.getConversations()
                .onSuccess { convos ->
                    _uiState.value = _uiState.value.copy(conversations = convos, isLoading = false)
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
                }
        }
    }

    fun loadFriendRequests() {
        viewModelScope.launch {
            userRepo.getFriendRequests()
                .onSuccess { requests ->
                    _uiState.value = _uiState.value.copy(friendRequests = requests)
                }
        }
    }

    fun setSelectedTab(tab: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        searchJob?.cancel()
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(searchResults = emptyList(), isSearching = false)
            return
        }
        searchJob = viewModelScope.launch {
            delay(500)
            _uiState.value = _uiState.value.copy(isSearching = true)
            userRepo.searchUsers(query)
                .onSuccess { users ->
                    _uiState.value = _uiState.value.copy(searchResults = users, isSearching = false)
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(isSearching = false)
                }
        }
    }

    fun respondToRequest(requestId: String, accept: Boolean) {
        viewModelScope.launch {
            userRepo.respondFriendRequest(requestId, accept)
                .onSuccess { loadFriendRequests() }
        }
    }

    fun sendFriendRequest(userId: String) {
        viewModelScope.launch {
            userRepo.sendFriendRequest(userId)
        }
    }

    fun startChatWith(userId: String, onChatCreated: (String) -> Unit) {
        viewModelScope.launch {
            chatRepo.getOrCreateConversation(userId)
                .onSuccess { conv -> onChatCreated(conv.id) }
        }
    }
}