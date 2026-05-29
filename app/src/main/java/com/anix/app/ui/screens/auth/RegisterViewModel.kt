package com.anix.app.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anix.app.core.di.ServiceLocator
import com.anix.app.data.models.AuthResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RegisterUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val registerSuccess: AuthResponse? = null
)

class RegisterViewModel : ViewModel() {
    private val authRepo = ServiceLocator.getAuthRepository()

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun register(username: String, email: String, password: String) {
        _uiState.value = RegisterUiState(isLoading = true)
        viewModelScope.launch {
            authRepo.register(username, email, password).onSuccess { response ->
                _uiState.value = RegisterUiState(registerSuccess = response)
            }.onFailure { e ->
                _uiState.value = RegisterUiState(error = e.message)
            }
        }
    }
}
