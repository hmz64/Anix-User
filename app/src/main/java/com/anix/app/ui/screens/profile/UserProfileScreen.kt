package com.anix.app.ui.screens.profile

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
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.anix.app.core.theme.AccentOrange
import com.anix.app.core.theme.Background
import com.anix.app.core.theme.BorderBlack
import com.anix.app.core.theme.Primary
import com.anix.app.core.theme.Surface
import com.anix.app.data.model.User
import com.anix.app.data.model.UserStats
import com.anix.app.ui.components.ErrorState
import com.anix.app.ui.components.LoadingIndicator
import com.anix.app.ui.components.NeoBadge
import com.anix.app.ui.components.NeoButton
import com.anix.app.util.ServiceLocator

data class UserProfileUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val user: User? = null,
    val isFriend: Boolean = false,
    val hasSentRequest: Boolean = false
)

class UserProfileViewModel {
    var uiState by mutableStateOf(UserProfileUiState())
        private set

    fun loadProfile(userId: String) {
        uiState = uiState.copy(isLoading = true, error = null)
        try {
            val profile = ServiceLocator.getUserRepository().getProfile(userId)
            uiState = uiState.copy(
                isLoading = false,
                user = profile.user,
                isFriend = profile.isFriend,
                hasSentRequest = profile.hasSentRequest
            )
        } catch (e: Exception) {
            uiState = uiState.copy(
                isLoading = false,
                error = e.message ?: "Failed to load profile"
            )
        }
    }

    fun sendFriendRequest(userId: String) {
        try {
            ServiceLocator.getChatRepository().sendFriendRequest(userId)
            uiState = uiState.copy(hasSentRequest = true)
        } catch (e: Exception) {
            uiState = uiState.copy(
                error = e.message ?: "Failed to send friend request"
            )
        }
    }
}

@Composable
fun UserProfileScreen(
    userId: String,
    onBack: () -> Unit,
    onChatClick: (String) -> Unit
) {
    val viewModel = remember { UserProfileViewModel() }
    val state = viewModel.uiState

    LaunchedEffect(userId) {
        viewModel.loadProfile(userId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
    ) {
        Header(onBack = onBack)

        when {
            state.isLoading -> {
                LoadingIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp)
                )
            }
            state.error != null -> {
                ErrorState(
                    message = state.error!!,
                    onRetry = { viewModel.loadProfile(userId) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp)
                )
            }
            state.user == null -> {
                EmptyState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp)
                )
            }
            else -> {
                ProfileContent(
                    user = state.user!!,
                    isFriend = state.isFriend,
                    hasSentRequest = state.hasSentRequest,
                    onChatClick = { onChatClick(userId) },
                    onSendFriendRequest = { viewModel.sendFriendRequest(userId) }
                )
            }
        }
    }
}

@Composable
private fun Header(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Surface)
            .border(2.dp, BorderBlack)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "\u2190 Back",
            color = Primary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clickable(onClick = onBack)
                .border(2.dp, BorderBlack)
                .background(Surface)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun ProfileContent(
    user: User,
    isFriend: Boolean,
    hasSentRequest: Boolean,
    onChatClick: () -> Unit,
    onSendFriendRequest: () -> Unit
) {
    val isLimited = user.privacySetting == "private"

    BannerSection(bannerUrl = user.banner)

    ProfileInfoSection(user = user, isLimited = isLimited)

    if (!isLimited) {
        StatsSection(userId = user.id)
    }

    Spacer(modifier = Modifier.height(24.dp))

    ActionButtons(
        onChatClick = onChatClick,
        onSendFriendRequest = onSendFriendRequest,
        isFriend = isFriend,
        hasSentRequest = hasSentRequest,
        isLimited = isLimited
    )

    Spacer(modifier = Modifier.height(32.dp))
}

@Composable
private fun BannerSection(bannerUrl: String?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(Primary)
            .border(2.dp, BorderBlack)
    ) {
        if (!bannerUrl.isNullOrBlank()) {
            AsyncImage(
                model = bannerUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun ProfileInfoSection(user: User, isLimited: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(top = (-40).dp)
                .size(80.dp)
                .clip(CircleShape)
                .border(3.dp, BorderBlack, CircleShape)
                .background(Surface)
        ) {
            if (!user.avatar.isNullOrBlank()) {
                AsyncImage(
                    model = user.avatar,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = user.username,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = BorderBlack
        )

        Spacer(modifier = Modifier.height(4.dp))

        NeoBadge(
            text = "Lv. ${user.level}",
            color = AccentOrange
        )

        if (user.bio != null && !isLimited) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = user.bio,
                fontSize = 14.sp,
                color = BorderBlack,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }

        if (isLimited) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "This profile is private",
                fontSize = 13.sp,
                color = BorderBlack,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun StatsSection(userId: String) {
    val stats = remember(userId) {
        try {
            ServiceLocator.getUserRepository().getUserStats(userId)
        } catch (_: Exception) {
            null
        }
    }

    if (stats != null) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .border(2.dp, BorderBlack)
                .background(Surface)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItem(label = "Comments", value = stats.totalComments)
            StatItem(label = "Favorites", value = stats.totalFavorites)
            StatItem(label = "Watched", value = stats.totalWatched)
            StatItem(label = "XP", value = stats.totalXp)
        }
    }
}

@Composable
private fun StatItem(label: String, value: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value.toString(),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Primary
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = BorderBlack
        )
    }
}

@Composable
private fun ActionButtons(
    onChatClick: () -> Unit,
    onSendFriendRequest: () -> Unit,
    isFriend: Boolean,
    hasSentRequest: Boolean,
    isLimited: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        NeoButton(
            text = "Send Message",
            onClick = onChatClick,
            modifier = Modifier.weight(1f)
        )

        if (!isLimited && !isFriend) {
            NeoButton(
                text = if (hasSentRequest) "Request Sent" else "Add Friend",
                onClick = onSendFriendRequest,
                enabled = !hasSentRequest,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "User not found",
            fontSize = 16.sp,
            color = BorderBlack,
            textAlign = TextAlign.Center
        )
    }
}
