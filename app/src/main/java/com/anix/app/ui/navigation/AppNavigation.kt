package com.anix.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.anix.app.core.theme.BorderBlack
import com.anix.app.core.theme.Primary
import com.anix.app.core.theme.Surface
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
import com.anix.app.ui.screens.player.VideoPlayerScreen
import com.anix.app.ui.screens.profile.ProfileScreen
import com.anix.app.ui.screens.profile.UserProfileScreen
import com.anix.app.ui.screens.search.SearchScreen
import com.anix.app.ui.screens.settings.SettingsScreen
import com.anix.app.ui.screens.social.SocialFeedScreen
import com.anix.app.ui.screens.social.SocialPostDetailScreen
import com.anix.app.ui.screens.splash.SplashScreen

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val SEARCH = "search"
    const val ANIME_DETAIL = "anime/{animeId}"
    const val VIDEO_PLAYER = "player/{episodeId}"
    const val COMMENTS = "comments/{animeId}"
    const val CHAT_LIST = "chat_list"
    const val CHAT_DETAIL = "chat/{conversationId}"
    const val CLAN_LIST = "clans"
    const val CLAN_DETAIL = "clan/{clanId}"
    const val GIVEAWAY_LIST = "giveaways"
    const val GIVEAWAY_DETAIL = "giveaway/{giveawayId}"
    const val PROFILE = "profile"
    const val NOTIFICATIONS = "notifications"
    const val SOCIAL_FEED = "social"
    const val SETTINGS = "settings"
    const val ONBOARDING = "onboarding"
    const val ANIME_LIST = "anime_list/{category}"
    const val USER_PROFILE = "user/{userId}"
    const val SOCIAL_POST_DETAIL = "post/{postId}"

    fun animeDetail(id: String) = "anime/$id"
    fun videoPlayer(episodeId: String) = "player/$episodeId"
    fun comments(animeId: String) = "comments/$animeId"
    fun chatDetail(id: String) = "chat/$id"
    fun clanDetail(id: String) = "clan/$id"
    fun giveawayDetail(id: String) = "giveaway/$id"
    fun animeList(category: String) = "anime_list/$category"
    fun userProfile(userId: String) = "user/$userId"
    fun socialPostDetail(postId: String) = "post/$postId"
}

data class BottomNavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String
)

