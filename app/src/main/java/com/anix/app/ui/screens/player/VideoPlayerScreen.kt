package com.anix.app.ui.screens.player

import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.anix.app.core.theme.BorderBlack
import com.anix.app.core.util.downloadVideoMp4
import com.anix.app.ui.components.ReportDialog
import com.anix.app.data.models.AnimeSeries
import com.anix.app.data.models.Comment
import com.anix.app.data.models.Episode
import com.anix.app.ui.components.LoadingIndicator
import com.anix.app.ui.components.NeoBadge
import com.anix.app.ui.components.NeoChip
import com.anix.app.ui.components.NeoTextField
import kotlinx.coroutines.delay
import kotlin.math.min

private val Bg = Color(0xFFF5EEE8)
private val Blue = Color(0xFF2B2BFF)
private val Dark = Color(0xFF0D0D0D)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayerScreen(
    episodeId: String,
    animeId: String? = null,
    onBack: () -> Unit,
    onCommentsClick: ((String) -> Unit)? = null,
    viewModel: VideoPlayerViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showControls by remember { mutableStateOf(true) }
    var showReportDialog by remember { mutableStateOf(false) }

    LaunchedEffect(episodeId) {
        viewModel.loadEpisode(episodeId, animeId)
    }

    LaunchedEffect(showControls) {
        if (showControls) {
            delay(3000)
            showControls = false
        }
    }

    val window = (context as? Activity)?.window

    if (state.isFullscreen) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            FullscreenPlayer(
                videoUrl = state.videoUrl,
                speed = state.playbackSpeed,
                showControls = showControls,
                onToggleControls = { showControls = !showControls },
                onBack = { window?.let { resetSystemBars(it) }; viewModel.setFullscreen(false); onBack() },
                onExitFullscreen = {
                    (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    window?.let { resetSystemBars(it) }
                    viewModel.setFullscreen(false)
                }
            )
        }
        return
    }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            playWhenReady = true
            addListener(object : Player.Listener {
                override fun onIsPlayingChanged(playing: Boolean) {
                    viewModel.setPlaying(playing)
                }
            })
        }
    }

    LaunchedEffect(state.videoUrl) {
        if (state.videoUrl.isNotEmpty()) {
            exoPlayer.setMediaItem(MediaItem.fromUri(state.videoUrl))
            exoPlayer.prepare()
        }
    }

    LaunchedEffect(state.playbackSpeed) {
        exoPlayer.playbackParameters = PlaybackParameters(state.playbackSpeed)
    }

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    val position by remember { derivedStateOf { exoPlayer.currentPosition } }
    val duration by remember {
        derivedStateOf {
            val d = exoPlayer.duration
            if (d == Long.MIN_VALUE || d < 0) 0L else d
        }
    }
    val progress by remember {
        derivedStateOf {
            if (duration > 0) (position.toFloat() / duration.toFloat()).coerceIn(0f, 1f) else 0f
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
    ) {
        item(key = "player") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black)
            ) {
                when {
                    state.isLoading -> Box(
                        Modifier.fillMaxWidth().aspectRatio(16f / 9f),
                        contentAlignment = Alignment.Center
                    ) { LoadingIndicator() }

                    state.error != null -> Box(
                        Modifier.fillMaxWidth().aspectRatio(16f / 9f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(state.error ?: "Error", color = Color.White)
                    }

                    state.videoUrl.isNotEmpty() -> {
                        PlayerSurface(
                            exoPlayer = exoPlayer,
                            showControls = showControls,
                            speed = state.playbackSpeed,
                            quality = state.currentQuality,
                            onToggleControls = { showControls = !showControls },
                            onBack = {
                                exoPlayer.stop(); onBack()
                            },
                            onFullscreen = {
                                (context as? Activity)?.let { act ->
                                    act.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                                    window?.let { w ->
                                        WindowCompat.setDecorFitsSystemWindows(w, false)
                                        WindowInsetsControllerCompat(w, w.decorView).apply {
                                            hide(WindowInsetsCompat.Type.systemBars())
                                            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                                        }
                                    }
                                }
                                viewModel.setFullscreen(true)
                            },
                            onChangeSpeed = { /* bottom sheet handles it */ },
                            onChangeQuality = { /* bottom sheet handles it */ }
                        )
                    }
                }
            }
        }

        if (state.videoUrl.isNotEmpty()) {
            item(key = "seekbar") {
                ProgressBar(progress = progress, position = position, duration = duration) { frac ->
                    exoPlayer.seekTo((frac * duration).toLong())
                }
            }
        }

        val anime = state.anime
        val ep = state.episode
        if (anime != null && ep != null) {
            item(key = "info") {
                AnimeInfo(anime = anime, episode = ep, quality = state.currentQuality)
            }
            item(key = "desc") {
                Description(text = anime.description)
            }
            item(key = "chips") {
                ActionChips(
                    quality = state.currentQuality,
                    onDownload = {
                        val fn = "${state.anime?.title ?: "anime"}_Ep${state.episode?.number ?: 0}"
                        downloadVideoMp4(context, state.videoUrl, fn)
                    },
                    onReport = { showReportDialog = true }
                )
            }
            if (state.episodes.isNotEmpty()) {
                item(key = "episodes") {
                    EpisodeGrid(
                        episodes = state.episodes,
                        currentId = ep.id,
                        watched = state.watchedEpisodeIds,
                        onClick = { viewModel.selectEpisode(it) }
                    )
                }
            }
        }

        item(key = "tips") {
            TipsCard()
        }

        item(key = "comments") {
            Comments(
                comments = state.comments,
                loading = state.commentsLoading,
                text = state.commentText,
                submitting = state.submittingComment,
                sort = state.sortMode,
                episodeId = episodeId,
                onTextChange = { viewModel.setCommentText(it) },
                onSubmit = { viewModel.submitComment(episodeId) },
                onDelete = { viewModel.deleteComment(episodeId, it) },
                onSortChange = { viewModel.setSortMode(it); viewModel.loadComments(episodeId, it) }
            )
        }

        item { Spacer(Modifier.height(80.dp)) }
    }

    if (showReportDialog) {
        ReportDialog(
            onDismiss = { showReportDialog = false },
            onSubmit = { type, message -> viewModel.submitReport(type, message, episodeId) }
        )
    }
}

