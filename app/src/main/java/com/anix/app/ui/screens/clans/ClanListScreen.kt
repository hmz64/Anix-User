package com.anix.app.ui.screens.clans
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.anix.app.core.di.ServiceLocator
import com.anix.app.core.theme.Background
import com.anix.app.core.theme.BorderBlack
import com.anix.app.core.theme.Primary
import com.anix.app.core.theme.Surface
import com.anix.app.data.models.Clan
import com.anix.app.ui.components.EmptyState
import com.anix.app.ui.components.ErrorState
import com.anix.app.ui.components.LoadingIndicator
import com.anix.app.ui.components.NeoButton
import com.anix.app.ui.components.NeoCard
import kotlinx.coroutines.launch

@Composable
fun ClanListScreen(
    onClanClick: (String) -> Unit
) {
    var clans by remember { mutableStateOf<List<Clan>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        ServiceLocator.getClanRepository().getClans()
            .onSuccess { clans = it }
            .onFailure { error = it.message }
        isLoading = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Clans",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            if (isLoading) {
                LoadingIndicator()
            } else if (error != null) {
                ErrorState(message = error!!, onRetry = { isLoading = true; error = null })
            } else if (clans.isEmpty()) {
                EmptyState(message = "No clans yet")
            } else {
                LazyColumn {
                    items(clans) { clan ->
                        ClanCard(
                            clan = clan,
                            onClick = { onClanClick(clan.id) },
                            onJoin = {
                                scope.launch {
                                    ServiceLocator.getClanRepository().joinClan(clan.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ClanCard(
    clan: Clan,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onJoin: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .background(Surface, RoundedCornerShape(8.dp))
            .border(BorderStroke(2.dp, BorderBlack), RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = clan.logo,
            contentDescription = clan.name,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .border(BorderStroke(2.dp, BorderBlack), CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = clan.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "[${clan.tag}]",
                    style = MaterialTheme.typography.bodySmall,
                    color = Primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Level ${clan.level} · ${clan.memberCount}/${clan.maxMembers} members",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
        NeoButton(
            text = "Join",
            onClick = onJoin,
            backgroundColor = Primary,
            modifier = Modifier.width(80.dp)
        )
    }
}
