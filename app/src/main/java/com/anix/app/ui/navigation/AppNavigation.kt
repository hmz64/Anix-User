package com.anix.app.ui.navigation

import android.util.Log
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseInCubic
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.anix.app.ui.components.FloatingMiniPlayer
import com.anix.app.ui.components.GlassNavBar
import com.anix.app.ui.components.NavItem
import com.anix.app.ui.screens.auth.GoogleRegisterScreen
import com.anix.app.ui.screens.auth.LoginScreen
import com.anix.app.ui.screens.auth.RegisterScreen
import com.anix.app.ui.screens.chat.ChatDetailScreen
import com.anix.app.ui.screens.chat.ChatListScreen
import com.anix.app.ui.screens.clans.ClanDetailScreen
import com.anix.app.ui.screens.clans.ClanListScreen
import com.anix.app.ui.screens.comments.CommentsScreen
import com.anix.app.ui.screens.detail.AnimeDetailScreen
import com.anix.app.ui.screens.giveaways.GiveawayDetailScreen
import com.anix.app.ui.screens.giveaways.GiveawayListScreen
import com.anix.app.ui.screens.home.AnimeListScreen
import com.anix.app.ui.screens.home.HomeScreen
import com.anix.app.ui.screens.notifications.NotificationsScreen
import com.anix.app.ui.screens.onboarding.OnboardingScreen
import com.anix.app.ui.screens.player.PlayerViewModel
import com.anix.app.ui.screens.player.VideoPlayerScreen
import com.anix.app.ui.screens.profile.ProfileScreen
import com.anix.app.ui.screens.profile.UserProfileScreen
import com.anix.app.ui.screens.search.SearchScreen
import com.anix.app.ui.screens.settings.SettingsScreen
import com.anix.app.ui.screens.splash.SplashScreen

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val GOOGLE_REGISTER = "google_register"
    const val HOME = "home"
    const val SEARCH = "search"
    const val ANIME_DETAIL = "anime/{animeId}"
    const val VIDEO_PLAYER = "player/{episodeId}/{animeId}"
    const val COMMENTS = "comments/{animeId}/{episodeId}"
    const val CHAT_LIST = "chat_list"
    const val CHAT_DETAIL = "chat/{conversationId}"
    const val CLAN_LIST = "clans"
    const val CLAN_DETAIL = "clan/{clanId}"
    const val GIVEAWAY_LIST = "giveaways"
    const val GIVEAWAY_DETAIL = "giveaway/{giveawayId}"
    const val PROFILE = "profile"
    const val NOTIFICATIONS = "notifications"
    const val SETTINGS = "settings"
    const val ONBOARDING = "onboarding"
    const val ANIME_LIST = "anime_list/{category}"
    const val USER_PROFILE = "user/{userId}"

    fun animeDetail(id: String) = "anime/$id"
    fun videoPlayer(episodeId: String, animeId: String) = "player/$episodeId/$animeId"
    fun comments(animeId: String, episodeId: String) = "comments/$animeId/$episodeId"
    fun chatDetail(id: String) = "chat/$id"
    fun clanDetail(id: String) = "clan/$id"
    fun giveawayDetail(id: String) = "giveaway/$id"
    fun animeList(category: String) = "anime_list/$category"
    fun userProfile(userId: String) = "user/$userId"
    fun googleRegister() = "google_register"
}

private val navItems = listOf(
    NavItem(route = Routes.HOME, icon = Icons.Outlined.Home, iconSelected = Icons.Filled.Home, label = "Home"),
    NavItem(route = Routes.SEARCH, icon = Icons.Outlined.Search, iconSelected = Icons.Filled.Search, label = "Search"),
    NavItem(route = Routes.CHAT_LIST, icon = Icons.Outlined.Chat, iconSelected = Icons.Filled.Chat, label = "Chat"),
    NavItem(route = Routes.PROFILE, icon = Icons.Outlined.Person, iconSelected = Icons.Filled.Person, label = "Profile"),
)