private fun resetSystemBars(window: android.view.Window) {
    WindowCompat.setDecorFitsSystemWindows(window, true)
    WindowInsetsControllerCompat(window, window.decorView).show(WindowInsetsCompat.Type.systemBars())
}

@Composable
private fun PlayerSurface(
    exoPlayer: ExoPlayer,
    showControls: Boolean,
    speed: Float,
    quality: String,
    onToggleControls: () -> Unit,
    onBack: () -> Unit,
    onFullscreen: () -> Unit,
    onChangeSpeed: () -> Unit,
    onChangeQuality: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
    ) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                    setShowNextButton(false)
                    setShowPreviousButton(false)
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        if (showControls) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable { onToggleControls() }
            ) {
                Row(
                    Modifier.align(Alignment.TopStart).padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Pill("← Back", onClick = onBack)
                }

                Row(
                    Modifier.align(Alignment.TopEnd).padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Pill("${speed}x", onClick = onChangeSpeed)
                    Pill(quality, onClick = onChangeQuality)
                    Box(
                        Modifier
                            .border(2.dp, Color.Black, RoundedCornerShape(50))
                            .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(50))
                            .clickable { onFullscreen() }
                            .padding(4.dp)
                    ) {
                        Icon(Icons.Filled.Fullscreen, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                    }
                }

                Row(
                    Modifier.align(Alignment.Center),
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CtlBtn(Icons.Filled.FastRewind, onClick = { exoPlayer.seekTo((exoPlayer.currentPosition - 10000).coerceAtLeast(0)) })
                    Box(
                        Modifier
                            .size(64.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                            .clickable { if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            if (exoPlayer.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    CtlBtn(Icons.Filled.FastForward, onClick = { exoPlayer.seekTo((exoPlayer.currentPosition + 10000).coerceAtMost(exoPlayer.duration.coerceAtLeast(0))) })
                }
            }
        }
    }
}

@Composable
private fun Pill(text: String, onClick: () -> Unit) {
    Box(
        Modifier
            .border(2.dp, Color.Black, RoundedCornerShape(50))
            .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(50))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(text, color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}

@Composable
private fun CtlBtn(icon: ImageVector, onClick: () -> Unit) {
    Box(
        Modifier
            .size(48.dp)
            .background(Color.White.copy(alpha = 0.2f), CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
    }
}

@Composable
private fun ProgressBar(progress: Float, position: Long, duration: Long, onSeek: (Float) -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(Color.Black)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Slider(
            value = progress,
            onValueChange = onSeek,
            modifier = Modifier.fillMaxWidth().height(4.dp),
            colors = SliderDefaults.colors(
                thumbColor = Blue,
                activeTrackColor = Blue,
                inactiveTrackColor = Color.Gray.copy(alpha = 0.5f)
            )
        )
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(formatTime(position), color = Color.White, fontSize = 11.sp)
            Text(formatTime(duration), color = Color.White, fontSize = 11.sp)
        }
    }
}

private fun formatTime(ms: Long): String {
    val total = ms / 1000
    return "%02d:%02d".format(total / 60, total % 60)
}

@Composable
private fun AnimeInfo(anime: AnimeSeries, episode: Episode, quality: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(Bg)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = anime.poster,
            contentDescription = null,
            modifier = Modifier
                .size(60.dp, 80.dp)
                .border(2.dp, BorderBlack, RoundedCornerShape(4.dp))
                .clip(RoundedCornerShape(4.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(anime.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, color = Dark)
            Spacer(Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                NeoBadge(text = "Episode ${episode.number}")
                NeoBadge(text = quality)
            }
        }
    }
}

@Composable
private fun Description(text: String) {
    var expanded by remember { mutableStateOf(false) }
    Column(
        Modifier
            .fillMaxWidth()
            .background(Bg)
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = text.ifEmpty { "No description." },
            maxLines = if (expanded) Int.MAX_VALUE else 3,
            overflow = TextOverflow.Ellipsis,
            fontSize = 13.sp,
            color = Color(0xFF444444)
        )
        if (!expanded && text.length > 150) {
            Text(
                "Baca semua >",
                color = Blue,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.clickable { expanded = true }.padding(top = 2.dp)
            )
        }
    }
}

