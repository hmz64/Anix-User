package com.anix.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anix.app.core.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val successMessage: String? = null
)

class SettingsViewModel : ViewModel() {
    private val userRepo = ServiceLocator.getUserRepository()

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun updateUsername(username: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null, successMessage = null)
        viewModelScope.launch {
            userRepo.updateUsername(username)
                .onSuccess { _uiState.value = _uiState.value.copy(isLoading = false, successMessage = "Username updated") }
                .onFailure { _uiState.value = _uiState.value.copy(isLoading = false, error = it.message) }
        }
    }
}
