package com.anix.app.data.repositories

import com.anix.app.core.network.ApiService
import com.anix.app.data.models.*

class NotificationRepository(private val api: ApiService) {

    suspend fun getNotifications(page: Int = 1, limit: Int = 20): Result<List<Notification>> {
        return try {
            val response = api.getNotifications(page, limit)
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                Result.success(body.data ?: emptyList())
            } else {
                Result.failure(Exception(body?.error ?: "Failed to fetch notifications"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUnreadCount(): Result<Int> {
        return try {
            val response = api.getUnreadCount()
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                Result.success(body.data ?: 0)
            } else {
                Result.failure(Exception(body?.error ?: "Failed to fetch unread count"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markAllRead(): Result<Unit> {
        return try {
            val response = api.markAllNotificationsRead()
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(body?.error ?: "Failed to mark read"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markRead(notificationId: String): Result<Unit> {
        return markAllRead()
    }

    suspend fun upsertToken(token: String): Result<Unit> {
        return try {
            val response = api.upsertNotificationToken(NotificationToken(token))
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(body?.error ?: "Failed to upsert token"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteToken(): Result<Unit> {
        return try {
            val response = api.deleteNotificationToken()
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(body?.error ?: "Failed to delete token"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}