package com.anix.app.ui.screens.chat

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
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
import com.anix.app.ui.components.ErrorState
import com.anix.app.ui.components.LoadingIndicator
import com.anix.app.ui.components.NeoTextField

@Composable
fun ChatDetailScreen(
    conversationId: String,
    onBack: () -> Unit,
    viewModel: ChatDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var newMessage by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(conversationId) {
        viewModel.loadMessages(conversationId)
        viewModel.markRead(conversationId)
    }

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
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
                Text("Chat", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            if (uiState.isLoading) {
                LoadingIndicator()
            } else if (uiState.error != null) {
                ErrorState(message = uiState.error!!, onRetry = { viewModel.loadMessages(conversationId) })
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                    state = listState,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(uiState.messages) { message ->
                        MessageBubble(message = message, currentUserId = viewModel.currentUserId)
                    }
                }
            }

            // Input
            Row(
                modifier = Modifier.fillMaxWidth().background(Surface).border(BorderStroke(2.dp, BorderBlack), RoundedCornerShape(0.dp)).padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NeoTextField(value = newMessage, onValueChange = { newMessage = it }, placeholder = "Type a message...", modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { if (newMessage.isNotBlank()) { viewModel.sendMessage(conversationId, newMessage); newMessage = "" } },
                    modifier = Modifier.background(Primary, RoundedCornerShape(8.dp)).border(BorderStroke(2.dp, BorderBlack), RoundedCornerShape(8.dp))
                ) { Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White) }
            }
        }
    }
}

@Composable
private fun MessageBubble(message: com.anix.app.data.models.Message, currentUserId: String?) {
    val isMine = currentUserId != null && message.senderId == currentUserId
    val bubbleColor = if (isMine) Primary else Surface
    val textColor = if (isMine) Color.White else Color.Black
    val alignment = if (isMine) Alignment.CenterEnd else Alignment.CenterStart
    val avatarPlaceholder = rememberVectorPainter(Icons.Filled.Person)
    val avatarUrl = ApiClient.resolveUrl(message.senderAvatar)

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 2.dp),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isMine) {
            AsyncImage(
                model = avatarUrl,
                contentDescription = null,
                modifier = Modifier.size(28.dp).clip(CircleShape).border(BorderStroke(1.5.dp, BorderBlack), CircleShape),
                placeholder = avatarPlaceholder,
                error = avatarPlaceholder
            )
            Spacer(Modifier.width(6.dp))
        }
        Column(
            modifier = Modifier.widthIn(max = 280.dp).background(bubbleColor, RoundedCornerShape(8.dp)).border(BorderStroke(2.dp, BorderBlack), RoundedCornerShape(8.dp)).padding(12.dp)
        ) {
            Text(text = message.content, style = MaterialTheme.typography.bodyMedium, color = textColor)
            Text(text = message.createdAt.takeLast(8).take(5), style = MaterialTheme.typography.bodySmall, color = textColor.copy(alpha = 0.7f), modifier = Modifier.align(Alignment.End))
        }
    }
}
