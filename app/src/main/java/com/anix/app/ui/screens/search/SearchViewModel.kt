package com.anix.app.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anix.app.core.di.ServiceLocator
import com.anix.app.data.models.AnimeSeries
import com.anix.app.data.models.Genre
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
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
    val hasSearched: Boolean = false,
    val recentSearches: List<String> = emptyList(),
    val showRecentSearches: Boolean = false
)

class SearchViewModel : ViewModel() {
    private val repo = ServiceLocator.getAnimeRepository()

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null
    private val recentCache = mutableListOf<String>()

    init {
        loadGenres()
        loadRecentSearches()
    }

    private fun loadGenres() {
        viewModelScope.launch {
            repo.getGenres().onSuccess { genres ->
                _uiState.value = _uiState.value.copy(genres = genres)
            }
        }
    }

    private fun loadRecentSearches() {
        _uiState.value = _uiState.value.copy(
            recentSearches = getRecentFromMemory()
        )
    }

    private fun getRecentFromMemory(): List<String> = recentCache.toList()

    private fun addRecent(query: String) {
        recentCache.remove(query)
        recentCache.add(0, query)
        if (recentCache.size > 10) recentCache.removeAt(recentCache.lastIndex)
        _uiState.value = _uiState.value.copy(recentSearches = getRecentFromMemory())
    }

    fun clearRecentSearches() {
        recentCache.clear()
        _uiState.value = _uiState.value.copy(recentSearches = emptyList(), showRecentSearches = false)
    }

    fun setQuery(query: String) {
        _uiState.value = _uiState.value.copy(query = query, selectedGenre = null)
        searchJob?.cancel()
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(results = emptyList(), hasSearched = false)
            return
        }
        searchJob = viewModelScope.launch {
            delay(500)
            performSearch(query)
        }
    }

    fun selectGenre(slug: String?) {
        val state = _uiState.value
        val newGenre = if (state.selectedGenre == slug) null else slug
        _uiState.value = state.copy(selectedGenre = newGenre, query = "")
        searchJob?.cancel()
        search()
    }

    fun search() {
        val state = _uiState.value
        val q = state.query.trim()
        if (q.isNotBlank()) addRecent(q)
        _uiState.value = state.copy(hasSearched = true, isLoading = true, error = null, showRecentSearches = false)
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

    private suspend fun performSearch(query: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, hasSearched = true, error = null)
        repo.searchAnime(query)
            .onSuccess { results ->
                _uiState.value = _uiState.value.copy(results = results, isLoading = false)
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
    }

    fun removeRecent(query: String) {
        recentCache.remove(query)
        _uiState.value = _uiState.value.copy(recentSearches = getRecentFromMemory())
    }
}
