package com.anix.app.ui.screens.detail
import androidx.compose.foundation.border

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.anix.app.core.di.ServiceLocator
import com.anix.app.core.theme.AccentOrange
import com.anix.app.core.theme.Background
import com.anix.app.core.theme.BorderBlack
import com.anix.app.core.theme.Primary
import com.anix.app.core.theme.Surface
import com.anix.app.data.models.AnimeSeries
import com.anix.app.data.models.Episode
import com.anix.app.ui.components.EpisodeItem
import com.anix.app.ui.components.ErrorState
import com.anix.app.ui.components.LoadingIndicator
import com.anix.app.ui.components.NeoBadge
import com.anix.app.ui.components.NeoButton
import com.anix.app.ui.components.NeoCard
import kotlinx.coroutines.launch

@Composable
fun AnimeDetailScreen(
    animeId: String,
    onEpisodeClick: (String) -> Unit,
    onBack: () -> Unit,
    onCommentsClick: () -> Unit
) {
    var anime by remember { mutableStateOf<AnimeSeries?>(null) }
    var episodes by remember { mutableStateOf<List<Episode>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var isFavorited by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(animeId) {
        val repo = ServiceLocator.getAnimeRepository()
        repo.getAnimeDetail(animeId).onSuccess {
            anime = it
            isFavorited = it.isFavorited
        }.onFailure { error = it.message }

        repo.getAnimeEpisodes(animeId).onSuccess {
            episodes = it
        }
        isLoading = false
    }

    if (isLoading) {
        LoadingIndicator()
    } else if (error != null && anime == null) {
        ErrorState(message = error!!, onRetry = { isLoading = true; error = null })
    } else if (anime != null) {
        val a = anime!!
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
                // Back button
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
                    NeoBadge(
                        text = String.format("%.1f", a.rating),
                        backgroundColor = AccentOrange
                    )
                    NeoBadge(text = a.status, backgroundColor = Primary)
                    NeoBadge(text = a.type, backgroundColor = Color.DarkGray)
                    Text(
                        text = "${a.totalEpisodes} eps",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Synopsis
                Text(
                    text = a.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Favorite Button
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    NeoButton(
                        text = if (isFavorited) "Favorited" else "Add to Favorites",
                        onClick = {
                            scope.launch {
                                ServiceLocator.getUserRepository().toggleFavorite(animeId)
                                isFavorited = !isFavorited
                            }
                        },
                        backgroundColor = if (isFavorited) Color.Red else Primary,
                        modifier = Modifier.weight(1f)
                    )
                    NeoButton(
                        text = "Comments",
                        onClick = onCommentsClick,
                        backgroundColor = Surface,
                        textColor = Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Episodes Section
                Text(
                    text = "Episodes (${episodes.size})",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                episodes.forEach { episode ->
                    EpisodeItem(
                        episode = episode,
                        onClick = { onEpisodeClick(episode.id) }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
