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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.anix.app.data.models.SocialPost
import com.anix.app.ui.components.ErrorState
import com.anix.app.ui.components.LoadingIndicator
import com.anix.app.ui.components.NeoButton
import com.anix.app.ui.components.NeoTextField

@Composable
fun SocialFeedScreen(
    onPostClick: (String) -> Unit,
    viewModel: SocialFeedViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showCreateDialog by remember { mutableStateOf(false) }
    var postContent by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Pagination trigger
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItem >= uiState.posts.size - 3 && uiState.hasMore && !uiState.isLoadingMore
        }
    }
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) viewModel.loadMore()
    }

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().background(Surface).border(BorderStroke(2.dp, BorderBlack)).padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Social Feed", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                NeoButton(text = "Create Post", onClick = { showCreateDialog = true }, backgroundColor = Primary)
            }

            when {
                uiState.isLoading -> LoadingIndicator()
                uiState.error != null -> ErrorState(message = uiState.error!!, onRetry = { viewModel.loadFeed() })
                else -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        items(uiState.posts, key = { it.id }) { post ->
                            PostCard(
                                post = post,
                                onClick = { onPostClick(post.id) },
                                onLikeClick = { viewModel.toggleLike(post.id) }
                            )
                        }
                        if (uiState.isLoadingMore) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                    Text("Loading more...", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Create Post Dialog
        if (showCreateDialog) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)).clickable { showCreateDialog = false }) {
                Column(
                    modifier = Modifier.align(Alignment.Center).fillMaxWidth(0.9f).background(Color.White, RoundedCornerShape(12.dp)).border(BorderStroke(2.dp, BorderBlack), RoundedCornerShape(12.dp)).padding(16.dp)
                ) {
                    Text("Create Post", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    NeoTextField(value = postContent, onValueChange = { postContent = it }, placeholder = "What's on your mind?", modifier = Modifier.fillMaxWidth(), singleLine = false)
                    Spacer(modifier = Modifier.height(12.dp))
                    NeoButton(text = "Post", onClick = { viewModel.createPost(postContent, null); showCreateDialog = false; postContent = "" }, backgroundColor = Primary, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
private fun PostCard(post: SocialPost, onClick: () -> Unit, onLikeClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp).background(Color.White, RoundedCornerShape(8.dp)).border(BorderStroke(2.dp, BorderBlack), RoundedCornerShape(8.dp)).clickable { onClick() }.padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(model = ApiClient.resolveUrl(post.userAvatar), contentDescription = "", modifier = Modifier.size(36.dp).clip(CircleShape).border(BorderStroke(1.dp, BorderBlack), CircleShape), contentScale = ContentScale.Crop)
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(post.username, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                Text(post.createdAt.take(10), style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(post.content, style = MaterialTheme.typography.bodyMedium)
        if (!post.image.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            AsyncImage(model = post.image, contentDescription = "", modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(8.dp)).border(BorderStroke(1.dp, BorderBlack), RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Text(
                text = if (post.isLiked) "❤️ ${post.likeCount}" else "🤍 ${post.likeCount}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onLikeClick() }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text("💬 ${post.commentCount}", style = MaterialTheme.typography.bodySmall)
        }
    }
}