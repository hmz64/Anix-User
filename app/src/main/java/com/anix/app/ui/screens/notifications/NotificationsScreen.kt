package com.anix.app.ui.screens.notifications
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.anix.app.data.models.Notification
import com.anix.app.ui.components.EmptyState
import com.anix.app.ui.components.ErrorState
import com.anix.app.ui.components.LoadingIndicator
import com.anix.app.ui.components.NeoButton
import kotlinx.coroutines.launch

@Composable
fun NotificationsScreen(
    onBack: () -> Unit
) {
    var notifications by remember { mutableStateOf<List<Notification>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        ServiceLocator.getNotificationRepository().getNotifications()
            .onSuccess { notifications = it }
            .onFailure { error = it.message }
        isLoading = false
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "← Notifications",
                    modifier = Modifier.clickable { onBack() },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                NeoButton(
                    text = "Mark All Read",
                    onClick = {
                        scope.launch {
                            ServiceLocator.getNotificationRepository().markAllRead()
                            notifications = notifications.map { it.copy(read = true) }
                        }
                    },
                    backgroundColor = Surface,
                    textColor = Color.Black
                )
            }

            if (isLoading) {
                LoadingIndicator()
            } else if (error != null) {
                ErrorState(message = error!!, onRetry = { isLoading = true; error = null })
            } else if (notifications.isEmpty()) {
                EmptyState(message = "No notifications yet")
            } else {
                LazyColumn {
                    items(notifications) { notification ->
                        NotificationItem(
                            notification = notification,
                            onMarkRead = {
                                scope.launch {
                                    ServiceLocator.getNotificationRepository().markRead(notification.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationItem(
    notification: Notification,
    modifier: Modifier = Modifier,
    onMarkRead: () -> Unit = {}
) {
    val bgColor = if (notification.read) Surface else Primary.copy(alpha = 0.1f)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .background(bgColor, RoundedCornerShape(8.dp))
            .border(BorderStroke(2.dp, BorderBlack), RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = if (notification.read) FontWeight.Normal else FontWeight.Bold
                )
                Text(
                    text = notification.createdAt.take(10),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = notification.body,
                style = MaterialTheme.typography.bodyMedium,
                color = if (notification.read) Color.Gray else Color.Black
            )
        }
        if (!notification.read) {
            IconButton(onClick = onMarkRead) {
                Icon(Icons.Default.Delete, contentDescription = "Mark read", tint = Color.Gray)
            }
        }
    }
}
