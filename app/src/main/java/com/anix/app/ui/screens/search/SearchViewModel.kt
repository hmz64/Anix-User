package com.anix.app.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anix.app.core.di.ServiceLocator
import com.anix.app.data.models.AnimeSeries
import com.anix.app.data.models.Genre
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val results: List<AnimeSeries> = emptyList(),
    val genres: List<Genre> = emptyList(),
    val selectedGenre: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val hasSearched: Boolean = false
)

class SearchViewModel : ViewModel() {
    private val repo = ServiceLocator.getAnimeRepository()

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        loadGenres()
    }

    private fun loadGenres() {
        viewModelScope.launch {
            repo.getGenres().onSuccess { genres ->
                _uiState.value = _uiState.value.copy(genres = genres)
            }
        }
    }

    fun setQuery(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
    }

    fun selectGenre(slug: String?) {
        val state = _uiState.value
        val newGenre = if (state.selectedGenre == slug) null else slug
        _uiState.value = state.copy(selectedGenre = newGenre)
        search()
    }

    fun search() {
        val state = _uiState.value
        _uiState.value = state.copy(isLoading = true, hasSearched = true, error = null)
        viewModelScope.launch {
            val response = when {
                state.query.isNotBlank() -> repo.searchAnime(state.query)
                state.selectedGenre != null -> repo.getAnimeList(genre = state.selectedGenre)
                else -> repo.getAnimeList()
            }
            response.onSuccess { results ->
                _uiState.value = _uiState.value.copy(results = results, isLoading = false)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }
}
