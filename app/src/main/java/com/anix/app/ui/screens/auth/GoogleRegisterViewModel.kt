package com.anix.app.ui.screens.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anix.app.core.di.ServiceLocator
import com.anix.app.data.models.AuthResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GoogleRegisterUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: AuthResponse? = null
)

class GoogleRegisterViewModel : ViewModel() {
    private val authRepo = ServiceLocator.getAuthRepository()

    private val _uiState = MutableStateFlow(GoogleRegisterUiState())
    val uiState: StateFlow<GoogleRegisterUiState> = _uiState.asStateFlow()

    fun completeRegistration(idToken: String, username: String) {
        _uiState.value = GoogleRegisterUiState(isLoading = true)
        viewModelScope.launch {
            authRepo.completeGoogleRegistration(idToken, username).onSuccess { response ->
                _uiState.value = GoogleRegisterUiState(success = response)
            }.onFailure { e ->
                Log.e("AnixAuth", "Google reg complete failed", e)
                _uiState.value = GoogleRegisterUiState(error = e.message)
            }
        }
    }
}
