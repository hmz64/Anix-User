package com.anix.app.data.repositories

import com.anix.app.core.network.ApiService
import com.anix.app.data.models.*

class AnimeRepository(private val api: ApiService) {

    suspend fun getAnimeList(page: Int = 1, limit: Int = 20, genre: String? = null, sort: String? = null): Result<List<AnimeSeries>> {
        return try {
            val response = api.getAnimeList(page, limit, genre, sort)
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                Result.success(body.data ?: emptyList())
            } else {
                Result.failure(Exception(body?.error ?: "Failed to fetch anime"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchAnime(query: String, page: Int = 1, limit: Int = 20): Result<List<AnimeSeries>> {
        return try {
            val response = api.searchAnime(query, page, limit)
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

    suspend fun getSchedule(): Result<List<AnimeSeries>> {
        return try {
            val response = api.getSchedule()
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                Result.success(body.data ?: emptyList())
            } else {
                Result.failure(Exception(body?.error ?: "Failed to fetch schedule"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRanking(page: Int = 1, limit: Int = 20): Result<List<AnimeSeries>> {
        return try {
            val response = api.getRanking(page, limit)
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                Result.success(body.data ?: emptyList())
            } else {
                Result.failure(Exception(body?.error ?: "Failed to fetch ranking"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getGenres(): Result<List<Genre>> {
        return try {
            val response = api.getGenres()
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                Result.success(body.data ?: emptyList())
            } else {
                Result.failure(Exception(body?.error ?: "Failed to fetch genres"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAnimeDetail(id: String): Result<AnimeSeries> {
        return try {
            val response = api.getAnimeDetail(id)
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.error ?: "Failed to fetch anime detail"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAnimeEpisodes(id: String): Result<List<Episode>> {
        return try {
            val response = api.getAnimeEpisodes(id)
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                Result.success(body.data ?: emptyList())
            } else {
                Result.failure(Exception(body?.error ?: "Failed to fetch episodes"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getEpisodeDetail(id: String): Result<Episode> {
        return try {
            val response = api.getEpisodeDetail(id)
            val body = response.body()
            if (response.isSuccessful && body?.success == true && body.data != null) {
                Result.success(body.data)
            } else {
                Result.failure(Exception(body?.error ?: "Failed to fetch episode"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getEpisodeStreams(id: String): Result<List<EpisodeStream>> {
        return try {
            val response = api.getEpisodeStreams(id)
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                Result.success(body.data ?: emptyList())
            } else {
                Result.failure(Exception(body?.error ?: "Failed to fetch streams"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getBanners(): Result<List<Banner>> {
        return try {
            val response = api.getBanners()
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                Result.success(body.data ?: emptyList())
            } else {
                Result.failure(Exception(body?.error ?: "Failed to fetch banners"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAnimeByCategory(category: String, page: Int = 1, genre: String? = null, status: String? = null, type: String? = null, sortBy: String? = null): Result<List<AnimeSeries>> {
        return getAnimeList(page = page, genre = genre, sort = sortBy)
    }

    suspend fun getMostWatched(limit: Int = 10): Result<List<MostWatchedEpisode>> {
        return try {
            val response = api.getMostWatched(limit)
            val body = response.body()
            if (response.isSuccessful && body?.success == true) {
                Result.success(body.data ?: emptyList())
            } else {
                Result.failure(Exception(body?.error ?: "Failed to fetch most watched"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
