package com.anix.app.data.repositories

import com.anix.app.core.network.ApiService
import com.anix.app.data.models.*

class UserRepository(private val api: ApiService) {

    suspend fun getProfile(userId: String? = null): Result<User> {
        return try {
            val response = if (userId != null) api.getUserProfile(userId) else api.getMyProfile()
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.error ?: "Failed to fetch profile"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePrivacy(privacySetting: String): Result<Unit> {

    suspend fun getUserStats(): Result<UserStats> {
        return try {
            val response = api.getUserStats()
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.error ?: "Failed to fetch stats"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserComments(page: Int = 1, limit: Int = 20): Result<List<Comment>> {
        return try {
            val response = api.getUserComments(page, limit)
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                Result.success(body.data ?: emptyList())
            } else {
                Result.failure(Exception(body?.error ?: "Failed to fetch comments"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateName(username: String): Result<User> {
        return try {
            val response = api.updateName(UpdateNameRequest(username))
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.error ?: "Failed to update name"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateBanner(banner: String): Result<User> {
        return try {
            val response = api.updateBanner(UpdateBannerRequest(banner))
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.error ?: "Failed to update banner"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePrivacy(privacySetting: String): Result<Unit> {
        return try {
            val response = api.updatePrivacy(PrivacyRequest(privacySetting))
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(body?.error ?: "Failed to update privacy"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWatchHistory(page: Int = 1, limit: Int = 20): Result<List<WatchHistory>> {
        return try {
            val response = api.getWatchHistory(page, limit)
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                Result.success(body.data ?: emptyList())
            } else {
                Result.failure(Exception(body?.error ?: "Failed to fetch history"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFavorites(page: Int = 1, limit: Int = 20): Result<List<UserFavorite>> {
        return try {
            val response = api.getFavorites(page, limit)
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                Result.success(body.data ?: emptyList())
            } else {
                Result.failure(Exception(body?.error ?: "Failed to fetch favorites"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleFavorite(animeId: String): Result<Unit> {
        return try {
            val response = api.toggleFavorite(animeId)
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(body?.error ?: "Failed to toggle favorite"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getComments(episodeId: String): Result<List<Comment>> {
        return try {
            val response = api.getComments(episodeId)
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                Result.success(body.data ?: emptyList())
            } else {
                Result.failure(Exception(body?.error ?: "Failed to fetch comments"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createComment(episodeId: String, content: String, parentId: String? = null): Result<Comment> {
        return try {
            val response = api.createComment(episodeId, CreateCommentRequest(content, parentId))
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.error ?: "Failed to create comment"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteComment(episodeId: String, commentId: String): Result<Unit> {
        return try {
            val response = api.deleteComment(episodeId, commentId)
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(body?.error ?: "Failed to delete comment"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun reportComment(episodeId: String, commentId: String, reason: String): Result<Unit> {
        return try {
            val response = api.reportComment(episodeId, commentId, ReportCommentRequest(reason))
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(body?.error ?: "Failed to report comment"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

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
}