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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.anix.app.core.theme.Background
import com.anix.app.core.theme.BorderBlack
import com.anix.app.core.theme.Primary
import com.anix.app.core.theme.Surface
import com.anix.app.data.models.Conversation
import com.anix.app.data.models.FriendRequest
import com.anix.app.data.models.SearchUsersResponse
import com.anix.app.ui.components.EmptyState
import com.anix.app.ui.components.ErrorState
import com.anix.app.ui.components.LoadingIndicator
import com.anix.app.ui.components.NeoButton
import com.anix.app.ui.components.NeoTextField

@Composable
fun ChatListScreen(
    onChatClick: (String) -> Unit,
    viewModel: ChatListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showSearch by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth().background(Surface).border(BorderStroke(2.dp, BorderBlack)).padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Chats", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                NeoButton(text = "Search", onClick = { showSearch = !showSearch }, backgroundColor = Surface, textColor = Color.Black)
            }

            // Search bar
            if (showSearch) {
                NeoTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it; viewModel.setSearchQuery(it) },
                    placeholder = "Search users...",
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = ""; viewModel.setSearchQuery("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { viewModel.setSearchQuery(searchQuery) }),
                    singleLine = true
                )
                // Search results
                if (searchQuery.isNotEmpty()) {
                    if (uiState.isSearching) {
                        LoadingIndicator()
                    } else if (uiState.searchResults.isNotEmpty()) {
                        Text(
                            "Search Results",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                        uiState.searchResults.forEach { user ->
                            SearchUserItem(
                                user = user,
                                onAddFriend = { viewModel.sendFriendRequest(user.id) },
                                onChat = {
                                    viewModel.startChatWith(user.id) { convId -> onChatClick(convId) }
                                }
                            )
                        }
                    } else {
                        EmptyState(message = "No users found")
                    }
                }
            }

            // Tab bar
            TabRow(
                selectedTabIndex = uiState.selectedTab,
                containerColor = Surface,
                contentColor = Primary
            ) {
                Tab(
                    selected = uiState.selectedTab == 0,
                    onClick = { viewModel.setSelectedTab(0) },
                    text = { Text("Chats") }
                )
                Tab(
                    selected = uiState.selectedTab == 1,
                    onClick = { viewModel.setSelectedTab(1) },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Requests")
                            if (uiState.friendRequests.isNotEmpty()) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "${uiState.friendRequests.size}",
                                    modifier = Modifier
                                        .background(Color.Red, CircleShape)
                                        .padding(horizontal = 6.dp, vertical = 2.dp),
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                )
            }

            when (uiState.selectedTab) {
                0 -> {
                    when {
                        uiState.isLoading -> LoadingIndicator()
                        uiState.error != null -> ErrorState(message = uiState.error!!, onRetry = { viewModel.loadConversations() })
                        uiState.conversations.isEmpty() -> EmptyState(message = "No conversations yet")
                        else -> {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(uiState.conversations, key = { it.id }) { conv ->
                                    ConversationItem(conv = conv, onClick = { onChatClick(conv.id) })
                                }
                            }
                        }
                    }
                }
                1 -> {
                    if (uiState.friendRequests.isEmpty()) {
                        EmptyState(message = "No pending friend requests")
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(uiState.friendRequests, key = { it.id }) { request ->
                                FriendRequestItem(
                                    request = request,
                                    onAccept = { viewModel.respondToRequest(request.id, true) },
                                    onReject = { viewModel.respondToRequest(request.id, false) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ConversationItem(conv: Conversation, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val other = conv.participants.firstOrNull()
        AsyncImage(
            model = other?.avatar ?: "",
            contentDescription = "",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .border(BorderStroke(2.dp, BorderBlack), CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(other?.username ?: "Unknown", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            if (conv.lastMessage != null) {
                Text(
                    conv.lastMessage!!.content,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1
                )
            }
        }
        if (conv.unreadCount > 0) {
            Text(
                "${conv.unreadCount}",
                modifier = Modifier
                    .background(Color.Red, CircleShape)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                color = Color.White,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun FriendRequestItem(request: FriendRequest, onAccept: () -> Unit, onReject: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = request.senderAvatar,
            contentDescription = "",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .border(BorderStroke(2.dp, BorderBlack), CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(request.senderName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            Text("Wants to be friends", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            IconButton(onClick = onAccept) {
                Icon(Icons.Default.Check, contentDescription = "Accept", tint = Color.Green)
            }
            IconButton(onClick = onReject) {
                Icon(Icons.Default.Close, contentDescription = "Reject", tint = Color.Red)
            }
        }
    }
}

@Composable
private fun SearchUserItem(user: SearchUsersResponse, onAddFriend: () -> Unit, onChat: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = user.avatar,
            contentDescription = "",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(BorderStroke(2.dp, BorderBlack), CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(user.username, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
            Text("Lv.${user.level}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        NeoButton(text = "Add", onClick = onAddFriend, backgroundColor = Primary)
        Spacer(modifier = Modifier.width(4.dp))
        NeoButton(text = "Chat", onClick = onChat, backgroundColor = Surface, textColor = Color.Black)
    }
}