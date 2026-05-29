package com.anix.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anix.app.core.di.ServiceLocator
import com.anix.app.data.models.AnimeSeries
import com.anix.app.core.theme.Background
import com.anix.app.core.theme.BorderBlack
import com.anix.app.core.theme.Primary
import com.anix.app.core.theme.Surface
import com.anix.app.data.repositories.AnimeRepository
import com.anix.app.ui.components.AnimeCard
import com.anix.app.ui.components.EmptyState
import com.anix.app.ui.components.ErrorState
import com.anix.app.ui.components.LoadingIndicator
import com.anix.app.ui.components.NeoChip
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AnimeListUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val animeList: List<AnimeSeries> = emptyList(),
    val genreFilter: String? = null,
    val statusFilter: String? = null,
    val typeFilter: String? = null,
    val sortBy: String? = null,
    val page: Int = 1,
    val hasMore: Boolean = true
)

class AnimeListViewModel(
    private val repository: AnimeRepository = ServiceLocator.getAnimeRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnimeListUiState())
    val uiState: StateFlow<AnimeListUiState> = _uiState.asStateFlow()

    private var currentCategory: String = ""

    fun loadData(category: String) {
        currentCategory = category
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val state = _uiState.value
                val result = repository.getAnimeByCategory(
                    category = category,
                    page = 1,
                    genre = state.genreFilter,
                    status = state.statusFilter,
                    type = state.typeFilter,
                    sortBy = state.sortBy
                )
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        animeList = result,
                        page = 1,
                        hasMore = result.isNotEmpty()
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun loadMore() {
        val state = _uiState.value
        if (state.isLoading || !state.hasMore) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val nextPage = state.page + 1
                val result = repository.getAnimeByCategory(
                    category = currentCategory,
                    page = nextPage,
                    genre = state.genreFilter,
                    status = state.statusFilter,
                    type = state.typeFilter,
                    sortBy = state.sortBy
                )
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        animeList = it.animeList + result,
                        page = nextPage,
                        hasMore = result.isNotEmpty()
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun filterByGenre(genre: String?) {
        _uiState.update { it.copy(genreFilter = genre) }
        loadData(currentCategory)
    }

    fun filterByStatus(status: String?) {
        _uiState.update { it.copy(statusFilter = status) }
        loadData(currentCategory)
    }

    fun filterByType(type: String?) {
        _uiState.update { it.copy(typeFilter = type) }
        loadData(currentCategory)
    }

    fun sortBy(sort: String?) {
        _uiState.update { it.copy(sortBy = sort) }
        loadData(currentCategory)
    }

    fun refresh() {
        loadData(currentCategory)
    }
}

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.material.ExperimentalMaterialApi::class)
@Composable
fun AnimeListScreen(
    category: String,
    onAnimeClick: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: AnimeListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(category) {
        viewModel.loadData(category)
    }

    val gridState = rememberLazyGridState()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.isLoading,
        onRefresh = { viewModel.refresh() }
    )

    LaunchedEffect(gridState) {
        snapshotFlow {
            val lastVisibleItem = gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = gridState.layoutInfo.totalItemsCount
            lastVisibleItem >= totalItems - 3
        }.collect { shouldLoadMore ->
            if (shouldLoadMore && !uiState.isLoading && uiState.hasMore) {
                viewModel.loadMore()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = category) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background,
                    titleContentColor = Color.Black
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .padding(padding)
                .pullRefresh(pullRefreshState)
        ) {
            when {
                uiState.isLoading && uiState.animeList.isEmpty() -> {
                    LoadingIndicator()
                }

                uiState.error != null && uiState.animeList.isEmpty() -> {
                    ErrorState(
                        message = uiState.error!!,
                        onRetry = { viewModel.refresh() }
                    )
                }

                uiState.animeList.isEmpty() -> {
                    EmptyState()
                }

                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        FilterChipsRow(
                            genreFilter = uiState.genreFilter,
                            statusFilter = uiState.statusFilter,
                            typeFilter = uiState.typeFilter,
                            sortBy = uiState.sortBy,
                            onGenreClick = { viewModel.filterByGenre(it) },
                            onStatusClick = { viewModel.filterByStatus(it) },
                            onTypeClick = { viewModel.filterByType(it) },
                            onSortClick = { viewModel.sortBy(it) }
                        )

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            state = gridState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentPadding = PaddingValues(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                items = uiState.animeList,
                                key = { it.id }
                            ) { anime ->
                                AnimeCard(
                                    anime = anime,
                                    onClick = { onAnimeClick(anime.id) }
                                )
                            }

                            item {
                                if (uiState.isLoading && uiState.animeList.isNotEmpty()) {
                                    LoadingIndicator(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = uiState.isLoading,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter),
                backgroundColor = Surface,
                contentColor = Primary
            )
        }
    }
}

@Composable
private fun FilterChipsRow(
    genreFilter: String?,
    statusFilter: String?,
    typeFilter: String?,
    sortBy: String?,
    onGenreClick: (String?) -> Unit,
    onStatusClick: (String?) -> Unit,
    onTypeClick: (String?) -> Unit,
    onSortClick: (String?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Background)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        NeoChip(
            text = genreFilter ?: "Genre",
            selected = genreFilter != null,
            onClick = { onGenreClick(genreFilter) }
        )
        NeoChip(
            text = statusFilter ?: "Status",
            selected = statusFilter != null,
            onClick = { onStatusClick(statusFilter) }
        )
        NeoChip(
            text = typeFilter ?: "Type",
            selected = typeFilter != null,
            onClick = { onTypeClick(typeFilter) }
        )
        NeoChip(
            text = sortBy ?: "Sort",
            selected = sortBy != null,
            onClick = { onSortClick(sortBy) }
        )
    }
}
