package com.anix.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Brush
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.anix.app.core.network.ApiClient
import com.anix.app.core.theme.AccentBlue
import com.anix.app.core.theme.AccentOrange
import com.anix.app.core.theme.GlassBorder
import com.anix.app.core.theme.TextMuted
import com.anix.app.core.theme.TextPrimary
import com.anix.app.core.theme.TextSecondary
import com.anix.app.core.util.formatTimestamp

@Composable
fun CommentItem(
    comment: com.anix.app.data.models.Comment,
    modifier: Modifier = Modifier,
    onReply: (() -> Unit)? = null,
    onReport: ((com.anix.app.data.models.Comment) -> Unit)? = null,
    onViewReplies: (() -> Unit)? = null
) {
    val bannerUrl = ApiClient.resolveUrl(comment.userBanner)?.ifEmpty { null }
    val avatarUrl = ApiClient.resolveUrl(comment.userAvatar)?.ifEmpty { null }
    val hasBanner = !bannerUrl.isNullOrEmpty()

    Box(
        modifier = modifier
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
                    .heightIn(min = 120.dp, max = 160.dp)
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

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(avatarUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = comment.username,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, GlassBorder, CircleShape)
                        .background(Color.White.copy(alpha = 0.08f), CircleShape),
                    placeholder = null,
                    error = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = comment.username,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.width(6.dp))
                NeoBadge(
                    text = "Lv.${comment.userLevel}",
                    backgroundColor = AccentOrange,
                    textColor = Color.White
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = formatTimestamp(comment.createdAt),
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = comment.content,
                color = TextSecondary,
                fontSize = 14.sp
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
                        color = AccentBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (onReport != null) {
                    Text(
                        text = "Report",
                        modifier = Modifier.clickable { onReport(comment) },
                        style = MaterialTheme.typography.labelMedium,
                        color = Color(0xFFFF453A),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            if (comment.replies.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                comment.replies.take(3).forEach { reply ->
                    Row(
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    ) {
                        AsyncImage(
                            model = reply.userAvatar?.let { ApiClient.resolveUrl(it) },
                            contentDescription = reply.username,
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .border(1.dp, GlassBorder.copy(alpha = 0.4f), CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = reply.username,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = reply.content,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
                if (comment.replyCount > 3 && onViewReplies != null) {
                    Text(
                        text = "View ${comment.replyCount} more replies",
                        modifier = Modifier
                            .padding(start = 16.dp, top = 4.dp)
                            .clickable { onViewReplies() },
                        style = MaterialTheme.typography.labelSmall,
                        color = AccentBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
