package com.anix.app.data.repositories

import com.anix.app.core.network.ApiService
import com.anix.app.data.models.*

class SocialRepository(private val api: ApiService) {

    suspend fun getSocialFeed(page: Int = 1, limit: Int = 20): Result<List<SocialPost>> {
        return try {
            val response = api.getSocialFeed(page, limit)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data ?: emptyList())
            } else {
                Result.failure(Exception(response.body()?.error ?: "Failed to fetch feed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createPost(content: String, image: String? = null): Result<SocialPost> {
        return try {
            val response = api.createPost(CreatePostRequest(content, image))
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Failed to create post"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun likePost(postId: String): Result<Unit> {
        return try {
            val response = api.likePost(postId)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Failed to toggle like"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