private val topLevelRoutes = navItems.map { it.route }

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val playerViewModel: PlayerViewModel = viewModel()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showMiniPlayer = playerViewModel.isMiniPlayerVisible
        && currentRoute != Routes.SETTINGS
        && currentRoute?.startsWith("player/") != true

    val showNavBar = currentRoute in topLevelRoutes

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Routes.SPLASH,
            enterTransition = {
                fadeIn(animationSpec = tween(300, easing = EaseOutCubic)) +
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = spring(
                            dampingRatio = androidx.compose.animation.core.Spring.DampingRatioMediumBouncy,
                            stiffness = androidx.compose.animation.core.Spring.StiffnessLow
                        ),
                        initialOffset = { it / 10 }
                    )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(200, easing = EaseInCubic)) +
                    scaleOut(targetScale = 0.97f, animationSpec = tween(200))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(300)) +
                    scaleIn(initialScale = 0.97f, animationSpec = tween(300, easing = EaseOutCubic))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(200)) +
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Down,
                        animationSpec = tween(200, easing = EaseInCubic),
                        targetOffset = { it / 10 }
                    )
            }
        ) {
            composable(Routes.SPLASH) {
                SplashScreen(
                    onNavigateToLogin = { Log.d("AnixNav", "onNavigateToLogin"); navController.navigate(Routes.LOGIN) { popUpTo(Routes.SPLASH) { inclusive = true } } },
                    onNavigateToHome = { Log.d("AnixNav", "onNavigateToHome"); navController.navigate(Routes.HOME) { popUpTo(Routes.SPLASH) { inclusive = true } } },
                    onNavigateToOnboarding = { Log.d("AnixNav", "onNavigateToOnboarding"); navController.navigate(Routes.ONBOARDING) { popUpTo(Routes.SPLASH) { inclusive = true } } }
                )
            }
            composable(Routes.LOGIN) {
                LoginScreen(
                    onLoginSuccess = { Log.d("AnixNav", "onLoginSuccess"); navController.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } } },
                    onRegisterClick = { Log.d("AnixNav", "onRegisterClick"); navController.navigate(Routes.REGISTER) },
                    onGoogleRegister = {
                        Log.d("AnixNav", "onGoogleRegister")
                        navController.navigate(Routes.googleRegister())
                    }
                )
            }
            composable(Routes.REGISTER) {
                RegisterScreen(
                    onRegisterSuccess = { Log.d("AnixNav", "onRegisterSuccess"); navController.navigate(Routes.HOME) { popUpTo(Routes.REGISTER) { inclusive = true } } },
                    onLoginClick = { Log.d("AnixNav", "onLoginClick"); navController.popBackStack() }
                )
            }
            composable(Routes.GOOGLE_REGISTER) {
                GoogleRegisterScreen(
                    onSuccess = { Log.d("AnixNav", "onGoogleRegSuccess"); navController.navigate(Routes.HOME) { popUpTo(0) { inclusive = true } } },
                    onBack = { Log.d("AnixNav", "onGoogleRegBack"); navController.popBackStack() }
                )
            }
            composable(Routes.ANIME_DETAIL, arguments = listOf(navArgument("animeId") { type = NavType.StringType })) {
                val animeId = it.arguments?.getString("animeId") ?: return@composable
                AnimeDetailScreen(
                    animeId = animeId,
                    onEpisodeClick = { episodeId -> Log.d("AnixNav", "onEpisodeClick: $episodeId"); navController.navigate(Routes.videoPlayer(episodeId, animeId)) },
                    onBack = { Log.d("AnixNav", "onBack: AnimeDetail"); navController.popBackStack() }
                )
            }
            composable(Routes.VIDEO_PLAYER, arguments = listOf(
                navArgument("episodeId") { type = NavType.StringType },
                navArgument("animeId") { type = NavType.StringType }
            )) {
                val episodeId = it.arguments?.getString("episodeId") ?: return@composable
                val animeId = it.arguments?.getString("animeId") ?: return@composable
                VideoPlayerScreen(
                    episodeId = episodeId,
                    animeId = animeId,
                    onBack = { Log.d("AnixNav", "onBack: VideoPlayer($episodeId)"); navController.popBackStack() },
                    onCommentsClick = { Log.d("AnixNav", "onCommentsClick: anime=$animeId ep=$episodeId"); navController.navigate(Routes.comments(animeId, episodeId)) },
                    playerViewModel = playerViewModel
                )
            }
            composable(Routes.COMMENTS, arguments = listOf(
                navArgument("animeId") { type = NavType.StringType },
                navArgument("episodeId") { type = NavType.StringType }
            )) {
                val animeId = it.arguments?.getString("animeId") ?: return@composable
                val episodeId = it.arguments?.getString("episodeId") ?: return@composable
                CommentsScreen(
                    animeId = animeId,
                    episodeId = episodeId,
                    onBack = { Log.d("AnixNav", "onBack: Comments"); navController.popBackStack() }
                )
            }
            composable(Routes.CHAT_DETAIL, arguments = listOf(navArgument("conversationId") { type = NavType.StringType })) {
                val conversationId = it.arguments?.getString("conversationId") ?: return@composable
                ChatDetailScreen(
                    conversationId = conversationId,
                    onBack = { Log.d("AnixNav", "onBack: ChatDetail($conversationId)"); navController.popBackStack() }
                )
            }
            composable(Routes.CLAN_DETAIL, arguments = listOf(navArgument("clanId") { type = NavType.StringType })) {
                val clanId = it.arguments?.getString("clanId") ?: return@composable
                ClanDetailScreen(
                    clanId = clanId,
                    onBack = { Log.d("AnixNav", "onBack: ClanDetail($clanId)"); navController.popBackStack() }
                )
            }
            composable(Routes.GIVEAWAY_DETAIL, arguments = listOf(navArgument("giveawayId") { type = NavType.StringType })) {
                val giveawayId = it.arguments?.getString("giveawayId") ?: return@composable
                GiveawayDetailScreen(
                    giveawayId = giveawayId,
                    onBack = { Log.d("AnixNav", "onBack: GiveawayDetail($giveawayId)"); navController.popBackStack() }
                )
            }
            composable(Routes.SETTINGS) {
                SettingsScreen(
                    onBack = { Log.d("AnixNav", "onBack: Settings"); navController.popBackStack() },
                    onLogout = {
                        Log.d("AnixNav", "onLogout");
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.NOTIFICATIONS) {
                NotificationsScreen(onBack = { Log.d("AnixNav", "onBack: Notifications"); navController.popBackStack() })
            }
            composable(Routes.ANIME_LIST, arguments = listOf(navArgument("category") { type = NavType.StringType })) {
                val category = it.arguments?.getString("category") ?: return@composable
                AnimeListScreen(
                    category = category,
                    onAnimeClick = { id -> Log.d("AnixNav", "onAnimeClick: $id"); navController.navigate(Routes.animeDetail(id)) },
                    onBack = { Log.d("AnixNav", "onBack: AnimeList"); navController.popBackStack() }
                )
            }
            composable(Routes.USER_PROFILE, arguments = listOf(navArgument("userId") { type = NavType.StringType })) {
                val userId = it.arguments?.getString("userId") ?: return@composable
                UserProfileScreen(
                    userId = userId,
                    onBack = { Log.d("AnixNav", "onBack: UserProfile($userId)"); navController.popBackStack() },
                    onChatClick = { convId -> Log.d("AnixNav", "onChatClick: $convId"); navController.navigate(Routes.chatDetail(convId)) }
                )
            }
            composable(Routes.ONBOARDING) {
                OnboardingScreen(
                    onComplete = { Log.d("AnixNav", "onComplete: Onboarding"); navController.navigate(Routes.LOGIN) { popUpTo(Routes.ONBOARDING) { inclusive = true } } }
                )
            }
            composable(Routes.GIVEAWAY_LIST) {
                GiveawayListScreen(
                    onGiveawayClick = { id -> Log.d("AnixNav", "onGiveawayClick: $id"); navController.navigate(Routes.giveawayDetail(id)) }
                )
            }
            composable(Routes.CLAN_LIST) {
                ClanListScreen(
                    onClanClick = { id -> Log.d("AnixNav", "onClanClick: $id"); navController.navigate(Routes.clanDetail(id)) }
                )
            }

            // ── Tab screens ────────────────────────────────────────────
            composable(Routes.HOME) {
                HomeScreen(
                    onAnimeClick = { id -> Log.d("AnixNav", "onAnimeClick: $id"); navController.navigate(Routes.animeDetail(id)) },
                    onSeeAllClick = { category -> Log.d("AnixNav", "onSeeAllClick: $category"); navController.navigate(Routes.animeList(category)) },
                    onGenreClick = { genre -> Log.d("AnixNav", "onGenreClick: $genre"); navController.navigate(Routes.animeList(genre)) },
                    onNotificationClick = { Log.d("AnixNav", "onNotificationClick"); navController.navigate(Routes.NOTIFICATIONS) }
                )
            }
            composable(Routes.SEARCH) {
                SearchScreen(
                    onAnimeClick = { id -> Log.d("AnixNav", "onAnimeClick: $id"); navController.navigate(Routes.animeDetail(id)) }
                )
            }
            composable(Routes.CHAT_LIST) {
                ChatListScreen(
                    onChatClick = { id -> Log.d("AnixNav", "onChatClick: $id"); navController.navigate(Routes.chatDetail(id)) }
                )
            }
            composable(Routes.PROFILE) {
                ProfileScreen(
                    onSettingsClick = { Log.d("AnixNav", "onSettingsClick"); navController.navigate(Routes.SETTINGS) },
                    onAnimeClick = { id -> Log.d("AnixNav", "onAnimeClick: $id"); navController.navigate(Routes.animeDetail(id)) },
                    onLogout = {
                        Log.d("AnixNav", "onLogout");
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }

        if (showNavBar) {
            GlassNavBar(
                items = navItems,
                currentRoute = currentRoute,
                onItemClick = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        if (showMiniPlayer) {
            FloatingMiniPlayer(
                viewModel = playerViewModel,
                onTap = {
                    val epId = playerViewModel.resumeEpisodeId
                    val anId = playerViewModel.resumeAnimeId
                    if (epId != null && anId != null) {
                        playerViewModel.closeMiniPlayer()
                        navController.navigate(Routes.videoPlayer(epId, anId))
                    }
                }
            )
        }
    }
}
