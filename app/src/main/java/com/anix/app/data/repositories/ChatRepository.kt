package com.anix.app.data.repositories

import com.anix.app.core.network.ApiService
import com.anix.app.data.models.*

class ChatRepository(private val api: ApiService) {

    suspend fun getConversations(): Result<List<Conversation>> {
        return try {
            val response = api.getConversations()
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                Result.success(body.data ?: emptyList())
            } else {
                Result.failure(Exception(body?.error ?: "Failed to fetch conversations"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getConversationMessages(id: String, page: Int = 1, limit: Int = 50): Result<List<Message>> {
        return try {
            val response = api.getConversationMessages(id, page, limit)
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                Result.success(body.data ?: emptyList())
            } else {
                Result.failure(Exception(body?.error ?: "Failed to fetch messages"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendMessage(conversationId: String, content: String): Result<Message> {
        return try {
            val response = api.sendMessage(conversationId, SendMessageRequest(conversationId, content))
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.error ?: "Failed to send message"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markConversationRead(id: String): Result<Unit> {
        return try {
            val response = api.markConversationRead(id)
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

    suspend fun getFriends(): Result<List<User>> {
        return try {
            val response = api.getFriends()
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                Result.success(body.data ?: emptyList())
            } else {
                Result.failure(Exception(body?.error ?: "Failed to fetch friends"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun sendFriendRequest(userId: String): Result<FriendRequest> {
        return try {
            val response = api.sendFriendRequest(mapOf("user_id" to userId))
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.error ?: "Failed to send request"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun respondFriendRequest(accept: Boolean): Result<FriendRequest> {
        return try {
            val response = api.respondFriendRequest(FriendRequestAction(accept))
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.error ?: "Failed to respond"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchUsers(query: String): Result<List<SearchUsersResponse>> {
        return try {
            val response = api.searchUsers(query)
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                Result.success(body.data ?: emptyList())
            } else {
                Result.failure(Exception(body?.error ?: "Search failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getOrCreateConversation(userId: String): Result<Conversation> {
        return try {
            val response = api.getOrCreateConversation(userId)
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.error ?: "Failed to get conversation"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}