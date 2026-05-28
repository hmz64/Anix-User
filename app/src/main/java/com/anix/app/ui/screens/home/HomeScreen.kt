package com.anix.app.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.anix.app.core.di.ServiceLocator
import com.anix.app.core.theme.Background
import com.anix.app.core.theme.BorderBlack
import com.anix.app.core.theme.Primary
import com.anix.app.data.models.AnimeSeries
import com.anix.app.data.models.Banner
import com.anix.app.data.models.Genre
import com.anix.app.ui.components.AnimeCard
import com.anix.app.ui.components.ErrorState
import com.anix.app.ui.components.LoadingIndicator
import com.anix.app.ui.components.NeoChip

@Composable
fun HomeScreen(
    onAnimeClick: (String) -> Unit,
    onSeeAllClick: (String) -> Unit
) {
    var trendingAnime by remember { mutableStateOf<List<AnimeSeries>>(emptyList()) }
    var newReleases by remember { mutableStateOf<List<AnimeSeries>>(emptyList()) }
    var genres by remember { mutableStateOf<List<Genre>>(emptyList()) }
    var banners by remember { mutableStateOf<List<Banner>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val repo = ServiceLocator.getAnimeRepository()
        try {
            repo.getAnimeList(sort = "rating").onSuccess { trendingAnime = it }
            repo.getAnimeList(sort = "newest").onSuccess { newReleases = it }
            repo.getGenres().onSuccess { genres = it }
            repo.getBanners().onSuccess { banners = it }
            error = null
        } catch (e: Exception) {
            error = e.message
        }
        isLoading = false
    }

    if (isLoading) {
        LoadingIndicator()
    } else if (error != null) {
        ErrorState(message = error!!, onRetry = { isLoading = true; error = null })
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .verticalScroll(rememberScrollState())
        ) {
            // Banner Carousel
            if (banners.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    banners.forEach { banner ->
                        BannerItem(
                            banner = banner,
                            modifier = Modifier
                                .width(340.dp)
                                .height(180.dp),
                            onClick = { onAnimeClick(banner.animeId) }
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
                trendingAnime.take(10).forEach { anime ->
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
                genres.forEach { genre ->
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
                newReleases.take(10).forEach { anime ->
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
            if (banner.subtitle.isNotEmpty()) {
                Text(
                    text = banner.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}