@Composable
private fun ActionChips(
    quality: String,
    onDownload: () -> Unit = {},
    onReport: () -> Unit = {}
) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(Bg)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Chip(Icons.Outlined.Tune, "$quality Quality", onClick = {})
        Chip(Icons.Outlined.Download, "Download", onClick = onDownload)
        Chip(Icons.Outlined.Info, "Report", onClick = onReport)
    }
}

@Composable
private fun Chip(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        Modifier
            .border(2.dp, BorderBlack, RoundedCornerShape(8.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(icon, contentDescription = null, tint = Dark, modifier = Modifier.size(16.dp))
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Dark)
    }
}

@Composable
private fun EpisodeGrid(
    episodes: List<Episode>,
    currentId: String,
    watched: Set<String>,
    onClick: (Episode) -> Unit
) {
    val batchSize = 50
    val totalBatch = (episodes.size + batchSize - 1) / batchSize
    var batch by remember { mutableIntStateOf(0) }
    var expanded by remember { mutableStateOf(true) }

    Column(Modifier.background(Bg)) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Episode List", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Dark)
            IconButton(onClick = { expanded = !expanded }) {
                Icon(if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore, contentDescription = null, tint = Dark)
            }
        }

        if (expanded) {
            if (totalBatch > 1) {
                LazyRow(contentPadding = PaddingValues(horizontal = 12.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(totalBatch) { b ->
                        val s = b * batchSize + 1
                        val e = minOf((b + 1) * batchSize, episodes.size)
                        NeoChip(text = "$s-$e", selected = batch == b, onClick = { batch = b })
                    }
                }
                Spacer(Modifier.height(6.dp))
            }

            val shown = if (totalBatch > 1) episodes.drop(batch * batchSize).take(batchSize) else episodes
            LazyRow(contentPadding = PaddingValues(horizontal = 12.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(shown) { ep ->
                    val isCurrent = ep.id == currentId
                    val isWatched = ep.id in watched
                    Box(
                        Modifier
                            .size(48.dp)
                            .border(
                                width = if (isCurrent) 3.dp else 1.5.dp,
                                color = when { isCurrent -> Blue; isWatched -> Color.Gray; else -> BorderBlack },
                                shape = RoundedCornerShape(8.dp)
                            )
                            .background(
                                color = when { isCurrent -> Blue.copy(alpha = 0.1f); isWatched -> Color.Gray.copy(alpha = 0.1f); else -> Color.White },
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { onClick(ep) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${ep.number}", fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal, color = if (isCurrent) Blue else Dark, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun TipsCard() {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .border(1.5.dp, BorderBlack, RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEEF0FF)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Outlined.Info, tint = Blue, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Column {
                Text("Tips Mini Player", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Dark)
                Text("Geser video ke bawah untuk minimize dan tetap nonton sambil browsing.", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
private fun Comments(
    comments: List<Comment>,
    loading: Boolean,
    text: String,
    submitting: Boolean,
    sort: String,
    episodeId: String,
    onTextChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onDelete: (String) -> Unit,
    onSortChange: (String) -> Unit
) {
    Column(Modifier.background(Bg)) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("${comments.size} Comments", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Dark)
            Spacer(Modifier.weight(1f))
            Row(Modifier.border(2.dp, BorderBlack, RoundedCornerShape(8.dp))) {
                SortChip("Top", selected = sort == "top") { onSortChange("top") }
                SortChip("Terbaru", selected = sort == "new") { onSortChange("new") }
            }
        }

        Row(
            Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .border(2.dp, BorderBlack, CircleShape)
                    .background(Color.White)
            )
            Spacer(Modifier.width(8.dp))
            NeoTextField(
                value = text,
                onValueChange = onTextChange,
                placeholder = "Tulis komentar...",
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            Box(
                Modifier
                    .size(36.dp)
                    .background(Blue, RoundedCornerShape(8.dp))
                    .border(2.dp, BorderBlack, RoundedCornerShape(8.dp))
                    .clickable(enabled = !submitting) { onSubmit() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }

        when {
            loading -> Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) { LoadingIndicator() }
            comments.isEmpty() -> Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                Text("Belum ada komentar.", color = Color.Gray, fontSize = 13.sp)
            }
            else -> comments.forEach { c -> CommentRow(c, c.userId == "", onDelete = { onDelete(c.id) }) }
        }
    }
}

@Composable
private fun SortChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .background(if (selected) Blue else Color.White, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (selected) Color.White else Dark)
    }
}

@Composable
private fun CommentRow(comment: Comment, isOwn: Boolean, onDelete: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        AsyncImage(
            model = comment.userAvatar.ifEmpty { null },
            contentDescription = null,
            modifier = Modifier.size(36.dp).clip(CircleShape).border(1.5.dp, BorderBlack, CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(comment.username, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Dark)
                Box(
                    Modifier
                        .border(1.5.dp, BorderBlack, RoundedCornerShape(4.dp))
                        .background(Color(0xFFFF6B35))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text("Lvl. ${comment.userLevel}", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
                Text(comment.createdAt, fontSize = 11.sp, color = Color.Gray)
            }
            Text(comment.content, fontSize = 13.sp, color = Dark, modifier = Modifier.padding(top = 2.dp))
            Row(Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TextButton(onClick = {}) { Text("Balas", fontSize = 12.sp) }
                if (isOwn) TextButton(onClick = onDelete) { Text("Hapus", color = Color.Red, fontSize = 12.sp) }
            }
        }
    }
}

@Composable
private fun FullscreenPlayer(
    videoUrl: String,
    speed: Float,
    showControls: Boolean,
    onToggleControls: () -> Unit,
    onBack: () -> Unit,
    onExitFullscreen: () -> Unit
) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            playWhenReady = true
        }
    }

    LaunchedEffect(videoUrl) {
        exoPlayer.setMediaItem(MediaItem.fromUri(videoUrl))
        exoPlayer.prepare()
    }

    LaunchedEffect(speed) {
        exoPlayer.playbackParameters = PlaybackParameters(speed)
    }

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    Box(modifier = Modifier.fillMaxSize().clickable { onToggleControls() }) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        if (showControls) {
            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f))) {
                Pill("← Back", onClick = onBack)
                    .let { Box(Modifier.align(Alignment.TopStart).padding(8.dp)) { it } }

                Row(
                    Modifier.align(Alignment.TopEnd).padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Pill("${speed}x", onClick = {})
                    Box(
                        Modifier
                            .border(2.dp, Color.Black, RoundedCornerShape(50))
                            .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(50))
                            .clickable { onExitFullscreen() }
                            .padding(4.dp)
                    ) {
                        Icon(Icons.Filled.Fullscreen, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                    }
                }

                Row(
                    Modifier.align(Alignment.Center),
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CtlBtn(Icons.Filled.FastRewind, onClick = { exoPlayer.seekTo((exoPlayer.currentPosition - 10000).coerceAtLeast(0)) })
                    Box(
                        Modifier.size(64.dp).background(Color.White.copy(alpha = 0.2f), CircleShape)
                            .clickable { if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            if (exoPlayer.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp)
                        )
                    }
                    CtlBtn(Icons.Filled.FastForward, onClick = { exoPlayer.seekTo((exoPlayer.currentPosition + 10000).coerceAtMost(exoPlayer.duration.coerceAtLeast(0))) })
                }
            }
        }
    }
}
