package com.anix.app.ui.screens.giveaways
import androidx.compose.foundation.border

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.anix.app.core.di.ServiceLocator
import com.anix.app.core.theme.AccentOrange
import com.anix.app.core.theme.Background
import com.anix.app.core.theme.BorderBlack
import com.anix.app.core.theme.Primary
import com.anix.app.core.theme.Surface
import com.anix.app.data.models.Giveaway
import com.anix.app.ui.components.EmptyState
import com.anix.app.ui.components.ErrorState
import com.anix.app.ui.components.LoadingIndicator
import com.anix.app.ui.components.NeoBadge
import com.anix.app.ui.components.NeoButton
import com.anix.app.ui.components.NeoCard

@Composable
fun GiveawayListScreen(
    onGiveawayClick: (String) -> Unit
) {
    var giveaways by remember { mutableStateOf<List<Giveaway>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        ServiceLocator.getGiveawayRepository().getGiveaways()
            .onSuccess { giveaways = it }
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
                text = "Giveaways",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            if (isLoading) {
                LoadingIndicator()
            } else if (error != null) {
                ErrorState(message = error!!, onRetry = { isLoading = true; error = null })
            } else if (giveaways.isEmpty()) {
                EmptyState(message = "No active giveaways")
            } else {
                LazyColumn {
                    items(giveaways) { giveaway ->
                        GiveawayCard(
                            giveaway = giveaway,
                            onClick = { onGiveawayClick(giveaway.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GiveawayCard(
    giveaway: Giveaway,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .background(Surface, RoundedCornerShape(8.dp))
            .border(BorderStroke(2.dp, BorderBlack), RoundedCornerShape(8.dp))
    ) {
        AsyncImage(
            model = giveaway.prizeImage,
            contentDescription = giveaway.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
            contentScale = ContentScale.Crop
        )
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = giveaway.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = giveaway.prize,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NeoBadge(
                    text = "${giveaway.entries} entries",
                    backgroundColor = AccentOrange
                )
                NeoButton(
                    text = if (giveaway.isEntered) "Entered" else "Enter",
                    onClick = onClick,
                    backgroundColor = if (giveaway.isEntered) Color.Gray else Primary,
                    modifier = Modifier.width(100.dp)
                )
            }
        }
    }
}
