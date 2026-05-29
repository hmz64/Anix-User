package com.anix.app.data.models

import com.google.gson.annotations.SerializedName

// API Response Wrapper
data class ApiResponse<T>(
    val success: Boolean = false,
    val data: T? = null,
    val error: String? = null,
    val meta: PaginationMeta? = null
)

data class PaginationMeta(
    val page: Int = 1,
    val limit: Int = 20,
    val total: Int = 0,
    val totalPages: Int = 0
)

// Auth
data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

data class AuthResponse(
    @SerializedName("access_token") val token: String,
    val user: User
)

// User
data class User(
    val id: Long = 0,
    val username: String = "",
    val email: String = "",
    val avatar: String = "",
    val banner: String = "",
    val level: Int = 1,
    val xp: Int = 0,
    val xpToNextLevel: Int = 100,
    val bio: String = "",
    @SerializedName("is_premium") val premium: Boolean = false,
    @SerializedName("privacy_setting") val privacySetting: String = "public",
    @SerializedName("show_leaderboard") val showLeaderboard: Boolean = true,
    @SerializedName("push_enabled") val pushEnabled: Boolean = true,
    val role: String = "",
    val createdAt: String = "",
    val lastActiveAt: String? = null
)

data class UserSession(
    val token: String = "",
    val user: User? = null
)

data class UserStats(
    val totalComments: Int = 0,
    val totalFavorites: Int = 0,
    val totalWatched: Int = 0,
    val totalXp: Int = 0
)

data class UpdateNameRequest(
    val username: String
)

data class UpdateBannerRequest(
    val banner: String
)

data class PrivacyRequest(
    @SerializedName("privacy_setting") val privacySetting: String = "public"
)

data class ToggleFavoriteRequest(
    val animeId: String
)

data class UpdateProgressRequest(
    @SerializedName("episode_id") val episodeId: String,
    val progress: Int = 0,
    val completed: Boolean = false
)

// Anime
data class AnimeSeries(
    val id: String = "",
    val title: String = "",
    @SerializedName("title_japanese") val titleJapanese: String = "",
    @SerializedName("synopsis") val description: String = "",
    @SerializedName("poster_url") val poster: String = "",
    @SerializedName("cover_url") val cover: String = "",
    @SerializedName("banner_url") val banner: String = "",
    val rating: Double = 0.0,
    val status: String = "",
    val type: String = "",
    @SerializedName("year") val releaseYear: Int = 0,
    @SerializedName("total_episodes") val totalEpisodes: Int = 0,
    val duration: String = "",
    val genres: List<Genre> = emptyList(),
    val episodes: List<Episode> = emptyList(),
    @SerializedName("is_favorited") val isFavorited: Boolean = false,
    val createdAt: String = ""
)

data class Genre(
    val id: String = "",
    val name: String = "",
    val slug: String = ""
)

data class Episode(
    val id: String = "",
    val animeId: String = "",
    val number: Int = 0,
    val title: String = "",
    val thumbnail: String = "",
    val duration: String = "",
    val description: String = "",
    val streams: List<EpisodeStream> = emptyList(),
    val releaseDate: String = "",
    val isWatched: Boolean = false,
    val watchProgress: Int = 0
)

data class EpisodeStream(
    val id: String = "",
    val url: String = "",
    val quality: String = "",
    val format: String = ""
)

data class StreamResolveRequest(
    val episodeId: String,
    val quality: String = "1080p"
)

data class StreamResolveResponse(
    val url: String = "",
    val quality: String = ""
)

// Comments
data class Comment(
    val id: String = "",
    val userId: String = "",
    val username: String = "",
    val userAvatar: String = "",
    val userLevel: Int = 1,
    val userBanner: String = "",
    val animeId: String = "",
    val episodeId: String? = null,
    val parentId: String? = null,
    val content: String = "",
    val replyCount: Int = 0,
    val replies: List<Comment> = emptyList(),
    val createdAt: String = ""
)

data class CommentReport(
    val id: String = "",
    val commentId: String = "",
    val userId: String = "",
    val reason: String = "",
    val createdAt: String = ""
)

data class CreateCommentRequest(
    val content: String,
    @SerializedName("parent_id") val parentId: String? = null
)

data class ReportCommentRequest(
    val reason: String
)

// Report (video/subtitle/etc)
data class ReportRequest(
    val type: String,
    val message: String = "",
    val episodeId: String = ""
)

// Watch History
data class WatchHistory(
    val id: String = "",
    val userId: String = "",
    val animeId: String = "",
    val anime: AnimeSeries? = null,
    val episodeId: String = "",
    val episode: Episode? = null,
    val position: Int = 0,
    val completed: Boolean = false,
    val updatedAt: String = ""
)