val bottomNavItems = listOf(
    BottomNavItem("Home", Icons.Filled.Home, Icons.Outlined.Home, Routes.HOME),
    BottomNavItem("Search", Icons.Filled.Search, Icons.Outlined.Search, Routes.SEARCH),
    BottomNavItem("Chat", Icons.Filled.Chat, Icons.Outlined.Chat, Routes.CHAT_LIST),
    BottomNavItem("Feed", Icons.Filled.Notifications, Icons.Outlined.Notifications, Routes.SOCIAL_FEED),
    BottomNavItem("Profile", Icons.Filled.Person, Icons.Outlined.Person, Routes.PROFILE),
)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onNavigateToLogin = { navController.navigate(Routes.LOGIN) { popUpTo(Routes.SPLASH) { inclusive = true } } },
                onNavigateToHome = { navController.navigate(Routes.HOME) { popUpTo(Routes.SPLASH) { inclusive = true } } },
                onNavigateToOnboarding = { navController.navigate(Routes.ONBOARDING) { popUpTo(Routes.SPLASH) { inclusive = true } } }
            )
        }
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(Routes.HOME) { popUpTo(Routes.LOGIN) { inclusive = true } } },
                onRegisterClick = { navController.navigate(Routes.REGISTER) }
            )
        }
        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate(Routes.HOME) { popUpTo(Routes.REGISTER) { inclusive = true } } },
                onLoginClick = { navController.popBackStack() }
            )
        }
        composable(Routes.ANIME_DETAIL, arguments = listOf(navArgument("animeId") { type = NavType.StringType })) {
            val animeId = it.arguments?.getString("animeId") ?: return@composable
            AnimeDetailScreen(
                animeId = animeId,
                onEpisodeClick = { episodeId -> navController.navigate(Routes.videoPlayer(episodeId)) },
                onBack = { navController.popBackStack() },
                onCommentsClick = { navController.navigate(Routes.comments(animeId)) }
            )
        }
        composable(Routes.VIDEO_PLAYER, arguments = listOf(navArgument("episodeId") { type = NavType.StringType })) {
            val episodeId = it.arguments?.getString("episodeId") ?: return@composable
            VideoPlayerScreen(
                episodeId = episodeId,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.COMMENTS, arguments = listOf(navArgument("animeId") { type = NavType.StringType })) {
            val animeId = it.arguments?.getString("animeId") ?: return@composable
            CommentsScreen(
                animeId = animeId,
                episodeId = animeId,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.CHAT_DETAIL, arguments = listOf(navArgument("conversationId") { type = NavType.StringType })) {
            val conversationId = it.arguments?.getString("conversationId") ?: return@composable
            ChatDetailScreen(
                conversationId = conversationId,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.CLAN_DETAIL, arguments = listOf(navArgument("clanId") { type = NavType.StringType })) {
            val clanId = it.arguments?.getString("clanId") ?: return@composable
            ClanDetailScreen(
                clanId = clanId,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.GIVEAWAY_DETAIL, arguments = listOf(navArgument("giveawayId") { type = NavType.StringType })) {
            val giveawayId = it.arguments?.getString("giveawayId") ?: return@composable
            GiveawayDetailScreen(
                giveawayId = giveawayId,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.SETTINGS) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
        composable(Routes.NOTIFICATIONS) {
            NotificationsScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.ANIME_LIST, arguments = listOf(navArgument("category") { type = NavType.StringType })) {
            val category = it.arguments?.getString("category") ?: return@composable
            AnimeListScreen(category = category, onAnimeClick = { navController.navigate(Routes.animeDetail(it)) }, onBack = { navController.popBackStack() })
        }

        composable(Routes.USER_PROFILE, arguments = listOf(navArgument("userId") { type = NavType.StringType })) {
            val userId = it.arguments?.getString("userId") ?: return@composable
            UserProfileScreen(userId = userId, onBack = { navController.popBackStack() }, onChatClick = { navController.navigate(Routes.chatDetail(it)) })
        }

        composable(Routes.SOCIAL_POST_DETAIL, arguments = listOf(navArgument("postId") { type = NavType.StringType })) {
            val postId = it.arguments?.getString("postId") ?: return@composable
            SocialPostDetailScreen(postId = postId, onBack = { navController.popBackStack() })
        }

        composable(Routes.ONBOARDING) {
            OnboardingScreen(onComplete = { navController.navigate(Routes.LOGIN) { popUpTo(Routes.ONBOARDING) { inclusive = true } } })
        }

        composable(Routes.GIVEAWAY_LIST) {
            GiveawayListScreen(onGiveawayClick = { navController.navigate(Routes.giveawayDetail(it)) })
        }

        composable(Routes.CLAN_LIST) {
            ClanListScreen(onClanClick = { navController.navigate(Routes.clanDetail(it)) })
        }

        composable(Routes.CHAT_LIST) {
            ChatListScreen(onChatClick = { navController.navigate(Routes.chatDetail(it)) })
        }

        composable(Routes.SOCIAL_FEED) {
            SocialFeedScreen(onPostClick = { })
        }

        composable(Routes.HOME) {
            MainScreen(navController = navController)
        }

        composable(Routes.SEARCH) {
            SearchScreen(onAnimeClick = { navController.navigate(Routes.animeDetail(it)) })
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                onSettingsClick = { navController.navigate(Routes.SETTINGS) },
                onAnimeClick = { navController.navigate(Routes.animeDetail(it)) },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
fun MainScreen(navController: NavHostController) {
    val tabNavController = rememberNavController()
    val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Surface
            ) {
                bottomNavItems.forEach { item ->
                    val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.label
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        selected = selected,
                        onClick = {
                            tabNavController.navigate(item.route) {
                                popUpTo(tabNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Primary,
                            selectedTextColor = Primary,
                            unselectedIconColor = BorderBlack,
                            unselectedTextColor = BorderBlack,
                            indicatorColor = Surface
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = tabNavController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    onAnimeClick = { navController.navigate(Routes.animeDetail(it)) },
                    onSeeAllClick = { category -> navController.navigate(Routes.animeList(category)) },
                    onGenreClick = { genre -> navController.navigate(Routes.animeList("genre/$genre")) }
                )
            }
            composable(Routes.SEARCH) {
                SearchScreen(onAnimeClick = { navController.navigate(Routes.animeDetail(it)) })
            }
            composable(Routes.CHAT_LIST) {
                ChatListScreen(onChatClick = { navController.navigate(Routes.chatDetail(it)) })
            }
            composable(Routes.SOCIAL_FEED) {
                SocialFeedScreen(onPostClick = { postId -> navController.navigate(Routes.socialPostDetail(postId)) })
            }
            composable(Routes.PROFILE) {
                ProfileScreen(
                    onSettingsClick = { navController.navigate(Routes.SETTINGS) },
                    onAnimeClick = { navController.navigate(Routes.animeDetail(it)) },
                    onLogout = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
