package com.anix.app.ui.screens.profile

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.anix.app.core.theme.AccentOrange
import com.anix.app.core.theme.Background
import com.anix.app.core.theme.BorderBlack
import com.anix.app.core.theme.Primary
import com.anix.app.core.theme.Surface
import com.anix.app.ui.components.EmptyState
import com.anix.app.ui.components.ErrorState
import com.anix.app.ui.components.LoadingIndicator
import com.anix.app.ui.components.NeoBadge
import com.anix.app.ui.components.NeoButton

@Composable
fun ProfileScreen(
    onSettingsClick: () -> Unit,
    onAnimeClick: (String) -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoading) {
        LoadingIndicator()
    } else if (uiState.error != null && uiState.user == null) {
        ErrorState(message = uiState.error!!, onRetry = { viewModel.loadProfile() })
    } else if (uiState.user != null) {
        val u = uiState.user!!
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .background(Primary)
                    ) {
                        if (u.banner.isNotEmpty()) {
                            AsyncImage(
                                model = u.banner,
                                contentDescription = "Banner",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                        ) {
                            NeoButton(
                                text = "Settings",
                                onClick = onSettingsClick,
                                backgroundColor = Surface,
                                textColor = Color.Black
                            )
                        }
                    }

                    // Avatar and name
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Clickable avatar → Settings
                        AsyncImage(
                            model = u.avatar,
                            contentDescription = u.username,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .border(BorderStroke(3.dp, BorderBlack), CircleShape)
                                .clickable { onSettingsClick() },
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(u.username, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            NeoBadge(text = "Lv.${u.level}", backgroundColor = AccentOrange)
                            if (u.premium) {
                                Spacer(modifier = Modifier.width(6.dp))
                                NeoBadge(text = "PREMIUM", backgroundColor = Primary)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        // XP Bar with animation
                        XPBar(currentXp = u.xp, maxXp = u.xpToNextLevel)

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats row
                        if (uiState.stats != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatItem("Comments", "${uiState.stats!!.totalComments}")
                                StatItem("Favorites", "${uiState.stats!!.totalFavorites}")
                                StatItem("Watched", "${uiState.stats!!.totalWatched}")
                                StatItem("XP", "${uiState.stats!!.totalXp}")
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Tabs
                    val tabs = listOf("History", "Favorites", "Comments")
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
                                        title,
                                        fontWeight = if (uiState.selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                        color = if (uiState.selectedTab == index) Primary else Color.Black
                                    )
                                }
                            )
                        }
                    }

                    when (uiState.selectedTab) {
                        0 -> { // History
                            if (uiState.history.isEmpty()) EmptyState(message = "No watch history")
                            else uiState.history.forEach { h ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 4.dp)
                                        .border(BorderStroke(1.dp, BorderBlack.copy(alpha = 0.3f)), RoundedCornerShape(6.dp))
                                        .padding(8.dp)
                                        .clickable { h.anime?.let { onAnimeClick(it.id) } },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = h.anime?.poster ?: "",
                                        contentDescription = "",
                                        modifier = Modifier.size(48.dp).clip(RoundedCornerShape(4.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(h.anime?.title ?: "", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                                        Text("Ep ${h.episode?.number ?: ""}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                    }
                                }
                            }
                        }
                        1 -> { // Favorites
                            if (uiState.favorites.isEmpty()) EmptyState(message = "No favorites yet")
                            else uiState.favorites.forEach { f ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 4.dp)
                                        .border(BorderStroke(1.dp, BorderBlack.copy(alpha = 0.3f)), RoundedCornerShape(6.dp))
                                        .padding(8.dp)
                                        .clickable { f.anime?.let { onAnimeClick(it.id) } },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = f.anime?.poster ?: "",
                                        contentDescription = "",
                                        modifier = Modifier.size(48.dp).clip(RoundedCornerShape(4.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(f.anime?.title ?: "", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                        2 -> { // Comments
                            if (uiState.comments.isEmpty()) EmptyState(message = "No comments yet")
                            else uiState.comments.forEach { c ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 4.dp)
                                        .border(BorderStroke(1.dp, BorderBlack.copy(alpha = 0.3f)), RoundedCornerShape(6.dp))
                                        .padding(8.dp)
                                ) {
                                    Text(c.content, style = MaterialTheme.typography.bodyMedium)
                                    Text(c.createdAt.take(10), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun XPBar(currentXp: Int, maxXp: Int) {
    val targetProgress = if (maxXp > 0) currentXp.toFloat() / maxXp.toFloat() else 0f
    var animatedProgress by remember { mutableFloatStateOf(0f) }
    val animProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(1000),
        label = "xpProgress"
    )
    LaunchedEffect(targetProgress) { animatedProgress = targetProgress }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("XP", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(8.dp))
        LinearProgressIndicator(
            progress = animProgress,
            modifier = Modifier
                .weight(1f)
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
                .border(BorderStroke(1.dp, BorderBlack), RoundedCornerShape(6.dp)),
            color = AccentOrange,
            trackColor = Surface
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("$currentXp/$maxXp", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Primary)
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}
