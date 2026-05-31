package com.anix.app.ui.screens.auth

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anix.app.BuildConfig
import com.anix.app.core.di.ServiceLocator
import com.anix.app.data.models.AuthResponse
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
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
    private var googleSignInClient: GoogleSignInClient? = null

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun initGoogleSignIn(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.GOOGLE_WEB_CLIENT_ID)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    fun getGoogleSignInIntent() = googleSignInClient?.signInIntent

    fun handleGoogleResult(idToken: String) {
        _uiState.value = LoginUiState(isLoading = true)
        viewModelScope.launch {
            authRepo.googleLogin(idToken).onSuccess { response ->
                _uiState.value = LoginUiState(loginSuccess = response)
            }.onFailure { e ->
                Log.e("AnixAuth", "Google login failed", e)
                _uiState.value = LoginUiState(error = e.message)
            }
        }
    }

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