data class UserFavorite(
    val id: String = "",
    val userId: String = "",
    val animeId: String = "",
    val anime: AnimeSeries? = null,
    val createdAt: String = ""
)

// Chat
data class Conversation(
    val id: String = "",
    val participants: List<ConversationParticipant> = emptyList(),
    val lastMessage: Message? = null,
    val unreadCount: Int = 0,
    val updatedAt: String = ""
)

data class ConversationParticipant(
    val userId: String = "",
    val username: String = "",
    val avatar: String = ""
)

data class Message(
    val id: String = "",
    val conversationId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val content: String = "",
    val read: Boolean = false,
    val createdAt: String = ""
)

data class SendMessageRequest(
    val conversationId: String,
    val content: String
)

data class FriendRequest(
    val id: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderAvatar: String = "",
    val receiverId: String = "",
    val status: String = "",
    val createdAt: String = ""
)

data class FriendRequestAction(
    val accept: Boolean
)

data class SearchUsersResponse(
    val id: String = "",
    val username: String = "",
    val avatar: String = "",
    val level: Int = 1
)

// Clans
data class Clan(
    val id: String = "",
    val name: String = "",
    val tag: String = "",
    val logo: String = "",
    val banner: String = "",
    val description: String = "",
    val level: Int = 1,
    val xp: Int = 0,
    val xpToNextLevel: Int = 100,
    val memberCount: Int = 0,
    val maxMembers: Int = 50,
    val ownerId: String = "",
    val ownerName: String = "",
    val members: List<ClanMember> = emptyList(),
    val wallet: ClanWallet? = null,
    val upgrades: List<ClanUpgrade> = emptyList(),
    val createdAt: String = ""
)

data class ClanMember(
    val id: String = "",
    val userId: String = "",
    val username: String = "",
    val avatar: String = "",
    val role: String = "member",
    val level: Int = 1,
    val donated: Int = 0,
    val joinedAt: String = ""
)

data class ClanWallet(
    val balance: Int = 0,
    val totalDonated: Int = 0
)

data class ClanUpgrade(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val cost: Int = 0,
    val level: Int = 1,
    val maxLevel: Int = 5,
    val effect: String = ""
)

data class CreateClanRequest(
    val name: String,
    val tag: String,
    val description: String = ""
)

data class KickMemberRequest(
    val memberId: String
)

data class UpdateRoleRequest(
    val memberId: String,
    val role: String
)

data class DonateRequest(
    val amount: Int
)

data class PurchaseUpgradeRequest(
    val upgradeId: String
)

// Giveaway
data class Giveaway(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val prizeImage: String = "",
    val prize: String = "",
    val entries: Int = 0,
    val maxEntries: Int = 0,
    val creatorId: String = "",
    val creatorName: String = "",
    val endsAt: String = "",
    val createdAt: String = "",
    val isEntered: Boolean = false,
    val isActive: Boolean = true,
    val winnerId: String? = null,
    val winnerName: String? = null
)

data class GiveawayEntry(
    val id: String = "",
    val giveawayId: String = "",
    val userId: String = "",
    val username: String = "",
    val avatar: String = "",
    val createdAt: String = ""
)

data class CreateGiveawayRequest(
    val title: String,
    val description: String,
    val prize: String,
    val prizeImage: String,
    val maxEntries: Int,
    val endsAt: String
)

data class TopGiver(
    val userId: String = "",
    val username: String = "",
    val avatar: String = "",
    val totalGiven: Int = 0
)

// Notifications
data class Notification(
    val id: String = "",
    val userId: String = "",
    val type: String = "",
    val title: String = "",
    val body: String = "",
    val read: Boolean = false,
    val data: String? = null,
    val createdAt: String = ""
)

data class NotificationToken(
    val token: String = "",
    val platform: String = "android"
)

// Premium
data class PremiumSubscription(
    val id: String = "",
    val userId: String = "",
    val plan: String = "",
    val expiresAt: String = "",
    val active: Boolean = false,
    val giftedBy: String? = null
)

data class GiftPremiumRequest(
    val userId: String,
    val plan: String,
    val duration: Int
)

// Player
data class HeatwavePoint(val fraction: Float, val score: Float)

// Banner
data class Banner(
    val id: String = "",
    @SerializedName("image_url") val image: String = "",
    val title: String = "",
    @SerializedName("link_url") val linkUrl: String = "",
    @SerializedName("is_active") val active: Boolean = true
)
