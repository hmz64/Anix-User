package com.anix.app.ui.screens.player

import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.ViewGroup
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.waitForUpOrCancellation
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
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.anix.app.core.di.PreferencesKeys
import com.anix.app.core.di.ServiceLocator
import com.anix.app.core.network.ApiClient
import com.anix.app.core.theme.BorderBlack
import com.anix.app.core.util.downloadVideoMp4
import com.anix.app.ui.components.AdvancedPlayerTimeline
import com.anix.app.ui.components.HandlePlayerSystemUi
import com.anix.app.ui.components.ReportDialog
import com.anix.app.data.models.AnimeSeries
import com.anix.app.data.models.Comment
import com.anix.app.data.models.Episode
import com.anix.app.ui.components.LoadingIndicator
import com.anix.app.ui.components.NeoBadge
import com.anix.app.ui.components.NeoChip
import com.anix.app.ui.components.NeoTextField
import com.anix.app.ui.screens.player.PlayerViewModel
import kotlinx.coroutines.delay
import kotlin.math.min

private val Bg = Color(0xFFF5EEE8)
private val Blue = Color(0xFF2B2BFF)
private val Dark = Color(0xFF0D0D0D)

private val speedOptions = listOf(0.25f, 0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 1.75f, 2.0f, 3.0f, 4.0f)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayerScreen(
    episodeId: String,
    animeId: String? = null,
    onBack: () -> Unit,
    onCommentsClick: ((String) -> Unit)? = null,
    playerViewModel: PlayerViewModel? = null,
    viewModel: VideoPlayerViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showControls by remember { mutableStateOf(true) }
    var showReportDialog by remember { mutableStateOf(false) }
    var showSpeedSheet by remember { mutableStateOf(false) }
    var showQualitySheet by remember { mutableStateOf(false) }

    HandlePlayerSystemUi(isFullScreen = state.isFullscreen)

    LaunchedEffect(episodeId) {
        viewModel.loadEpisode(episodeId, animeId)
    }

    LaunchedEffect(showControls) {
        if (showControls) {
            delay(3000)
            showControls = false
        }
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
            if (playerViewModel != null && playerViewModel!!.savedPosition > 0) {
                exoPlayer.seekTo(playerViewModel!!.savedPosition)
                playerViewModel!!.savePositionMs(0L)
            }
        }
    }

    LaunchedEffect(state.playbackSpeed) {
        exoPlayer.playbackParameters = PlaybackParameters(state.playbackSpeed)
    }

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release() }
    }

    var position by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(0L) }
    var buffered by remember { mutableLongStateOf(0L) }

    LaunchedEffect(Unit) {
        while (true) {
            position = exoPlayer.currentPosition
            val d = exoPlayer.duration
            duration = if (d == Long.MIN_VALUE || d < 0) 0L else d
            buffered = exoPlayer.bufferedPosition
            delay(200)
        }
    }

    if (state.isFullscreen) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            FullscreenPlayer(
                exoPlayer = exoPlayer,
                videoUrl = state.videoUrl,
                speed = state.playbackSpeed,
                showControls = showControls,
                onToggleControls = { showControls = !showControls },
                onBack = { viewModel.setFullscreen(false); onBack() },
                onExitFullscreen = {
                    (context as? Activity)?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                    viewModel.setFullscreen(false)
                },
                onChangeSpeed = { showSpeedSheet = true },
                position = position,
                duration = duration,
                buffered = buffered,
                onSeek = { exoPlayer.seekTo(it) }
            )
        }
        return
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
                                if (exoPlayer.isPlaying && playerViewModel != null) {
                                    playerViewModel.savePositionMs(exoPlayer.currentPosition)
                                    playerViewModel.playMedia(
                                        MediaItem.fromUri(state.videoUrl),
                                        state.anime?.title ?: "Now Playing",
                                        positionMs = exoPlayer.currentPosition,
                                        episodeId = episodeId,
                                        animeId = animeId
                                    )
                                } else {
                                    exoPlayer.stop()
                                }
                                onBack()
                            },
                            onFullscreen = {
                                (context as? Activity)?.let { act ->
                                    act.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                                }
                                viewModel.setFullscreen(true)
                            },
                            onChangeSpeed = { showSpeedSheet = true },
                            onChangeQuality = { showQualitySheet = true }
                        )
                    }
                }
            }
        }

        if (state.videoUrl.isNotEmpty()) {
            item(key = "seekbar") {
                Box(modifier = Modifier.background(Color.Black).padding(vertical = 4.dp)) {
                    AdvancedPlayerTimeline(
                        totalDurationMs = duration,
                        currentPositionMs = position,
                        bufferedPositionMs = buffered,
                        heatwaveData = emptyList(),
                        onSeekPerformed = { exoPlayer.seekTo(it) }
                    )
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
                currentUserId = state.currentUserId,
                currentUserAvatar = state.currentUserAvatar,
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

    if (showSpeedSheet) {
        SpeedBottomSheet(
            current = state.playbackSpeed,
            onSelect = { viewModel.setPlaybackSpeed(it); showSpeedSheet = false },
            onDismiss = { showSpeedSheet = false }
        )
    }

    if (showQualitySheet) {
        QualityBottomSheet(
            current = state.currentQuality,
            onSelect = { viewModel.setCurrentQuality(it); showQualitySheet = false },
            onDismiss = { showQualitySheet = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SpeedBottomSheet(current: Float, onSelect: (Float) -> Unit, onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text("Playback Speed", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Dark)
            Spacer(Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(speedOptions) { s ->
                    Box(
                        Modifier
                            .border(2.dp, if (s == current) Blue else BorderBlack, RoundedCornerShape(8.dp))
                            .background(if (s == current) Blue else Color.White, RoundedCornerShape(8.dp))
                            .clickable { onSelect(s) }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            "${s}x",
                            fontWeight = FontWeight.Bold,
                            color = if (s == current) Color.White else Dark,
                            fontSize = 14.sp
                        )
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QualityBottomSheet(current: String, onSelect: (String) -> Unit, onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text("Quality", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Dark)
            Spacer(Modifier.height(12.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listOf("360p", "480p", "720p", "1080p")) { q ->
                    Box(
                        Modifier
                            .border(2.dp, if (q == current) Blue else BorderBlack, RoundedCornerShape(8.dp))
                            .background(if (q == current) Blue else Color.White, RoundedCornerShape(8.dp))
                            .clickable { onSelect(q) }
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    ) {
                        Text(
                            q,
                            fontWeight = FontWeight.Bold,
                            color = if (q == current) Color.White else Dark,
                            fontSize = 14.sp
                        )
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
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
    onChangeQuality: () -> Unit,
    onSwipeDown: () -> Unit = onBack
) {
    var indicatorText by remember { mutableStateOf("") }
    var indicatorVisible by remember { mutableStateOf(false) }
    var isLongPressing by remember { mutableStateOf(false) }
    var restoreSpeed by remember { mutableFloatStateOf(1.0f) }
    var currentSpeed by remember { mutableFloatStateOf(speed) }
    LaunchedEffect(speed) { currentSpeed = speed }
    LaunchedEffect(indicatorVisible) {
        if (indicatorVisible) {
            delay(1200)
            indicatorVisible = false
        }
    }

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
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onVerticalDrag = { change, dragAmount ->
                            if (dragAmount > 200f) {
                                change.consume()
                                onSwipeDown()
                            }
                        }
                    )
                }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    awaitEachGesture {
                        awaitFirstDown()
                        val longPressMs = viewConfiguration.longPressTimeoutMillis
                        val doubleTapMs = viewConfiguration.doubleTapTimeoutMillis
                        val firstUp = withTimeoutOrNull(longPressMs) {
                            waitForUpOrCancellation()
                        }
                        if (firstUp == null) {
                            isLongPressing = true
                            restoreSpeed = currentSpeed
                            exoPlayer.playbackParameters = PlaybackParameters(2.0f)
                            indicatorText = "\u23E9 Kecepatan 2.0x"
                            indicatorVisible = true
                            try { waitForUpOrCancellation() } catch (_: Exception) {}
                            exoPlayer.playbackParameters = PlaybackParameters(restoreSpeed)
                            isLongPressing = false
                        } else {
                            val secondDown = withTimeoutOrNull(doubleTapMs) {
                                awaitFirstDown(requireUnconsumed = false)
                            }
                            if (secondDown != null) {
                                val viewWidth = size.width.toFloat()
                                if (secondDown.position.x > viewWidth / 2) {
                                    exoPlayer.seekTo((exoPlayer.currentPosition + 10000).coerceAtMost(exoPlayer.duration.coerceAtLeast(0)))
                                    indicatorText = "\u23E9 +10 detik"
                                } else {
                                    exoPlayer.seekTo((exoPlayer.currentPosition - 10000).coerceAtLeast(0))
                                    indicatorText = "\u23EA -10 detik"
                                }
                                indicatorVisible = true
                                waitForUpOrCancellation()
                            } else {
                                onToggleControls()
                            }
                        }
                    }
                }
        )

        if (showControls) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
            ) {
                Row(
                    Modifier.align(Alignment.TopStart).padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Pill("\u2190 Back", onClick = onBack)
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

        AnimatedVisibility(
            visible = indicatorVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 24.dp)
        ) {
            Box(
                Modifier
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(indicatorText, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
    currentUserId: String?,
    currentUserAvatar: String? = null,
    onTextChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onDelete: (String) -> Unit,
    onSortChange: (String) -> Unit
) {
    var showBanners by remember { mutableStateOf(true) }
    var showSettings by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        ServiceLocator.getDataStore()?.data?.collect { prefs ->
            showBanners = prefs[PreferencesKeys.SHOW_COMMENT_BANNERS] ?: true
        }
    }

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
            Spacer(Modifier.width(4.dp))
            Box {
                IconButton(onClick = { showSettings = true }) {
                    Icon(Icons.Filled.Settings, contentDescription = "Comment settings", tint = Dark, modifier = Modifier.size(20.dp))
                }
                DropdownMenu(expanded = showSettings, onDismissRequest = { showSettings = false }) {
                    DropdownMenuItem(
                        text = { Text(if (showBanners) "Sembunyikan Banner" else "Tampilkan Banner") },
                        onClick = { showBanners = !showBanners; showSettings = false }
                    )
                }
            }
        }

        Row(
            Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val inputAvatarUrl = ApiClient.resolveUrl(currentUserAvatar)?.ifEmpty { null }
            AsyncImage(
                model = inputAvatarUrl,
                contentDescription = null,
                modifier = Modifier.size(32.dp).clip(CircleShape).border(2.dp, BorderBlack, CircleShape),
                placeholder = rememberVectorPainter(Icons.Filled.Person),
                error = rememberVectorPainter(Icons.Filled.Person),
                contentScale = ContentScale.Crop
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
            else -> comments.forEach { c ->
                CommentRow(c, currentUserId = currentUserId, onDelete = { onDelete(c.id) }, showBanner = showBanners)
            }
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
private fun CommentRow(comment: Comment, currentUserId: String?, onDelete: () -> Unit, showBanner: Boolean = true) {
    val isOwn = currentUserId != null && comment.userId == currentUserId
    val bannerUrl = if (showBanner) ApiClient.resolveUrl(comment.userBanner)?.ifEmpty { null } else null
    val avatarUrl = ApiClient.resolveUrl(comment.userAvatar)?.ifEmpty { null }
    val avatarPlaceholder = rememberVectorPainter(Icons.Filled.Person)
    val bannerPlaceholder = rememberVectorPainter(Icons.Outlined.Image)
    val textColor = if (bannerUrl != null) Color.White else Dark

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        if (bannerUrl == null) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = 4.dp, y = 4.dp)
                    .background(Color.Black, RoundedCornerShape(6.dp))
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (bannerUrl != null) Color.Transparent else Color(0xFFFFF9EC),
                    RoundedCornerShape(6.dp)
                )
                .border(2.dp, Color.Black, RoundedCornerShape(6.dp))
        ) {
            if (bannerUrl != null) {
                AsyncImage(
                    model = bannerUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    contentScale = ContentScale.Crop,
                    placeholder = bannerPlaceholder,
                    error = bannerPlaceholder
                )
                Box(Modifier.matchParentSize().background(Color.Black.copy(alpha = 0.3f)))
            }
            Row(
                Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = null,
                    modifier = Modifier.size(36.dp).clip(CircleShape).border(1.5.dp, BorderBlack, CircleShape),
                    contentScale = ContentScale.Crop,
                    placeholder = avatarPlaceholder,
                    error = avatarPlaceholder
                )
                Spacer(Modifier.width(8.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(comment.username, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = textColor)
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFFF6B35))
                                .border(1.5.dp, Color.Black, RoundedCornerShape(4.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Lvl. ${comment.userLevel}", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                        Text(
                            comment.createdAt,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = if (bannerUrl != null) Color.White.copy(alpha = 0.8f) else Color.Gray
                        )
                    }
                    Text(comment.content, fontSize = 13.sp, color = textColor, modifier = Modifier.padding(top = 2.dp))
                    Row(Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        NeoActionButton("Balas", hasBanner = bannerUrl != null)
                        if (isOwn) NeoActionButton("Hapus", textColor = Color.Red, hasBanner = bannerUrl != null, onClick = onDelete)
                    }
                }
            }
        }
    }
}

@Composable
private fun NeoActionButton(text: String, textColor: Color = Color.Black, hasBanner: Boolean = false, onClick: () -> Unit = {}) {
    val bg = if (hasBanner) Color(0x33FFFFFF) else Color.White
    Box(
        modifier = Modifier
            .clickable(onClick = onClick)
            .border(1.5.dp, Color.Black, RoundedCornerShape(4.dp))
            .background(bg, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(text, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textColor)
    }
}

@Composable
private fun FullscreenPlayer(
    exoPlayer: ExoPlayer,
    videoUrl: String,
    speed: Float,
    showControls: Boolean,
    onToggleControls: () -> Unit,
    onBack: () -> Unit,
    onExitFullscreen: () -> Unit,
    onChangeSpeed: () -> Unit,
    position: Long,
    duration: Long,
    buffered: Long,
    onSeek: (Long) -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(videoUrl) {
        val current = exoPlayer.currentMediaItem?.mediaId
        if (current != videoUrl && videoUrl.isNotEmpty()) {
            exoPlayer.setMediaItem(MediaItem.fromUri(videoUrl))
            exoPlayer.prepare()
        }
    }

    LaunchedEffect(speed) {
        exoPlayer.playbackParameters = PlaybackParameters(speed)
    }

    var indicatorText by remember { mutableStateOf("") }
    var indicatorVisible by remember { mutableStateOf(false) }
    var isLongPressing by remember { mutableStateOf(false) }
    var restoreSpeed by remember { mutableFloatStateOf(1.0f) }
    var currentSpeed by remember { mutableFloatStateOf(speed) }

    LaunchedEffect(speed) { currentSpeed = speed }
    LaunchedEffect(indicatorVisible) {
        if (indicatorVisible) {
            delay(1200)
            indicatorVisible = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    awaitEachGesture {
                        awaitFirstDown()
                        val longPressMs = viewConfiguration.longPressTimeoutMillis
                        val doubleTapMs = viewConfiguration.doubleTapTimeoutMillis
                        val firstUp = withTimeoutOrNull(longPressMs) {
                            waitForUpOrCancellation()
                        }
                        if (firstUp == null) {
                            isLongPressing = true
                            restoreSpeed = currentSpeed
                            exoPlayer.playbackParameters = PlaybackParameters(2.0f)
                            indicatorText = "\u23E9 Kecepatan 2.0x"
                            indicatorVisible = true
                            try { waitForUpOrCancellation() } catch (_: Exception) {}
                            exoPlayer.playbackParameters = PlaybackParameters(restoreSpeed)
                            isLongPressing = false
                        } else {
                            val secondDown = withTimeoutOrNull(doubleTapMs) {
                                awaitFirstDown(requireUnconsumed = false)
                            }
                            if (secondDown != null) {
                                val viewWidth = size.width.toFloat()
                                if (secondDown.position.x > viewWidth / 2) {
                                    exoPlayer.seekTo((exoPlayer.currentPosition + 10000).coerceAtMost(exoPlayer.duration.coerceAtLeast(0)))
                                    indicatorText = "\u23E9 +10 detik"
                                } else {
                                    exoPlayer.seekTo((exoPlayer.currentPosition - 10000).coerceAtLeast(0))
                                    indicatorText = "\u23EA -10 detik"
                                }
                                indicatorVisible = true
                                waitForUpOrCancellation()
                            } else {
                                onToggleControls()
                            }
                        }
                    }
                }
        )

        if (showControls) {
            Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f))) {
                Row(
                    Modifier.align(Alignment.TopStart).padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Pill("\u2190 Back", onClick = onBack)
                }

                Row(
                    Modifier.align(Alignment.TopEnd).padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Pill("${speed}x", onClick = onChangeSpeed)
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

        AnimatedVisibility(
            visible = indicatorVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 24.dp)
        ) {
            Box(
                Modifier
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(indicatorText, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.6f))
        ) {
            AdvancedPlayerTimeline(
                totalDurationMs = duration,
                currentPositionMs = position,
                bufferedPositionMs = buffered,
                heatwaveData = emptyList(),
                onSeekPerformed = onSeek
            )
        }
    }
}
