package com.anix.app.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.anix.app.core.theme.Background
import com.anix.app.core.theme.BorderBlack
import com.anix.app.core.theme.Primary
import com.anix.app.data.models.Banner
import com.anix.app.ui.components.AnimeCard
import com.anix.app.ui.components.ErrorState
import com.anix.app.ui.components.LoadingIndicator
import com.anix.app.ui.components.NeoChip

@Composable
fun HomeScreen(
    onAnimeClick: (String) -> Unit,
    onSeeAllClick: (String) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoading) {
        LoadingIndicator()
    } else if (uiState.error != null) {
        ErrorState(message = uiState.error!!, onRetry = { viewModel.loadData() })
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .verticalScroll(rememberScrollState())
        ) {
            // Banner Carousel
            if (uiState.banners.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.banners.forEach { banner ->
                        BannerItem(
                            banner = banner,
                            modifier = Modifier
                                .width(340.dp)
                                .height(180.dp),
                            onClick = { if (banner.linkUrl.isNotEmpty()) onAnimeClick(banner.linkUrl) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Trending Now Section
            SectionHeader(
                title = "Trending Now",
                onSeeAll = { onSeeAllClick("trending") }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                uiState.trendingAnime.take(10).forEach { anime ->
                    AnimeCard(
                        posterUrl = anime.poster,
                        title = anime.title,
                        rating = anime.rating,
                        modifier = Modifier.width(130.dp),
                        onClick = { onAnimeClick(anime.id) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Genres
            Text(
                text = "Genres",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                uiState.genres.forEach { genre ->
                    NeoChip(text = genre.name, onClick = { })
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // New Releases
            SectionHeader(
                title = "New Releases",
                onSeeAll = { onSeeAllClick("new") }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                uiState.newReleases.take(10).forEach { anime ->
                    AnimeCard(
                        posterUrl = anime.poster,
                        title = anime.title,
                        rating = anime.rating,
                        modifier = Modifier.width(130.dp),
                        onClick = { onAnimeClick(anime.id) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String, onSeeAll: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "See All",
            style = MaterialTheme.typography.labelLarge,
            color = Primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onSeeAll() }
        )
    }
}

@Composable
private fun BannerItem(
    banner: Banner,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(BorderStroke(2.dp, BorderBlack), RoundedCornerShape(8.dp))
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = banner.image,
            contentDescription = banner.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
        ) {
            Text(
                text = banner.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            if (banner.linkUrl.isNotEmpty()) {
                Text(
                    text = banner.linkUrl,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}
