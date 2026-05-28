package com.anix.app.ui.screens.comments
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.anix.app.core.di.ServiceLocator
import com.anix.app.core.theme.Background
import com.anix.app.core.theme.BorderBlack
import com.anix.app.core.theme.Primary
import com.anix.app.core.theme.Surface
import com.anix.app.data.models.Comment
import com.anix.app.ui.components.CommentItem
import com.anix.app.ui.components.EmptyState
import com.anix.app.ui.components.ErrorState
import com.anix.app.ui.components.LoadingIndicator
import com.anix.app.ui.components.NeoButton
import com.anix.app.ui.components.NeoTextField
import kotlinx.coroutines.launch

@Composable
fun CommentsScreen(
    animeId: String,
    onBack: () -> Unit
) {
    var comments by remember { mutableStateOf<List<Comment>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var newComment by remember { mutableStateOf("") }
    var replyTo by remember { mutableStateOf<Comment?>(null) }
    var showReportDialog by remember { mutableStateOf<Comment?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(animeId) {
        ServiceLocator.getUserRepository().getComments(animeId)
            .onSuccess { comments = it }
            .onFailure { error = it.message }
        isLoading = false
    }

    fun submitComment() {
        if (newComment.isBlank()) return
        scope.launch {
            ServiceLocator.getUserRepository().createComment(
                animeId = animeId,
                content = newComment,
                parentId = replyTo?.id
            ).onSuccess {
                comments = listOf(it) + comments
                newComment = ""
                replyTo = null
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Surface)
                    .border(BorderStroke(2.dp, BorderBlack), RoundedCornerShape(0.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "← Back",
                    modifier = Modifier.clickable { onBack() },
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Comments (${comments.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (isLoading) {
                LoadingIndicator()
            } else if (error != null) {
                ErrorState(message = error!!, onRetry = { isLoading = true; error = null })
            } else if (comments.isEmpty()) {
                EmptyState(message = "No comments yet. Be the first!")
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    items(comments) { comment ->
                        CommentItem(
                            comment = comment,
                            onReply = { replyTo = comment },
                            onReport = { showReportDialog = it }
                        )
                    }
                }
            }

            // Reply indicator
            if (replyTo != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Yellow.copy(alpha = 0.2f))
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Replying to ${replyTo!!.username}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Text(
                        text = "Cancel",
                        modifier = Modifier.clickable { replyTo = null },
                        style = MaterialTheme.typography.bodySmall,
                        color = Primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Input
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Surface)
                    .border(BorderStroke(2.dp, BorderBlack), RoundedCornerShape(0.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NeoTextField(
                    value = newComment,
                    onValueChange = { newComment = it },
                    placeholder = "Add a comment...",
                    modifier = Modifier.weight(1f),
                    singleLine = false
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { submitComment() },
                    modifier = Modifier
                        .background(Primary, RoundedCornerShape(8.dp))
                        .border(BorderStroke(2.dp, BorderBlack), RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                }
            }
        }
    }

    // Report Dialog
    if (showReportDialog != null) {
        var reportReason by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showReportDialog = null },
            title = { Text("Report Comment", fontWeight = FontWeight.Bold) },
            text = {
                NeoTextField(
                    value = reportReason,
                    onValueChange = { reportReason = it },
                    placeholder = "Reason for report..."
                )
            },
            confirmButton = {
                NeoButton(
                    text = "Report",
                    onClick = {
                        scope.launch {
                            ServiceLocator.getUserRepository().reportComment(showReportDialog!!.id, reportReason)
                            showReportDialog = null
                        }
                    },
                    backgroundColor = Color.Red
                )
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
