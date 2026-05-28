package com.anix.app.data.repositories

import android.content.Context
import com.anix.app.core.di.ServiceLocator
import com.anix.app.core.network.ApiService
import com.anix.app.data.models.*

class AuthRepository(
    private val api: ApiService,
    private val context: Context
) {
    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            val response = api.login(LoginRequest(email, password))
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.data != null) {
                ServiceLocator.saveToken(body.data.token)
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.error ?: "Login failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(username: String, email: String, password: String): Result<AuthResponse> {
        return try {
            val response = api.register(RegisterRequest(username, email, password))
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.data != null) {
                ServiceLocator.saveToken(body.data.token)
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.error ?: "Registration failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun me(): Result<User> {
        return try {
            val response = api.me()
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.error ?: "Failed to get user"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun logout(): Result<Unit> {
        return try {
            api.logout()
            ServiceLocator.clearToken()
            Result.success(Unit)
        } catch (e: Exception) {
            ServiceLocator.clearToken()
            Result.success(Unit)
        }
    }

    suspend fun refreshToken(): Result<AuthResponse> {
        return try {
            val response = api.refreshToken()
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.data != null) {
                ServiceLocator.saveToken(body.data.token)
                Result.success(body.data)
            } else {
                Result.failure(Exception("Token refresh failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun isLoggedIn(): Boolean {
        return ServiceLocator.getToken() != null
    }
}