package com.anix.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anix.app.core.di.ServiceLocator
import com.anix.app.data.models.AnimeSeries
import com.anix.app.data.models.Banner
import com.anix.app.data.models.Genre
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
    val banners: List<Banner> = emptyList()
)

class HomeViewModel : ViewModel() {
    private val repo = ServiceLocator.getAnimeRepository()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            try {
                val trending = repo.getAnimeList(sort = "rating").getOrNull().orEmpty()
                val newReleases = repo.getAnimeList(sort = "newest").getOrNull().orEmpty()
                val genres = repo.getGenres().getOrNull().orEmpty()
                val banners = repo.getBanners().getOrNull().orEmpty()
                _uiState.value = HomeUiState(
                    isLoading = false,
                    trendingAnime = trending,
                    newReleases = newReleases,
                    genres = genres,
                    banners = banners
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}
