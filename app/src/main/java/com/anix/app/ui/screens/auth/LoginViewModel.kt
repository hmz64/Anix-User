package com.anix.app.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anix.app.core.di.ServiceLocator
import com.anix.app.data.models.AuthResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val loginSuccess: AuthResponse? = null
)

class LoginViewModel : ViewModel() {
    private val authRepo = ServiceLocator.getAuthRepository()

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        _uiState.value = LoginUiState(isLoading = true)
        viewModelScope.launch {
            authRepo.login(email, password).onSuccess { response ->
                _uiState.value = LoginUiState(loginSuccess = response)
            }.onFailure { e ->
                _uiState.value = LoginUiState(error = e.message)
            }
        }
    }
}
