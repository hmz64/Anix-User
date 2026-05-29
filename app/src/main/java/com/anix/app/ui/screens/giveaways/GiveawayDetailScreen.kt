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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.anix.app.core.theme.AccentOrange
import com.anix.app.core.theme.Background
import com.anix.app.core.theme.BorderBlack
import com.anix.app.core.theme.Primary
import com.anix.app.core.theme.Surface
import com.anix.app.ui.components.ErrorState
import com.anix.app.ui.components.LoadingIndicator
import com.anix.app.ui.components.NeoBadge
import com.anix.app.ui.components.NeoButton
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

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

        var remainingMs by remember { mutableLongStateOf(parseEndsAt(g.endsAt)) }

        LaunchedEffect(g.endsAt) {
            remainingMs = parseEndsAt(g.endsAt)
            while (remainingMs > 0) {
                delay(1000)
                remainingMs -= 1000
            }
        }

        val days = TimeUnit.MILLISECONDS.toDays(remainingMs)
        val hours = TimeUnit.MILLISECONDS.toHours(remainingMs) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(remainingMs) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(remainingMs) % 60
        val isExpired = remainingMs <= 0

        Column(modifier = Modifier.fillMaxSize().background(Background).verticalScroll(rememberScrollState())) {
            Box(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(model = g.prizeImage, contentDescription = "", modifier = Modifier.fillMaxWidth().height(200.dp), contentScale = ContentScale.Crop)
                Text(
                    "← Back",
                    modifier = Modifier.align(Alignment.TopStart).padding(12.dp)
                        .background(Color.White, RoundedCornerShape(6.dp))
                        .border(BorderStroke(2.dp, BorderBlack), RoundedCornerShape(6.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .clickable { onBack() },
                    color = Color.Black, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(g.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(g.description, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(12.dp))
                Text("Prize: ${g.prize}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(12.dp))

                // Countdown
                Row(
                    modifier = Modifier.fillMaxWidth().background(Surface, RoundedCornerShape(8.dp))
                        .border(BorderStroke(2.dp, BorderBlack), RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CountdownUnit(value = days, label = "Days")
                    CountdownUnit(value = hours, label = "Hrs")
                    CountdownUnit(value = minutes, label = "Min")
                    CountdownUnit(value = seconds, label = "Sec")
                }

                Spacer(modifier = Modifier.height(8.dp))
                if (isExpired) {
                    NeoBadge(text = "Ended", backgroundColor = Color.Red)
                }

                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = if (g.maxEntries > 0) g.entries.toFloat() / g.maxEntries else 0f,
                    modifier = Modifier.fillMaxWidth().height(12.dp).clip(RoundedCornerShape(6.dp)).border(BorderStroke(1.dp, BorderBlack), RoundedCornerShape(6.dp)),
                    color = Primary, trackColor = Surface
                )
                Text("${g.entries}/${g.maxEntries} entries", style = MaterialTheme.typography.bodySmall, color = Color.Gray)

                Spacer(modifier = Modifier.height(12.dp))
                if (g.isEntered) {
                    NeoButton(text = "Already Joined", onClick = {}, backgroundColor = Surface, textColor = Color.Gray, modifier = Modifier.fillMaxWidth())
                } else if (!isExpired) {
                    NeoButton(text = "Join Giveaway", onClick = { viewModel.claimGiveaway(giveawayId) }, backgroundColor = Primary, modifier = Modifier.fillMaxWidth())
                } else {
                    NeoButton(text = "Giveaway Ended", onClick = {}, backgroundColor = Surface, textColor = Color.Gray, modifier = Modifier.fillMaxWidth())
                }

                if (g.winnerName != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    NeoBadge(text = "Winner: ${g.winnerName}", backgroundColor = Color(0xFF4CAF50))
                }
            }
        }
    }
}

@Composable
private fun CountdownUnit(value: Long, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "%02d".format(value),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = AccentOrange
        )
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
    }
}

private fun parseEndsAt(endsAt: String): Long {
    if (endsAt.isBlank()) return 0
    return try {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        format.isLenient = false
        val endDate = format.parse(endsAt) ?: Date()
        val remaining = endDate.time - System.currentTimeMillis()
        remaining.coerceAtLeast(0)
    } catch (e: Exception) {
        0
    }
}
