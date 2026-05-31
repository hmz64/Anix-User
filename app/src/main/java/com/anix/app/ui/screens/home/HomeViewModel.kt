package com.anix.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anix.app.core.di.ServiceLocator
import com.anix.app.data.models.AnimeSeries
import com.anix.app.data.models.Banner
import com.anix.app.data.models.ContinueWatchingItem
import com.anix.app.data.models.Genre
import com.anix.app.data.models.LeaderboardUser
import com.anix.app.data.models.MostWatchedEpisode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val trendingAnime: List<AnimeSeries> = emptyList(),
    val newReleases: List<AnimeSeries> = emptyList(),
    val genres: List<Genre> = emptyList(),
    val banners: List<Banner> = emptyList(),
    val schedule: List<AnimeSeries> = emptyList(),
    val leaderboard: List<LeaderboardUser> = emptyList(),
    val continueWatching: List<ContinueWatchingItem> = emptyList(),
    val mostWatched: List<MostWatchedEpisode> = emptyList(),
    val unreadCount: Int = 0
)

class HomeViewModel : ViewModel() {
    private val animeRepo = ServiceLocator.getAnimeRepository()
    private val userRepo = ServiceLocator.getUserRepository()
    private val notifRepo = ServiceLocator.getNotificationRepository()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            try {
                val trending = animeRepo.getAnimeList(sort = "rating").getOrNull().orEmpty()
                val newReleases = animeRepo.getAnimeList(sort = "newest").getOrNull().orEmpty()
                val genres = animeRepo.getGenres().getOrNull().orEmpty()
                val banners = animeRepo.getBanners().getOrNull().orEmpty()
                val schedule = animeRepo.getSchedule().getOrNull().orEmpty()
                val leaderboard = userRepo.getLeaderboard(limit = 10).getOrNull().orEmpty()
                val continueWatching = userRepo.getContinueWatching().getOrNull().orEmpty()
                val mostWatched = animeRepo.getMostWatched(limit = 10).getOrNull().orEmpty()
                val unreadCount = notifRepo.getUnreadCount().getOrNull() ?: 0
                _uiState.value = HomeUiState(
                    isLoading = false,
                    trendingAnime = trending,
                    newReleases = newReleases,
                    genres = genres,
                    banners = banners,
                    schedule = schedule,
                    leaderboard = leaderboard,
                    continueWatching = continueWatching,
                    mostWatched = mostWatched,
                    unreadCount = unreadCount
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}
