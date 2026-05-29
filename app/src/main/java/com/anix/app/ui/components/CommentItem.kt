package com.anix.app.ui.components
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
import androidx.compose.foundation.layout.matchParentSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.anix.app.core.network.ApiClient
import com.anix.app.core.theme.AccentOrange
import com.anix.app.core.theme.BorderBlack
import com.anix.app.core.theme.Primary
import com.anix.app.data.models.Comment

@Composable
fun CommentItem(
    comment: Comment,
    modifier: Modifier = Modifier,
    onReply: (() -> Unit)? = null,
    onReport: ((Comment) -> Unit)? = null,
    onViewReplies: (() -> Unit)? = null
) {
    val bannerUrl = ApiClient.resolveUrl(comment.userBanner)?.ifEmpty { null }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .border(BorderStroke(2.dp, BorderBlack), RoundedCornerShape(8.dp))
    ) {
        Box {
            if (bannerUrl != null) {
                AsyncImage(
                    model = bannerUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
                Box(Modifier.matchParentSize().background(Color.Black.copy(alpha = 0.3f)))
            }
            Column(Modifier.padding(12.dp).then(if (bannerUrl != null) Modifier.fillMaxWidth() else Modifier)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = ApiClient.resolveUrl(comment.userAvatar),
                        contentDescription = comment.username,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .border(BorderStroke(1.5.dp, BorderBlack), CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = comment.username,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (bannerUrl != null) Color.White else Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    NeoBadge(
                        text = "Lv.${comment.userLevel}",
                        backgroundColor = AccentOrange,
                        textColor = Color.White
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = comment.createdAt.take(10),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (bannerUrl != null) Color.White.copy(alpha = 0.8f) else Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = comment.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (bannerUrl != null) Color.White else Color.Unspecified
                )
                Spacer(modifier = Modifier.height(8.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (onReply != null) {
                Text(
                    text = "Reply",
                    modifier = Modifier.clickable { onReply() },
                    style = MaterialTheme.typography.labelMedium,
                    color = Primary,
                    fontWeight = FontWeight.Bold
                )
            }
            if (onReport != null) {
                Text(
                    text = "Report",
                    modifier = Modifier.clickable { onReport(comment) },
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        if (comment.replies.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            comment.replies.take(3).forEach { reply ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 4.dp)
                        .border(BorderStroke(1.dp, BorderBlack.copy(alpha = 0.3f)), RoundedCornerShape(6.dp))
                        .padding(8.dp)
                ) {
                    AsyncImage(
                        model = ApiClient.resolveUrl(reply.userAvatar),
                        contentDescription = reply.username,
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = reply.username,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = reply.content,
                            style = MaterialTheme.typography.bodySmall
                        )
            }
        }
    }
}

            if (comment.replyCount > 3 && onViewReplies != null) {
                Text(
                    text = "View ${comment.replyCount} more replies",
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
