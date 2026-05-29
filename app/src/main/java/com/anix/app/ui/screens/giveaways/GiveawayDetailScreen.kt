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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.anix.app.core.theme.Background
import com.anix.app.core.theme.BorderBlack
import com.anix.app.core.theme.Primary
import com.anix.app.core.theme.Surface
import com.anix.app.ui.components.ErrorState
import com.anix.app.ui.components.LoadingIndicator
import com.anix.app.ui.components.NeoButton

@Composable
fun GiveawayDetailScreen(
    giveawayId: String,
    onBack: () -> Unit,
    viewModel: GiveawayDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(giveawayId) {
        viewModel.loadGiveaway(giveawayId)
    }

    if (uiState.isLoading) {
        LoadingIndicator()
    } else if (uiState.error != null) {
        ErrorState(message = uiState.error!!, onRetry = { viewModel.loadGiveaway(giveawayId) })
    } else if (uiState.giveaway != null) {
        val g = uiState.giveaway!!
        Column(modifier = Modifier.fillMaxSize().background(Background).verticalScroll(rememberScrollState())) {
            Box(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(model = g.image, contentDescription = "", modifier = Modifier.fillMaxWidth().height(200.dp), contentScale = ContentScale.Crop)
                Text("← Back", modifier = Modifier.align(Alignment.TopStart).padding(12.dp).background(Color.White, RoundedCornerShape(6.dp)).border(BorderStroke(2.dp, BorderBlack), RoundedCornerShape(6.dp)).padding(horizontal = 12.dp, vertical = 6.dp).clickable { onBack() }, color = Color.Black, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(g.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(g.description, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Prize: ${g.prize}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = if (g.maxEntries > 0) g.entries.toFloat() / g.maxEntries else 0f,
                    modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp)).border(BorderStroke(1.dp, BorderBlack), RoundedCornerShape(6.dp)),
                    color = Primary, trackColor = Surface
                )
                Text("${g.entries}/${g.maxEntries} entries", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))
                if (g.isEntered) {
                    NeoButton(text = "Already Joined", onClick = {}, backgroundColor = Surface, textColor = Color.Gray, modifier = Modifier.fillMaxWidth())
                } else {
                    NeoButton(text = "Join Giveaway", onClick = { viewModel.claimGiveaway(giveawayId) }, backgroundColor = Primary, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}
