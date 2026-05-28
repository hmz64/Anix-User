package com.anix.app.ui.screens.clans
import androidx.compose.foundation.border

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.anix.app.data.models.ClanMember
import com.anix.app.data.models.ClanUpgrade
import com.anix.app.ui.components.EmptyState
import com.anix.app.ui.components.ErrorState
import com.anix.app.ui.components.LoadingIndicator
import com.anix.app.ui.components.NeoBadge
import com.anix.app.ui.components.NeoButton
import com.anix.app.ui.components.NeoCard
import kotlinx.coroutines.launch

@Composable
fun ClanDetailScreen(
    clanId: String,
    onBack: () -> Unit
) {
    var clan by remember { mutableStateOf<Clan?>(null) }
    var members by remember { mutableStateOf<List<ClanMember>>(emptyList()) }
    var upgrades by remember { mutableStateOf<List<ClanUpgrade>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var selectedTab by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(clanId) {
        val repo = ServiceLocator.getClanRepository()
        repo.getClanDetail(clanId).onSuccess { clan = it }.onFailure { error = it.message }
        repo.getClanMembers(clanId).onSuccess { members = it }
        repo.getUpgradeCatalog().onSuccess { upgrades = it }
        isLoading = false
    }

    if (isLoading) {
        LoadingIndicator()
    } else if (error != null && clan == null) {
        ErrorState(message = error!!, onRetry = { isLoading = true; error = null })
    } else if (clan != null) {
        val c = clan!!
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Surface)
                    .border(BorderStroke(2.dp, BorderBlack), RoundedCornerShape(0.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("← Back", modifier = Modifier.clickable { onBack() }, color = Primary, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(12.dp))
                Text(c.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = c.logo,
                    contentDescription = c.name,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .border(BorderStroke(3.dp, BorderBlack), CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(c.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("[${c.tag}]", style = MaterialTheme.typography.bodyMedium, color = Primary)
                Spacer(modifier = Modifier.height(8.dp))
                Text(c.description, style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    NeoBadge(text = "Level ${c.level}", backgroundColor = Primary)
                    NeoBadge(text = "${c.memberCount} Members", backgroundColor = Color.DarkGray)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NeoButton(text = "Join", onClick = {
                        scope.launch { ServiceLocator.getClanRepository().joinClan(clanId) }
                    }, backgroundColor = Primary)
                    NeoButton(text = "Donate", onClick = {
                        scope.launch { ServiceLocator.getClanRepository().donateToClan(clanId, 100) }
                    }, backgroundColor = Surface, textColor = Color.Black)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Members Section
                Text("Members", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                members.forEach { member ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .border(BorderStroke(1.dp, BorderBlack.copy(alpha = 0.3f)), RoundedCornerShape(6.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            model = member.avatar,
                            contentDescription = member.username,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(member.username, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                            Text(member.role, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                        NeoBadge(text = "Lv.${member.level}", backgroundColor = Primary)
                    }
                }
            }
        }
    }
}
