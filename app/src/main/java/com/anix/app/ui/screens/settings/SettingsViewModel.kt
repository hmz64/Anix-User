package com.anix.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anix.app.core.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

data class SettingsUiState(
    val isLoading: Boolean = false,
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
            userRepo.updateName(username)
                .onSuccess { _uiState.value = _uiState.value.copy(isLoading = false, successMessage = "Username updated") }
                .onFailure { _uiState.value = _uiState.value.copy(isLoading = false, error = it.message) }
        }
    }

    fun updateBio(bio: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null, successMessage = null)
        viewModelScope.launch {
            userRepo.updateBio(bio)
                .onSuccess { _uiState.value = _uiState.value.copy(isLoading = false, successMessage = "Bio updated") }
                .onFailure { _uiState.value = _uiState.value.copy(isLoading = false, error = it.message) }
        }
    }

    fun updatePassword(oldPassword: String, newPassword: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null, successMessage = null)
        viewModelScope.launch {
            userRepo.updatePassword(oldPassword, newPassword)
                .onSuccess { _uiState.value = _uiState.value.copy(isLoading = false, successMessage = "Password updated") }
                .onFailure { _uiState.value = _uiState.value.copy(isLoading = false, error = it.message) }
        }
    }

    fun updatePrivacy(privacySetting: String) {
        viewModelScope.launch {
            userRepo.updatePrivacy(privacySetting).onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun updateAvatar(part: MultipartBody.Part) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null, successMessage = null)
        viewModelScope.launch {
            try {
                val api = ServiceLocator.getApiService()
                val response = api.updateAvatar(part)
                val body = response.body()
                if (response.isSuccessful && body?.success == true) {
                    _uiState.value = _uiState.value.copy(isLoading = false, successMessage = "Avatar updated")
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = body?.error ?: "Failed to update avatar")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun updateBanner(part: MultipartBody.Part) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null, successMessage = null)
        viewModelScope.launch {
            try {
                val api = ServiceLocator.getApiService()
                val response = api.updateBannerImage(part)
                val body = response.body()
                if (response.isSuccessful && body?.success == true) {
                    _uiState.value = _uiState.value.copy(isLoading = false, successMessage = "Banner updated")
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = body?.error ?: "Failed to update banner")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}
