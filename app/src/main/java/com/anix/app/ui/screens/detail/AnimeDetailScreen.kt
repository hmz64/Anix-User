package com.anix.app.ui.screens.detail

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.anix.app.core.theme.AccentOrange
import com.anix.app.core.theme.Background
import com.anix.app.core.theme.BorderBlack
import com.anix.app.core.theme.Primary
import com.anix.app.core.theme.Surface
import com.anix.app.ui.components.EpisodeItem
import com.anix.app.ui.components.ErrorState
import com.anix.app.ui.components.LoadingIndicator
import com.anix.app.ui.components.NeoBadge
import com.anix.app.ui.components.NeoButton

@Composable
fun AnimeDetailScreen(
    animeId: String,
    onEpisodeClick: (String) -> Unit,
    onBack: () -> Unit,
    onCommentsClick: () -> Unit,
    viewModel: AnimeDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(animeId) {
        viewModel.loadAnime(animeId)
    }

    if (uiState.isLoading) {
        LoadingIndicator()
    } else if (uiState.error != null && uiState.anime == null) {
        ErrorState(message = uiState.error!!, onRetry = { viewModel.loadAnime(animeId) })
    } else if (uiState.anime != null) {
        val a = uiState.anime!!
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .verticalScroll(rememberScrollState())
        ) {
            // Banner Image
            Box(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = a.banner,
                    contentDescription = a.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2f / 1f),
                    contentScale = ContentScale.Crop
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2f / 1f)
                        .background(Color.Black.copy(alpha = 0.3f))
                )
                Text(
                    text = "← Back",
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp)
                        .background(Color.White, RoundedCornerShape(6.dp))
                        .border(BorderStroke(2.dp, BorderBlack), RoundedCornerShape(6.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .clickable { onBack() },
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelLarge
                )
                Text(
                    text = "Share",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .background(Primary, RoundedCornerShape(6.dp))
                        .border(BorderStroke(2.dp, BorderBlack), RoundedCornerShape(6.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .clickable {
                            val share = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, a.title)
                                putExtra(Intent.EXTRA_TEXT, "Watch ${a.title} on Anix!\n${a.description.take(100)}...")
                            }
                            context.startActivity(Intent.createChooser(share, "Share via"))
                        },
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            // Title and Info
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = a.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                if (a.titleJapanese.isNotEmpty()) {
                    Text(
                        text = a.titleJapanese,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Genre Tags
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(a.genres) { genre ->
                        NeoBadge(text = genre.name, backgroundColor = Primary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Info Row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NeoBadge(text = String.format("%.1f", a.rating), backgroundColor = AccentOrange)
                    NeoBadge(text = a.status, backgroundColor = Primary)
                    NeoBadge(text = a.type, backgroundColor = Color.DarkGray)
                    Text(text = "${a.totalEpisodes} eps", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    if (a.duration.isNotEmpty()) {
                        Text(text = a.duration, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tabs
                val tabs = listOf("Episodes", "Synopsis", "Details")
                TabRow(
                    selectedTabIndex = uiState.selectedTab,
                    containerColor = Surface,
                    contentColor = Primary
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = uiState.selectedTab == index,
                            onClick = { viewModel.setSelectedTab(index) },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (uiState.selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                when (uiState.selectedTab) {
                    0 -> {
                        // Episodes Tab
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Episodes (${uiState.episodes.size})",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            NeoButton(text = "Comments", onClick = onCommentsClick, backgroundColor = Surface, textColor = Color.Black)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        uiState.episodes.forEach { episode ->
                            EpisodeItem(episode = episode, onClick = { onEpisodeClick(episode.id) })
                        }
                    }
                    1 -> {
                        // Synopsis Tab
                        Text(
                            text = a.description,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    2 -> {
                        // Details Tab
                        DetailRow("Type", a.type)
                        DetailRow("Status", a.status)
                        DetailRow("Rating", String.format("%.1f", a.rating))
                        DetailRow("Episodes", a.totalEpisodes.toString())
                        DetailRow("Duration", a.duration.ifEmpty { "-" })
                        DetailRow("Release Year", a.releaseYear.toString())
                        if (a.titleJapanese.isNotEmpty()) DetailRow("Japanese Title", a.titleJapanese)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Favorite Button
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    NeoButton(
                        text = if (uiState.isFavorited) "Favorited" else "Add to Favorites",
                        onClick = { viewModel.toggleFavorite(animeId) },
                        backgroundColor = if (uiState.isFavorited) Color.Red else Primary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = Color.Gray, modifier = Modifier.weight(0.4f))
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.6f))
    }
}
