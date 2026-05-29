package com.anix.app.ui.screens.giveaways

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import com.anix.app.core.network.ApiClient
import com.anix.app.core.theme.Background
import com.anix.app.core.theme.BorderBlack
import com.anix.app.core.theme.Primary
import com.anix.app.core.theme.Surface
import com.anix.app.data.models.Giveaway
import com.anix.app.data.models.TopGiver
import com.anix.app.ui.components.EmptyState
import com.anix.app.ui.components.ErrorState
import com.anix.app.ui.components.LoadingIndicator
import com.anix.app.ui.components.NeoBadge
import com.anix.app.ui.components.NeoCard

@Composable
fun GiveawayListScreen(
    onGiveawayClick: (String) -> Unit,
    viewModel: GiveawayListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().background(Surface).border(BorderStroke(2.dp, BorderBlack)).padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Giveaways", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }

            TabRow(
                selectedTabIndex = uiState.selectedTab,
                containerColor = Surface,
                contentColor = Primary
            ) {
                Tab(selected = uiState.selectedTab == 0, onClick = { viewModel.setSelectedTab(0) }, text = { Text("Giveaways") })
                Tab(selected = uiState.selectedTab == 1, onClick = { viewModel.setSelectedTab(1) }, text = { Text("Top Givers") })
            }

            when (uiState.selectedTab) {
                0 -> {
                    when {
                        uiState.isLoading -> LoadingIndicator()
                        uiState.error != null -> ErrorState(message = uiState.error!!, onRetry = { viewModel.loadGiveaways() })
                        uiState.giveaways.isEmpty() -> EmptyState(message = "No giveaways yet")
                        else -> {
                            LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(uiState.giveaways) { giveaway ->
                                    GiveawayCard(giveaway = giveaway, onClick = { onGiveawayClick(giveaway.id) })
                                }
                            }
                        }
                    }
                }
                1 -> {
                    if (uiState.topGivers.isEmpty()) {
                        EmptyState(message = "No top givers yet")
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            items(uiState.topGivers) { giver ->
                                TopGiverItem(giver = giver, rank = uiState.topGivers.indexOf(giver) + 1)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GiveawayCard(giveaway: Giveaway, onClick: () -> Unit) {
    NeoCard(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(giveaway.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
            if (giveaway.description.isNotEmpty()) {
                Text(giveaway.description, style = MaterialTheme.typography.bodySmall, color = Color.Gray, maxLines = 2)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NeoBadge(text = "Prize: ${giveaway.prize}", backgroundColor = Primary)
                Text("${giveaway.entries}/${giveaway.maxEntries} entries", style = MaterialTheme.typography.bodySmall, color = Primary, fontWeight = FontWeight.Bold)
            }
            if (giveaway.winnerName != null) {
                Spacer(modifier = Modifier.height(4.dp))
                NeoBadge(text = "Winner: ${giveaway.winnerName}", backgroundColor = Color(0xFF4CAF50))
            }
        }
    }
}

@Composable
private fun TopGiverItem(giver: TopGiver, rank: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("#$rank", fontWeight = FontWeight.Bold, color = Primary, style = MaterialTheme.typography.titleMedium, modifier = Modifier.width(36.dp))
        AsyncImage(
            model = ApiClient.resolveUrl(giver.avatar),
            contentDescription = "",
            modifier = Modifier.size(40.dp).clip(CircleShape).border(BorderStroke(2.dp, BorderBlack), CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(giver.username, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            Text("${giver.totalGiven} gifts", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}
