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
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.anix.app.core.theme.Background
import com.anix.app.core.network.ApiClient
import com.anix.app.core.theme.AccentBlue
import com.anix.app.core.theme.BorderBlack
import com.anix.app.core.theme.GlassBorder
import com.anix.app.core.theme.Primary
import com.anix.app.core.theme.TextMuted
import com.anix.app.core.theme.TextPrimary
import com.anix.app.core.theme.TextSecondary
import com.anix.app.core.theme.Surface
import com.anix.app.core.util.formatTimestamp
import com.anix.app.ui.components.ErrorState
import com.anix.app.ui.components.LoadingIndicator
import com.anix.app.ui.components.NeoButton
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
    var reportReason by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(episodeId) {
        viewModel.loadComments(episodeId)
    }

    val shouldLoadMore by remember {
        derivedStateOf {
            val last = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            last >= uiState.comments.size - 3 && uiState.hasMore && !uiState.isLoadingMore
        }
    }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) viewModel.loadMore(episodeId)
    }

    // Report dialog
    if (uiState.reportingCommentId != null) {
        Dialog(onDismissRequest = { viewModel.dismissReportDialog() }) {
            Column(
                modifier = Modifier.background(Color.White, RoundedCornerShape(12.dp))
                    .border(BorderStroke(2.dp, BorderBlack), RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Text("Report Comment", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                NeoTextField(value = reportReason, onValueChange = { reportReason = it }, placeholder = "Reason for report...", modifier = Modifier.fillMaxWidth(), singleLine = false)
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    NeoButton(text = "Cancel", onClick = { viewModel.dismissReportDialog() }, backgroundColor = Surface, textColor = Color.Black)
                    NeoButton(text = "Report", onClick = {
                        if (reportReason.isNotBlank()) {
                            viewModel.reportComment(episodeId, uiState.reportingCommentId!!, reportReason)
                            reportReason = ""
                        }
                    }, backgroundColor = Color.Red)
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().background(Surface).border(BorderStroke(2.dp, BorderBlack)).padding(12.dp),
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
                LazyColumn(
                    state = listState,
                    modifier = Modifier.weight(1f).padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.comments, key = { it.id }) { comment ->
                        CommentItem(
                            comment = comment,
                            onReply = { viewModel.setReplyTo(comment) },
                            onReport = { viewModel.showReportDialog(comment.id) }
                        )
                    }
                    if (uiState.isLoadingMore) {
                        item { Box(modifier = Modifier.fillMaxWidth().padding(8.dp), contentAlignment = Alignment.Center) { LoadingIndicator() } }
                    }
                }
            }

            // Reply indicator
            if (uiState.replyingTo != null) {
                Row(
                    modifier = Modifier.fillMaxWidth().background(Primary.copy(alpha = 0.1f)).padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Replying to ${uiState.replyingTo!!.username}", style = MaterialTheme.typography.bodySmall, color = Primary, modifier = Modifier.weight(1f))
                    IconButton(onClick = { viewModel.setReplyTo(null) }) {
                        Text("X", color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Input
            Row(
                modifier = Modifier.fillMaxWidth().background(Surface).border(BorderStroke(2.dp, BorderBlack)).padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NeoTextField(value = commentText, onValueChange = { commentText = it }, placeholder = if (uiState.replyingTo != null) "Reply to ${uiState.replyingTo!!.username}..." else "Write a comment...", modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = { if (commentText.isNotBlank()) { viewModel.sendComment(episodeId, commentText); commentText = "" } },
                    modifier = Modifier.background(Primary, RoundedCornerShape(8.dp)).border(BorderStroke(2.dp, BorderBlack), RoundedCornerShape(8.dp))
                ) { Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White) }
            }
        }
    }
}

@Composable
private fun CommentItem(
    comment: com.anix.app.data.models.Comment,
    onReply: () -> Unit,
    onReport: () -> Unit
) {
    val bannerUrl = ApiClient.resolveUrl(comment.userBanner)?.ifEmpty { null }
    val avatarUrl = ApiClient.resolveUrl(comment.userAvatar)?.ifEmpty { null }
    val hasBanner = !bannerUrl.isNullOrEmpty()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
    ) {
        if (hasBanner) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(bannerUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxWidth()
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.verticalGradient(
                            colorStops = arrayOf(
                                0.0f to Color(0xFF050A18).copy(alpha = 0.50f),
                                1.0f to Color(0xFF050A18).copy(alpha = 0.80f)
                            )
                        )
                    )
            )
        } else {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.White.copy(alpha = 0.08f))
            )
        }

        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(avatarUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, GlassBorder, CircleShape)
                        .background(Color.White.copy(alpha = 0.08f), CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(comment.username, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = TextPrimary)
                Spacer(modifier = Modifier.weight(1f))
                Text(formatTimestamp(comment.createdAt), color = TextMuted, fontSize = 11.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(comment.content, color = TextSecondary, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Row {
                Text("Reply", style = MaterialTheme.typography.labelSmall, color = AccentBlue, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onReply() })
                Spacer(modifier = Modifier.width(16.dp))
                Text("Report", style = MaterialTheme.typography.labelSmall, color = Color(0xFFFF453A), fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onReport() })
            }
        }
    }
}
