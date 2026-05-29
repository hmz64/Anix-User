package com.anix.app.ui.screens.clans

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anix.app.core.theme.AccentOrange
import com.anix.app.core.theme.Background
import com.anix.app.core.theme.BorderBlack
import com.anix.app.core.theme.Primary
import com.anix.app.core.theme.Surface
import com.anix.app.data.models.Clan
import com.anix.app.ui.components.EmptyState
import com.anix.app.ui.components.ErrorState
import com.anix.app.ui.components.LoadingIndicator
import com.anix.app.ui.components.NeoBadge
import com.anix.app.ui.components.NeoButton
import com.anix.app.ui.components.NeoCard
import com.anix.app.ui.components.NeoTextField

@Composable
fun ClanListScreen(
    onClanClick: (String) -> Unit,
    viewModel: ClanListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showCreateDialog by remember { mutableStateOf(false) }
    var createName by remember { mutableStateOf("") }
    var createTag by remember { mutableStateOf("") }
    var createDesc by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().background(Surface).border(BorderStroke(2.dp, BorderBlack)).padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Clans", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                NeoButton(text = "Create Clan", onClick = { showCreateDialog = true }, backgroundColor = Primary)
            }

            TabRow(
                selectedTabIndex = uiState.selectedTab,
                containerColor = Surface,
                contentColor = Primary
            ) {
                Tab(selected = uiState.selectedTab == 0, onClick = { viewModel.setSelectedTab(0) }, text = { Text("Clans") })
                Tab(selected = uiState.selectedTab == 1, onClick = { viewModel.setSelectedTab(1) }, text = { Text("Leaderboard") })
                Tab(selected = uiState.selectedTab == 2, onClick = { viewModel.setSelectedTab(2) }, text = { Text("My Clan") })
            }

            when (uiState.selectedTab) {
                0 -> {
                    when {
                        uiState.isLoading -> LoadingIndicator()
                        uiState.error != null -> ErrorState(message = uiState.error!!, onRetry = { viewModel.loadClans() })
                        uiState.clans.isEmpty() -> EmptyState(message = "No clans yet")
                        else -> ClanList(uiState.clans, onClanClick)
                    }
                }
                1 -> {
                    if (uiState.leaderboard.isEmpty()) {
                        EmptyState(message = "No rankings yet")
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            items(uiState.leaderboard) { clan ->
                                LeaderboardItem(clan = clan, onClick = { onClanClick(clan.id) })
                            }
                        }
                    }
                }
                2 -> {
                    if (uiState.myClan != null) {
                        MyClanItem(clan = uiState.myClan!!, onClick = { onClanClick(uiState.myClan!!.id) })
                    } else {
                        EmptyState(message = "You are not in a clan")
                    }
                }
            }
        }

        if (showCreateDialog) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)).clickable { showCreateDialog = false }) {
                Column(
                    modifier = Modifier.align(Alignment.Center).fillMaxWidth(0.9f).background(Color.White, RoundedCornerShape(12.dp)).border(BorderStroke(2.dp, BorderBlack)).padding(16.dp)
                ) {
                    Text("Create Clan", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    NeoTextField(value = createName, onValueChange = { createName = it }, placeholder = "Clan Name", modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    NeoTextField(value = createTag, onValueChange = { createTag = it }, placeholder = "Tag (3 chars)", modifier = Modifier.fillMaxWidth(), singleLine = true)
                    Spacer(modifier = Modifier.height(8.dp))
                    NeoTextField(value = createDesc, onValueChange = { createDesc = it }, placeholder = "Description (optional)", modifier = Modifier.fillMaxWidth(), singleLine = false)
                    Spacer(modifier = Modifier.height(12.dp))
                    NeoButton(
                        text = "Create",
                        onClick = {
                            viewModel.createClan(createName, createTag, createDesc)
                            showCreateDialog = false
                        },
                        backgroundColor = Primary,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun ClanList(clans: List<Clan>, onClanClick: (String) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(clans) { clan ->
            NeoCard(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                onClick = { onClanClick(clan.id) }
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(clan.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            NeoBadge(text = clan.tag, backgroundColor = AccentOrange)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Lv.${clan.level}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                    }
                    Text("${clan.memberCount} members", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
private fun LeaderboardItem(clan: Clan, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("#${clan.level}", fontWeight = FontWeight.Bold, color = Primary, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(clan.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            Text("Level ${clan.level}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Text("${clan.xp} XP", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun MyClanItem(clan: Clan, onClick: () -> Unit) {
    NeoCard(
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(clan.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
            Row(verticalAlignment = Alignment.CenterVertically) {
                NeoBadge(text = clan.tag, backgroundColor = AccentOrange)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Lv.${clan.level}", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text("${clan.memberCount} members", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}