package com.anix.app.ui.screens.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anix.app.core.di.ServiceLocator
import com.anix.app.data.models.AnimeSeries
import com.anix.app.data.models.Episode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AnimeDetailUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val anime: AnimeSeries? = null,
    val episodes: List<Episode> = emptyList(),
    val isFavorited: Boolean = false,
    val selectedTab: Int = 0
)

class AnimeDetailViewModel : ViewModel() {
    private val animeRepo = ServiceLocator.getAnimeRepository()
    private val userRepo = ServiceLocator.getUserRepository()

    private val _uiState = MutableStateFlow(AnimeDetailUiState())
    val uiState: StateFlow<AnimeDetailUiState> = _uiState.asStateFlow()

    fun loadAnime(animeId: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            animeRepo.getAnimeDetail(animeId).onSuccess { a ->
                _uiState.value = _uiState.value.copy(anime = a, isFavorited = a.isFavorited)
            }.onFailure { _uiState.value = _uiState.value.copy(error = it.message) }

            animeRepo.getAnimeEpisodes(animeId).onSuccess { eps ->
                _uiState.value = _uiState.value.copy(episodes = eps)
            }
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    fun toggleFavorite(animeId: String) {
        val current = _uiState.value.isFavorited
        _uiState.value = _uiState.value.copy(isFavorited = !current)
        viewModelScope.launch {
            userRepo.toggleFavorite(animeId).onFailure {
                _uiState.value = _uiState.value.copy(isFavorited = current)
            }
        }
    }

    fun setSelectedTab(tab: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }
}
