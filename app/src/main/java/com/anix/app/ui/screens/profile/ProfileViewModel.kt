package com.anix.app.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anix.app.core.di.ServiceLocator
import com.anix.app.data.models.Comment
import com.anix.app.data.models.User
import com.anix.app.data.models.UserFavorite
import com.anix.app.data.models.UserStats
import com.anix.app.data.models.WatchHistory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val user: User? = null,
    val stats: UserStats? = null,
    val selectedTab: Int = 0,
    val history: List<WatchHistory> = emptyList(),
    val favorites: List<UserFavorite> = emptyList(),
    val comments: List<Comment> = emptyList()
)

class ProfileViewModel : ViewModel() {
    private val authRepo = ServiceLocator.getAuthRepository()
    private val userRepo = ServiceLocator.getUserRepository()

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            authRepo.me().onSuccess { u ->
                _uiState.value = _uiState.value.copy(user = u)
            }.onFailure { _uiState.value = _uiState.value.copy(error = it.message) }

            userRepo.getUserStats().onSuccess { s ->
                _uiState.value = _uiState.value.copy(stats = s)
            }
            userRepo.getWatchHistory().onSuccess { h ->
                _uiState.value = _uiState.value.copy(history = h)
            }
            userRepo.getFavorites().onSuccess { f ->
                _uiState.value = _uiState.value.copy(favorites = f)
            }
            userRepo.getUserComments().onSuccess { c ->
                _uiState.value = _uiState.value.copy(comments = c)
            }
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun setSelectedTab(tab: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }

    fun logout() {
        viewModelScope.launch {
            authRepo.logout()
        }
    }
}
