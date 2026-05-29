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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.anix.app.core.theme.Background
import com.anix.app.core.theme.BorderBlack
import com.anix.app.core.theme.Primary
import com.anix.app.core.theme.Surface
import com.anix.app.ui.components.ErrorState
import com.anix.app.ui.components.LoadingIndicator
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
                modifier = Modifier.fillMaxWidth().background(Surface).border(BorderStroke(2.dp, BorderBlack), RoundedCornerShape(0.dp)).padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Giveaways", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }

            when {
                uiState.isLoading -> LoadingIndicator()
                uiState.error != null -> ErrorState(message = uiState.error!!, onRetry = { viewModel.loadGiveaways() })
                uiState.giveaways.isEmpty() -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No giveaways", color = Color.Gray) }
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(uiState.giveaways) { giveaway ->
                            NeoCard(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                                onClick = { onGiveawayClick(giveaway.id) }
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(giveaway.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                                    if (giveaway.description.isNotEmpty()) {
                                        Text(giveaway.description, style = MaterialTheme.typography.bodySmall, color = Color.Gray, maxLines = 2)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("${giveaway.entries}/${giveaway.maxEntries} entries", style = MaterialTheme.typography.bodySmall, color = Primary, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
