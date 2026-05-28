package com.anix.app.ui.screens.profile
import androidx.compose.foundation.border

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.mutableIntStateOf
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
import com.anix.app.core.theme.AccentOrange
import com.anix.app.core.theme.Background
import com.anix.app.core.theme.BorderBlack
import com.anix.app.core.theme.Primary
import com.anix.app.core.theme.Surface
import com.anix.app.data.models.Comment
import com.anix.app.data.models.User
import com.anix.app.data.models.UserFavorite
import com.anix.app.data.models.UserStats
import com.anix.app.data.models.WatchHistory
import com.anix.app.ui.components.EmptyState
import com.anix.app.ui.components.ErrorState
import com.anix.app.ui.components.LoadingIndicator
import com.anix.app.ui.components.NeoBadge
import com.anix.app.ui.components.NeoButton
import com.anix.app.ui.components.NeoCard

@Composable
fun ProfileScreen(
    onSettingsClick: () -> Unit,
    onAnimeClick: (String) -> Unit,
    onLogout: () -> Unit
) {
    var user by remember { mutableStateOf<User?>(null) }
    var stats by remember { mutableStateOf<UserStats?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var selectedTab by remember { mutableIntStateOf(0) }
    var history by remember { mutableStateOf<List<WatchHistory>>(emptyList()) }
    var favorites by remember { mutableStateOf<List<UserFavorite>>(emptyList()) }
    var comments by remember { mutableStateOf<List<Comment>>(emptyList()) }

    LaunchedEffect(Unit) {
        val authRepo = ServiceLocator.getAuthRepository()
        val userRepo = ServiceLocator.getUserRepository()
        authRepo.me().onSuccess { user = it }.onFailure { error = it.message }
        userRepo.getUserStats().onSuccess { stats = it }
        userRepo.getWatchHistory().onSuccess { history = it }
        userRepo.getFavorites().onSuccess { favorites = it }
        userRepo.getUserComments().onSuccess { comments = it }
        isLoading = false
    }

    if (isLoading) {
        LoadingIndicator()
    } else if (error != null && user == null) {
        ErrorState(message = error!!, onRetry = { isLoading = true; error = null })
    } else if (user != null) {
        val u = user!!
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
                        AsyncImage(
                            model = u.avatar,
                            contentDescription = u.username,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .border(BorderStroke(3.dp, BorderBlack), CircleShape),
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
                        // XP Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("XP", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(8.dp))
                            LinearProgressIndicator(
                                progress = { if (u.xpToNextLevel > 0) u.xp.toFloat() / u.xpToNextLevel else 0f },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(12.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .border(BorderStroke(1.dp, BorderBlack), RoundedCornerShape(6.dp)),
                                color = AccentOrange,
                                trackColor = Surface
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("${u.xp}/${u.xpToNextLevel}", style = MaterialTheme.typography.bodySmall)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats row
                        if (stats != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatItem("Comments", "${stats!!.totalComments}")
                                StatItem("Favorites", "${stats!!.totalFavorites}")
                                StatItem("Watched", "${stats!!.totalWatched}")
                                StatItem("XP", "${stats!!.totalXp}")
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        NeoButton(
                            text = "Logout",
                            onClick = {
                                ServiceLocator.getAuthRepository().logout()
                                onLogout()
                            },
                            backgroundColor = Color.Red,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Tabs
                    val tabs = listOf("History", "Favorites", "Comments")
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = Surface,
                        contentColor = Primary
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = {
                                    Text(
                                        title,
                                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selectedTab == index) Primary else Color.Black
                                    )
                                }
                            )
                        }
                    }

                    when (selectedTab) {
                        0 -> { // History
                            if (history.isEmpty()) EmptyState(message = "No watch history")
                            else history.forEach { h ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 4.dp)
                                        .border(BorderStroke(1.dp, BorderBlack.copy(alpha = 0.3f)), RoundedCornerShape(6.dp))
                                        .padding(8.dp),
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
                            if (favorites.isEmpty()) EmptyState(message = "No favorites yet")
                            else favorites.forEach { f ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 4.dp)
                                        .border(BorderStroke(1.dp, BorderBlack.copy(alpha = 0.3f)), RoundedCornerShape(6.dp))
                                        .padding(8.dp),
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
                            if (comments.isEmpty()) EmptyState(message = "No comments yet")
                            else comments.forEach { c ->
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
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Primary)
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}
