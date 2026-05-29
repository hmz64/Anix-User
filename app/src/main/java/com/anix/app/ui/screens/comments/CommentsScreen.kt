package com.anix.app.ui.screens.comments

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.anix.app.ui.components.NeoTextField

@Composable
fun CommentsScreen(
    animeId: String,
    episodeId: String,
    onBack: () -> Unit,
    viewModel: CommentsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var commentText by remember { mutableStateOf("") }

    LaunchedEffect(episodeId) {
        viewModel.loadComments(episodeId)
    }

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().background(Surface).border(BorderStroke(2.dp, BorderBlack), RoundedCornerShape(0.dp)).padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("← Back", modifier = Modifier.clickable { onBack() }, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = Primary)
                Spacer(modifier = Modifier.width(12.dp))
                Text("Comments", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            if (uiState.isLoading) {
                LoadingIndicator()
            } else if (uiState.error != null) {
                ErrorState(message = uiState.error!!, onRetry = { viewModel.loadComments(episodeId) })
            } else {
                LazyColumn(modifier = Modifier.weight(1f).padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(uiState.comments) { comment ->
                        Column(
                            modifier = Modifier.fillMaxWidth().background(Color.White, RoundedCornerShape(8.dp)).border(BorderStroke(1.dp, BorderBlack), RoundedCornerShape(8.dp)).padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AsyncImage(model = comment.user?.avatar ?: "", contentDescription = "", modifier = Modifier.size(28.dp).clip(CircleShape).border(BorderStroke(1.dp, BorderBlack), CircleShape), contentScale = ContentScale.Crop)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(comment.user?.username ?: "Unknown", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                                Spacer(modifier = Modifier.weight(1f))
                                Text(comment.createdAt.take(10), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(comment.content, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            // Input
            Row(
                modifier = Modifier.fillMaxWidth().background(Surface).border(BorderStroke(2.dp, BorderBlack), RoundedCornerShape(0.dp)).padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NeoTextField(value = commentText, onValueChange = { commentText = it }, placeholder = "Write a comment...", modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { if (commentText.isNotBlank()) { viewModel.sendComment(episodeId, commentText); commentText = "" } },
                    modifier = Modifier.background(Primary, RoundedCornerShape(8.dp)).border(BorderStroke(2.dp, BorderBlack), RoundedCornerShape(8.dp))
                ) { Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White) }
            }
        }
    }
}
