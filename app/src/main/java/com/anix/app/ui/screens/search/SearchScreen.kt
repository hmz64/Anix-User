package com.anix.app.ui.screens.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.anix.app.core.di.ServiceLocator
import com.anix.app.core.theme.Background
import com.anix.app.core.theme.BorderBlack
import com.anix.app.core.theme.Primary
import com.anix.app.core.theme.Surface
import com.anix.app.data.models.AnimeSeries
import com.anix.app.data.models.Genre
import com.anix.app.ui.components.EmptyState
import com.anix.app.ui.components.ErrorState
import com.anix.app.ui.components.LoadingIndicator
import com.anix.app.ui.components.NeoTextField
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(
    onAnimeClick: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf<List<AnimeSeries>>(emptyList()) }
    var genres by remember { mutableStateOf<List<Genre>>(emptyList()) }
    var selectedGenre by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var hasSearched by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        ServiceLocator.getAnimeRepository().getGenres().onSuccess { genres = it }
    }

    fun search() {
        isLoading = true
        hasSearched = true
        error = null
        scope.launch {
            val repo = ServiceLocator.getAnimeRepository()
            val response = if (query.isNotBlank()) {
                repo.searchAnime(query)
            } else if (selectedGenre != null) {
                repo.getAnimeList(genre = selectedGenre)
            } else {
                repo.getAnimeList()
            }
            response.onSuccess { results = it }.onFailure { error = it.message }
            isLoading = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            NeoTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = "Search anime...",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { search() }),
                singleLine = true
            )

            if (genres.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(genres.take(15)) { genre ->
                        val isSelected = selectedGenre == genre.slug
                        Text(
                            text = genre.name,
                            modifier = Modifier
                                .clickable { selectedGenre = if (isSelected) null else genre.slug; search() }
                                .background(
                                    if (isSelected) Primary else Surface,
                                    RoundedCornerShape(20.dp)
                                )
                                .border(BorderStroke(2.dp, BorderBlack), RoundedCornerShape(20.dp))
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else Color.Black
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Results area
            when {
                isLoading && results.isEmpty() -> LoadingIndicator()
                error != null && results.isEmpty() -> ErrorState(message = error!!, onRetry = { search() })
                !hasSearched -> EmptyState(message = "Search for your favorite anime")
                results.isEmpty() -> EmptyState(message = "No results found")
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        gridItems(results) { anime ->
                            AnimeSearchItem(
                                anime = anime,
                                onClick = { onAnimeClick(anime.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimeSearchItem(
    anime: AnimeSeries,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(8.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .border(BorderStroke(2.dp, BorderBlack), shape)
            .background(Color.White, shape)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.7f)
        ) {
            AsyncImage(
                model = anime.poster,
                contentDescription = anime.title,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .background(Primary, RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = String.format("%.1f", anime.rating),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
        Text(
            text = anime.title,
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}
