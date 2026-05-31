package com.anix.app.data.repositories

import android.content.Context
import android.util.Log
import com.anix.app.core.di.ServiceLocator
import com.anix.app.core.network.ApiService
import com.anix.app.data.models.*
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthRepository(
    private val api: ApiService,
    private val context: Context
) {
    suspend fun googleLogin(idToken: String): Result<AuthResponse> {
        return try {
            Log.d("AnixAuth", "Google login attempt")
            val response = api.googleLogin(GoogleLoginRequest(idToken))
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.data != null) {
                Log.d("AnixAuth", "Google login success")
                ServiceLocator.saveToken(body.data.token)
                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                            ServiceLocator.getNotificationRepository().upsertToken(task.result)
                        }
                    }
                }
                Result.success(body.data)
            } else {
                val err = body?.error ?: "Google login failed"
                Log.w("AnixAuth", "Google login failed: $err")
                Result.failure(Exception(err))
            }
        } catch (e: Exception) {
            Log.e("AnixAuth", "Google login exception: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return try {
            Log.d("AnixAuth", "Login attempt: $email")
            val response = api.login(LoginRequest(email, password))
            val body = response.body()
            val errorBodyStr = if (response.isSuccessful) null else response.errorBody()?.string()
            Log.d("AnixAuth", "Login response: isSuccessful=${response.isSuccessful}, body=${body?.success}, error=$errorBodyStr")
            if (response.isSuccessful && body?.success == true && body.data != null) {
                Log.d("AnixAuth", "Login success, token length: ${body.data.token.length}")
                ServiceLocator.saveToken(body.data.token)
                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        CoroutineScope(Dispatchers.IO).launch {
                            ServiceLocator.getNotificationRepository().upsertToken(task.result)
                        }
                    }
                }
                Result.success(body.data)
            } else {
                val err = errorBodyStr ?: body?.error ?: "Login failed"
                Log.w("AnixAuth", "Login failed: $err")
                Result.failure(Exception(err))
            }
        } catch (e: Exception) {
            Log.e("AnixAuth", "Login exception: ${e.javaClass.simpleName}: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun register(username: String, email: String, password: String): Result<AuthResponse> {
        return try {
            Log.d("AnixAuth", "Register attempt: $username / $email")
            val response = api.register(RegisterRequest(username, email, password))
            val body = response.body()
            val errorBodyStr = if (response.isSuccessful) null else response.errorBody()?.string()
            Log.d("AnixAuth", "Register response: isSuccessful=${response.isSuccessful}, body=${body?.success}, error=$errorBodyStr")
            if (response.isSuccessful && body?.success == true && body.data != null) {
                Log.d("AnixAuth", "Register success, token length: ${body.data.token.length}")
                ServiceLocator.saveToken(body.data.token)
                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        CoroutineScope(Dispatchers.IO).launch {
                            ServiceLocator.getNotificationRepository().upsertToken(task.result)
                        }
                    }
                }
                Result.success(body.data)
            } else {
                val err = errorBodyStr ?: body?.error ?: "Registration failed"
                Log.w("AnixAuth", "Register failed: $err")
                Result.failure(Exception(err))
            }
        } catch (e: Exception) {
            Log.e("AnixAuth", "Register exception: ${e.javaClass.simpleName}: ${e.message}", e)
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
        try {
            ServiceLocator.getNotificationRepository().deleteToken()
            api.logout()
        } catch (_: Exception) { }
        ServiceLocator.clearToken()
        return Result.success(Unit)
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