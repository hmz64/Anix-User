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
import com.anix.app.core.network.ApiClient
import com.anix.app.core.theme.AccentOrange
import com.anix.app.core.theme.Background
import com.anix.app.core.theme.BorderBlack
import com.anix.app.core.theme.Primary
import com.anix.app.core.theme.Surface
import com.anix.app.ui.components.ErrorState
import com.anix.app.ui.components.LoadingIndicator
import com.anix.app.ui.components.NeoBadge

@Composable
fun ClanDetailScreen(
    clanId: String,
    onBack: () -> Unit,
    viewModel: ClanDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(clanId) {
        viewModel.loadClan(clanId)
    }

    if (uiState.isLoading) {
        LoadingIndicator()
    } else if (uiState.error != null) {
        ErrorState(message = uiState.error!!, onRetry = { viewModel.loadClan(clanId) })
    } else if (uiState.clan != null) {
        val c = uiState.clan!!
        Column(modifier = Modifier.fillMaxSize().background(Background).verticalScroll(rememberScrollState())) {
            // Banner
            Box(modifier = Modifier.fillMaxWidth().height(140.dp).background(Primary)) {
                if (c.banner.isNotEmpty()) {
                    AsyncImage(model = ApiClient.resolveUrl(c.banner), contentDescription = "", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                }
                Text("← Back", modifier = Modifier.align(Alignment.TopStart).padding(12.dp).background(Color.White, RoundedCornerShape(6.dp)).border(BorderStroke(2.dp, BorderBlack), RoundedCornerShape(6.dp)).padding(horizontal = 12.dp, vertical = 6.dp).clickable { onBack() }, color = Color.Black, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
            }

            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                AsyncImage(model = ApiClient.resolveUrl(c.logo), contentDescription = "", modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)).border(BorderStroke(3.dp, BorderBlack), RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
                Spacer(modifier = Modifier.height(8.dp))
                Text(c.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    NeoBadge(text = c.tag, backgroundColor = AccentOrange)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Lv.${c.level}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(progress = 0.5f, modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)).border(BorderStroke(1.dp, BorderBlack), RoundedCornerShape(4.dp)), color = AccentOrange, trackColor = Surface)
                Spacer(modifier = Modifier.height(4.dp))
                Text("${c.memberCount} members", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                if (c.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(c.description, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}
