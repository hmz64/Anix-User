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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.anix.app.core.theme.AccentBlue
import com.anix.app.core.theme.Background
import com.anix.app.core.theme.BorderBlack
import com.anix.app.core.theme.GlassBorder
import com.anix.app.core.theme.GlassSurface
import com.anix.app.core.theme.GlassSurfaceHigh
import com.anix.app.core.theme.Primary
import com.anix.app.core.theme.Surface
import com.anix.app.core.theme.TextMuted
import com.anix.app.core.theme.TextPrimary
import com.anix.app.core.theme.TextSecondary
import com.anix.app.data.models.AnimeSeries
import com.anix.app.data.models.Banner
import com.anix.app.data.models.ContinueWatchingItem
import com.anix.app.data.models.Genre
import com.anix.app.data.models.LeaderboardUser
import com.anix.app.data.models.MostWatchedEpisode
import com.anix.app.ui.components.AnimeCard
import com.anix.app.ui.components.ErrorState
import com.anix.app.ui.components.LoadingIndicator
import com.anix.app.ui.components.NeoBadge
import com.anix.app.ui.components.NeoChip
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    onAnimeClick: (String) -> Unit,
    onSeeAllClick: (String) -> Unit,
    onGenreClick: (String) -> Unit,
    onNotificationClick: () -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoading) {
        LoadingIndicator()
    } else if (uiState.error != null) {
        ErrorState(message = uiState.error!!, onRetry = { viewModel.loadData() })
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(top = 60.dp)
            ) {
            // 1. Banner Pager
            if (uiState.banners.isNotEmpty()) {
                BannerPager(banners = uiState.banners, onClick = { onAnimeClick(it) })
                Spacer(modifier = Modifier.height(8.dp))
            }

            // 2. XP Leaderboard
            if (uiState.leaderboard.isNotEmpty()) {
                SectionHeader(
                    title = "Leaderboard",
                    onSeeAll = { onSeeAllClick("leaderboard") }
                )
                LeaderboardSection(users = uiState.leaderboard.take(5))
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 3. Continue Watching
            if (uiState.continueWatching.isNotEmpty()) {
                SectionHeader(
                    title = "Lanjutkan Menonton",
                    onSeeAll = { onSeeAllClick("continue") }
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    uiState.continueWatching.take(5).forEach { item ->
                        ContinueWatchingCard(
                            item = item,
                            modifier = Modifier.width(200.dp),
                            onClick = { onAnimeClick(item.id.toString()) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 4. Trending Now
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

            // 5. Genres
            SectionHeader(
                title = "Genres",
                onSeeAll = { onSeeAllClick("genres") }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                uiState.genres.forEach { genre ->
                    NeoChip(text = genre.name, onClick = { onGenreClick(genre.name) })
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // 6. New Releases
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
            Spacer(modifier = Modifier.height(16.dp))

            // 7. Most Watched
            if (uiState.mostWatched.isNotEmpty()) {
                SectionHeader(
                    title = "Most Watched",
                    onSeeAll = { onSeeAllClick("most-watched") }
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.mostWatched.take(5).forEach { episode ->
                        MostWatchedRow(episode = episode, onClick = { onAnimeClick(episode.seriesId) })
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 8. Tayang Hari Ini
            if (uiState.schedule.isNotEmpty()) {
                SectionHeader(
                    title = "Tayang Hari Ini",
                    onSeeAll = { onSeeAllClick("schedule") }
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.schedule.take(5).forEach { anime ->
                        ScheduleItem(anime = anime, onClick = { onAnimeClick(anime.id) })
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Header overlay (sticky)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GlassSurfaceHigh)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Anix",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                Box {
                    IconButton(onClick = onNotificationClick) {
                        Icon(
                            imageVector = if (uiState.unreadCount > 0) Icons.Filled.Notifications else Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = TextPrimary
                        )
                    }
                    if (uiState.unreadCount > 0) {
                        NeoBadge(
                            text = if (uiState.unreadCount > 99) "99+" else uiState.unreadCount.toString(),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(end = 4.dp, top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardSection(users: List<LeaderboardUser>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        users.forEachIndexed { index, user ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(GlassSurface)
                    .border(BorderStroke(1.dp, GlassBorder), RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#${index + 1}",
                    color = if (index < 3) AccentBlue else TextMuted,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.width(32.dp)
                )
                AsyncImage(
                    model = user.avatar,
                    contentDescription = null,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .border(BorderStroke(1.dp, GlassBorder), CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(user.username, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Lv.${user.level} - ${user.xp} XP", color = TextMuted, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun ContinueWatchingCard(
    item: ContinueWatchingItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(GlassSurface)
            .border(BorderStroke(1.dp, GlassBorder), RoundedCornerShape(12.dp))
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = item.coverUrl,
            contentDescription = item.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(8.dp)
        ) {
            Text(
                text = item.title,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "Eps. ${item.episodeNumber}",
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun MostWatchedRow(episode: MostWatchedEpisode, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(GlassSurface)
            .border(BorderStroke(1.dp, GlassBorder), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = episode.coverUrl,
            contentDescription = episode.title,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(6.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(episode.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("Ep. ${episode.number}", color = TextSecondary, fontSize = 11.sp)
        }
        Text("${episode.viewCount}", color = AccentBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}

@Composable
private fun BannerPager(banners: List<Banner>, onClick: (String) -> Unit) {
    val pagerState = rememberPagerState(pageCount = { banners.size })
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        while (true) {
            delay(4000)
            val next = (pagerState.currentPage + 1) % banners.size
            pagerState.animateScrollToPage(next)
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) { page ->
            val banner = banners[page]
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(BorderStroke(1.dp, GlassBorder), RoundedCornerShape(12.dp))
                    .clickable {
                        if (banner.linkUrl.isNotEmpty()) onClick(banner.linkUrl)
                    }
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
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(bottom = 4.dp)
        ) {
            repeat(banners.size) { index ->
                Box(
                    modifier = Modifier
                        .size(if (pagerState.currentPage == index) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage == index) Primary else Color.Gray.copy(alpha = 0.5f)
                        )
                )
            }
        }
    }
}

@Composable
private fun ScheduleItem(anime: AnimeSeries, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(GlassSurface)
            .border(BorderStroke(1.dp, GlassBorder), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = anime.poster,
            contentDescription = anime.title,
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(6.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = anime.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "${anime.type} • ${anime.totalEpisodes} eps",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
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
            fontWeight = FontWeight.Bold,
            color = TextPrimary
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
