package com.anix.app.data.repositories

import com.anix.app.core.network.ApiService
import com.anix.app.data.models.*

class UserRepository(private val api: ApiService) {

    suspend fun getUserProfile(userId: String): Result<User> {
        return try {
            val response = api.getUserProfile(userId)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Failed to fetch profile"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserStats(): Result<UserStats> {
        return try {
            val response = api.getUserStats()
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Failed to fetch stats"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getWatchHistory(page: Int = 1, limit: Int = 20): Result<List<WatchHistory>> {
        return try {
            val response = api.getWatchHistory(page, limit)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data ?: emptyList())
            } else {
                Result.failure(Exception(response.body()?.error ?: "Failed to fetch history"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFavorites(page: Int = 1, limit: Int = 20): Result<List<UserFavorite>> {
        return try {
            val response = api.getFavorites(page, limit)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data ?: emptyList())
            } else {
                Result.failure(Exception(response.body()?.error ?: "Failed to fetch favorites"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserComments(page: Int = 1, limit: Int = 20): Result<List<Comment>> {
        return try {
            val response = api.getUserComments(page, limit)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data ?: emptyList())
            } else {
                Result.failure(Exception(response.body()?.error ?: "Failed to fetch comments"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateName(username: String): Result<User> {
        return try {
            val response = api.updateName(UpdateNameRequest(username))
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Failed to update name"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateBanner(banner: String): Result<User> {
        return try {
            val response = api.updateBanner(UpdateBannerRequest(banner))
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Failed to update banner"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePrivacy(privacySetting: String): Result<User> {
        return try {
            val response = api.updatePrivacy(PrivacyRequest(privacySetting))
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.error ?: "Failed to update privacy"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleFavorite(animeId: String): Result<UserFavorite> {
        return try {
            val response = api.toggleFavorite(ToggleFavoriteRequest(animeId))
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Failed to toggle favorite"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProgress(episodeId: String, position: Int, completed: Boolean = false): Result<Unit> {
        return try {
            val response = api.updateProgress(UpdateProgressRequest(episodeId, position, completed))
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Failed to update progress"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getComments(animeId: String, episodeId: String? = null, page: Int = 1, limit: Int = 20): Result<List<Comment>> {
        return try {
            val response = api.getComments(animeId, episodeId, page, limit)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data ?: emptyList())
            } else {
                Result.failure(Exception(response.body()?.error ?: "Failed to fetch comments"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createComment(animeId: String, content: String, episodeId: String? = null, parentId: String? = null): Result<Comment> {
        return try {
            val response = api.createComment(CreateCommentRequest(animeId, episodeId, parentId, content))
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Failed to create comment"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteComment(id: String): Result<Unit> {
        return try {
            val response = api.deleteComment(id)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Failed to delete comment"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun reportComment(id: String, reason: String): Result<Unit> {
        return try {
            val response = api.reportComment(id, ReportCommentRequest(reason))
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Failed to report comment"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
