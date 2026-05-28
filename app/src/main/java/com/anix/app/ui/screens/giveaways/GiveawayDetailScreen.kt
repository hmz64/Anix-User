package com.anix.app.ui.screens.giveaways

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.anix.app.core.theme.AccentOrange
import com.anix.app.core.theme.Background
import com.anix.app.core.theme.BorderBlack
import com.anix.app.core.theme.Primary
import com.anix.app.core.theme.Surface
import com.anix.app.data.models.Giveaway
import com.anix.app.ui.components.ErrorState
import com.anix.app.ui.components.LoadingIndicator
import com.anix.app.ui.components.NeoBadge
import com.anix.app.ui.components.NeoButton
import com.anix.app.ui.components.NeoCard
import kotlinx.coroutines.launch

@Composable
fun GiveawayDetailScreen(
    giveawayId: String,
    onBack: () -> Unit
) {
    var giveaway by remember { mutableStateOf<Giveaway?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    var claimed by remember { mutableStateOf(false) }

    LaunchedEffect(giveawayId) {
        ServiceLocator.getGiveawayRepository().getGiveawayDetail(giveawayId)
            .onSuccess { giveaway = it; claimed = it.isEntered }
            .onFailure { error = it.message }
        isLoading = false
    }

    if (isLoading) {
        LoadingIndicator()
    } else if (error != null && giveaway == null) {
        ErrorState(message = error!!, onRetry = { isLoading = true; error = null })
    } else if (giveaway != null) {
        val g = giveaway!!
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Background)
                .verticalScroll(rememberScrollState())
        ) {
            // Prize Image
            AsyncImage(
                model = g.prizeImage,
                contentDescription = g.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(16.dp)) {
                Text(g.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(g.prize, style = MaterialTheme.typography.titleMedium, color = AccentOrange, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NeoBadge(text = "${g.entries} entries", backgroundColor = Primary)
                    if (g.maxEntries > 0) {
                        NeoBadge(text = "Max ${g.maxEntries}", backgroundColor = Color.DarkGray)
                    }
                    if (g.isActive) {
                        NeoBadge(text = "Active", backgroundColor = AccentOrange)
                    } else {
                        NeoBadge(text = "Ended", backgroundColor = Color.Red)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Description", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(g.description, style = MaterialTheme.typography.bodyMedium)

                Spacer(modifier = Modifier.height(8.dp))
                Text("Created by ${g.creatorName}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)

                if (g.winnerId != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Winner: ${g.winnerName}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = AccentOrange)
                }

                Spacer(modifier = Modifier.height(24.dp))

                if (g.isActive && !claimed) {
                    NeoButton(
                        text = "Claim Prize",
                        onClick = {
                            scope.launch {
                                ServiceLocator.getGiveawayRepository().claimGiveaway(giveawayId)
                                    .onSuccess { claimed = true }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = Primary
                    )
                } else if (claimed) {
                    NeoButton(
                        text = "Already Entered",
                        onClick = { },
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                NeoButton(
                    text = "Back",
                    onClick = onBack,
                    backgroundColor = Surface,
                    textColor = Color.Black,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
