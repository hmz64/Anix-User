package com.anix.app.ui.screens.social

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
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import com.anix.app.core.network.ApiClient
import com.anix.app.core.theme.Background
import com.anix.app.core.theme.BorderBlack
import com.anix.app.core.theme.Primary
import com.anix.app.core.theme.Surface
import com.anix.app.data.models.Comment
import com.anix.app.data.models.SocialPost
import com.anix.app.ui.components.CommentItem
import com.anix.app.ui.components.EmptyState
import com.anix.app.ui.components.ErrorState
import com.anix.app.ui.components.LoadingIndicator
import com.anix.app.ui.components.NeoTextField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.anix.app.core.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SocialPostDetailUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val post: SocialPost? = null,
    val comments: List<Comment> = emptyList(),
    val snackbarMessage: String? = null
)

class SocialPostDetailViewModel : ViewModel() {
    private val repo = ServiceLocator.getSocialRepository()

    private val _uiState = MutableStateFlow(SocialPostDetailUiState())
    val uiState: StateFlow<SocialPostDetailUiState> = _uiState.asStateFlow()

    fun loadPost(postId: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            repo.getSocialFeed().onSuccess { posts ->
                val post = posts.find { it.id == postId }
                if (post != null) {
                    _uiState.value = _uiState.value.copy(post = post, isLoading = false)
                } else {
                    _uiState.value = _uiState.value.copy(error = "Post not found", isLoading = false)
                }
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.message, isLoading = false)
            }
        }
    }

    fun sendComment(postId: String, content: String) {
        _uiState.value = _uiState.value.copy(snackbarMessage = "Comment feature coming soon")
    }

    fun clearSnackbar() {
        _uiState.value = _uiState.value.copy(snackbarMessage = null)
    }
}

@Composable
fun SocialPostDetailScreen(
    postId: String,
    onBack: () -> Unit,
    viewModel: SocialPostDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var commentText by remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(postId) {
        viewModel.loadPost(postId)
    }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().background(Surface).border(2.dp, BorderBlack, RoundedCornerShape(0.dp)).padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "\u2190 Back",
                    modifier = Modifier.clickable { onBack() },
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "Post", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            when {
                uiState.isLoading -> LoadingIndicator()
                uiState.error != null -> ErrorState(message = uiState.error!!, onRetry = { viewModel.loadPost(postId) })
                uiState.post == null -> EmptyState(message = "Post not found")
                else -> {
                    val post = uiState.post!!
                    LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 8.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            PostDetailCard(post = post)
                        }
                        if (uiState.comments.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                    Text(text = "No comments yet", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                                }
                            }
                        } else {
                            items(uiState.comments) { comment ->
                                CommentItem(comment = comment)
                            }
                        }
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().background(Surface).border(2.dp, BorderBlack, RoundedCornerShape(0.dp)).padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NeoTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    placeholder = "Write a comment...",
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (commentText.isNotBlank()) {
                            viewModel.sendComment(postId, commentText)
                            commentText = ""
                        }
                    },
                    modifier = Modifier.background(Primary, RoundedCornerShape(8.dp)).border(2.dp, BorderBlack, RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White)
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun PostDetailCard(post: SocialPost) {
    Column(
        modifier = Modifier.fillMaxWidth().background(Surface, RoundedCornerShape(8.dp)).border(2.dp, BorderBlack, RoundedCornerShape(8.dp)).padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = ApiClient.resolveUrl(post.userAvatar),
                contentDescription = "",
                modifier = Modifier.size(40.dp).clip(CircleShape).border(BorderStroke(1.5.dp, BorderBlack), CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = post.username, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                Text(text = post.createdAt.take(10), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = post.content, style = MaterialTheme.typography.bodyLarge)
        if (!post.image.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            AsyncImage(
                model = post.image,
                contentDescription = "",
                modifier = Modifier.fillMaxWidth().height(220.dp).clip(RoundedCornerShape(8.dp)).border(1.dp, BorderBlack, RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row {
            Text(
                text = if (post.isLiked) "\u2764\uFE0F ${post.likeCount}" else "\uD83E\uDD0D ${post.likeCount}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "\uD83D\uDCAC ${post.commentCount}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
