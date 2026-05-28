package com.anix.app.data.repositories

import com.anix.app.core.network.ApiService
import com.anix.app.data.models.*

class AnimeRepository(private val api: ApiService) {

    suspend fun getAnimeList(page: Int = 1, limit: Int = 20, genre: String? = null, sort: String? = null): Result<List<AnimeSeries>> {
        return try {
            val response = api.getAnimeList(page, limit, genre, sort)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data ?: emptyList())
            } else {
                Result.failure(Exception(response.body()?.error ?: "Failed to fetch anime"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchAnime(query: String, page: Int = 1, limit: Int = 20): Result<List<AnimeSeries>> {
        return try {
            val response = api.searchAnime(query, page, limit)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data ?: emptyList())
            } else {
                Result.failure(Exception(response.body()?.error ?: "Search failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getSchedule(): Result<List<AnimeSeries>> {
        return try {
            val response = api.getSchedule()
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data ?: emptyList())
            } else {
                Result.failure(Exception(response.body()?.error ?: "Failed to fetch schedule"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRanking(page: Int = 1, limit: Int = 20): Result<List<AnimeSeries>> {
        return try {
            val response = api.getRanking(page, limit)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data ?: emptyList())
            } else {
                Result.failure(Exception(response.body()?.error ?: "Failed to fetch ranking"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getGenres(): Result<List<Genre>> {
        return try {
            val response = api.getGenres()
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data ?: emptyList())
            } else {
                Result.failure(Exception(response.body()?.error ?: "Failed to fetch genres"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAnimeDetail(id: String): Result<AnimeSeries> {
        return try {
            val response = api.getAnimeDetail(id)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Failed to fetch anime detail"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAnimeEpisodes(id: String): Result<List<Episode>> {
        return try {
            val response = api.getAnimeEpisodes(id)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data ?: emptyList())
            } else {
                Result.failure(Exception(response.body()?.error ?: "Failed to fetch episodes"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getEpisodeDetail(id: String): Result<Episode> {
        return try {
            val response = api.getEpisodeDetail(id)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.error ?: "Failed to fetch episode"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getEpisodeStreams(id: String): Result<List<EpisodeStream>> {
        return try {
            val response = api.getEpisodeStreams(id)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data ?: emptyList())
            } else {
                Result.failure(Exception(response.body()?.error ?: "Failed to fetch streams"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBanners(): Result<List<Banner>> {
        return try {
            val response = api.getBanners()
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data ?: emptyList())
            } else {
                Result.failure(Exception(response.body()?.error ?: "Failed to fetch banners"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
