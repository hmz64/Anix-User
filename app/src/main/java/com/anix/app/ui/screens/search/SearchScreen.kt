package com.anix.app.ui.screens.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.anix.app.core.theme.Background
import com.anix.app.core.theme.BorderBlack
import com.anix.app.core.theme.Primary
import com.anix.app.core.theme.Surface
import com.anix.app.core.theme.TextPrimary
import com.anix.app.core.theme.TextSecondary
import com.anix.app.core.theme.TextMuted
import com.anix.app.core.theme.AccentBlue
import com.anix.app.core.theme.GlassBorder
import com.anix.app.core.theme.AccentOrange
import com.anix.app.data.models.AnimeSeries
import com.anix.app.ui.components.EmptyState
import com.anix.app.ui.components.ErrorState
import com.anix.app.ui.components.LoadingIndicator
import com.anix.app.ui.components.NeoTextField
import com.anix.app.core.util.liquidGlass

@Composable
fun SearchScreen(
    onAnimeClick: (String) -> Unit,
    viewModel: SearchViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            NeoTextField(
                value = uiState.query,
                onValueChange = { viewModel.setQuery(it) },
                placeholder = "Search anime...",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                trailingIcon = {
                    if (uiState.query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setQuery("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    viewModel.search()
                }),
                singleLine = true
            )

            if (uiState.genres.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.genres.take(15)) { genre ->
                        val isSelected = uiState.selectedGenre == genre.slug
                        Text(
                            text = genre.name,
                            modifier = Modifier
                                .clickable { viewModel.selectGenre(genre.slug) }
                                .background(
                                    if (isSelected) AccentBlue else Color.White.copy(alpha = 0.08f),
                                    RoundedCornerShape(20.dp)
                                )
                                .border(1.dp, if (isSelected) AccentBlue else GlassBorder, RoundedCornerShape(20.dp))
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) TextPrimary else TextSecondary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Recent Searches
            if (uiState.query.isBlank() && uiState.recentSearches.isNotEmpty() && !uiState.hasSearched) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Searches",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Clear All",
                        style = MaterialTheme.typography.labelSmall,
                        color = AccentBlue,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { viewModel.clearRecentSearches() }
                    )
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                ) {
                    items(uiState.recentSearches) { recent ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setQuery(recent)
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                            Icons.Default.History,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 12.dp),
                            tint = TextMuted
                        )
                        Text(
                            text = recent,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                            IconButton(onClick = { viewModel.removeRecent(recent) }) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Remove",
                                        modifier = Modifier.size(16.dp),
                                        tint = TextMuted
                                    )
                            }
                        }
                    }
                }
            }

            when {
                uiState.isLoading && uiState.results.isEmpty() -> LoadingIndicator()
                uiState.error != null && uiState.results.isEmpty() -> ErrorState(message = uiState.error!!, onRetry = { viewModel.search() })
                !uiState.hasSearched && uiState.query.isBlank() && uiState.recentSearches.isEmpty() -> EmptyState(message = "Search for your favorite anime")
                uiState.results.isEmpty() && uiState.hasSearched -> EmptyState(message = "No results found")
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        gridItems(uiState.results) { anime ->
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
    Column(
        modifier = modifier
            .clickable { onClick() }
            .liquidGlass(
                shape = RoundedCornerShape(12.dp),
                alpha = 0.08f
            )
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
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(6.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(AccentBlue.copy(alpha = 0.85f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = String.format("%.1f", anime.rating),
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            }
        }
        Text(
            text = anime.title,
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = TextPrimary
        )
    }
}
