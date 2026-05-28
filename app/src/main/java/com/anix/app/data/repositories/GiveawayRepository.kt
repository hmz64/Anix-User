package com.anix.app.data.repositories

import com.anix.app.core.network.ApiService
import com.anix.app.data.models.*

class GiveawayRepository(private val api: ApiService) {

    suspend fun getGiveaways(page: Int = 1, limit: Int = 20): Result<List<Giveaway>> {
        return try {
            val response = api.getGiveaways(page, limit)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data ?: emptyList())
            } else {
                Result.failure(Exception(response.body()?.error ?: "Failed to fetch giveaways"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getGiveawayDetail(id: String): Result<Giveaway> {
        return try {
            val response = api.getGiveawayDetail(id)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Failed to fetch giveaway"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createGiveaway(request: CreateGiveawayRequest): Result<Giveaway> {
        return try {
            val response = api.createGiveaway(request)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Failed to create giveaway"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun claimGiveaway(id: String): Result<Unit> {
        return try {
            val response = api.claimGiveaway(id)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Failed to claim giveaway"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTopGivers(page: Int = 1, limit: Int = 20): Result<List<TopGiver>> {
        return try {
            val response = api.getTopGivers(page, limit)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data ?: emptyList())
            } else {
                Result.failure(Exception(response.body()?.error ?: "Failed to fetch top givers"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
