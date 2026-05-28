package com.anix.app.core.network

import com.anix.app.data.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Health
    @GET("health")
    suspend fun health(): Response<ApiResponse<Unit>>

    // Auth
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<AuthResponse>>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<AuthResponse>>

    @POST("api/auth/refresh")
    suspend fun refreshToken(): Response<ApiResponse<AuthResponse>>

    @GET("api/auth/me")
    suspend fun me(): Response<ApiResponse<User>>

    @POST("api/auth/logout")
    suspend fun logout(): Response<ApiResponse<Unit>>

    // Anime
    @GET("api/anime")
    suspend fun getAnimeList(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("genre") genre: String? = null,
        @Query("sort") sort: String? = null
    ): Response<ApiResponse<List<AnimeSeries>>>

    @GET("api/anime/search")
    suspend fun searchAnime(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<AnimeSeries>>>

    @GET("api/anime/schedule")
    suspend fun getSchedule(): Response<ApiResponse<List<AnimeSeries>>>

    @GET("api/anime/ranking")
    suspend fun getRanking(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<AnimeSeries>>>

    @GET("api/anime/genres")
    suspend fun getGenres(): Response<ApiResponse<List<Genre>>>

    @GET("api/anime/{id}")
    suspend fun getAnimeDetail(@Path("id") id: String): Response<ApiResponse<AnimeSeries>>

    @GET("api/anime/{id}/episodes")
    suspend fun getAnimeEpisodes(@Path("id") id: String): Response<ApiResponse<List<Episode>>>

    // Episode
    @GET("api/episode/{id}")
    suspend fun getEpisodeDetail(@Path("id") id: String): Response<ApiResponse<Episode>>

    @GET("api/episode/{id}/streams")
    suspend fun getEpisodeStreams(@Path("id") id: String): Response<ApiResponse<List<EpisodeStream>>>

    // Comments
    @GET("api/comments")
    suspend fun getComments(
        @Query("animeId") animeId: String,
        @Query("episodeId") episodeId: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<Comment>>>

    @POST("api/comments")
    suspend fun createComment(@Body request: CreateCommentRequest): Response<ApiResponse<Comment>>

    @DELETE("api/comments/{id}")
    suspend fun deleteComment(@Path("id") id: String): Response<ApiResponse<Unit>>

    @POST("api/comments/{id}/report")
    suspend fun reportComment(
        @Path("id") id: String,
        @Body request: ReportCommentRequest
    ): Response<ApiResponse<Unit>>

    // User
    @GET("api/user/profile/{userId}")
    suspend fun getUserProfile(@Path("userId") userId: String): Response<ApiResponse<User>>

    @GET("api/user/stats")
    suspend fun getUserStats(): Response<ApiResponse<UserStats>>

    @GET("api/user/history")
    suspend fun getWatchHistory(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<WatchHistory>>>

    @GET("api/user/favorites")
    suspend fun getFavorites(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<UserFavorite>>>

    @GET("api/user/comments")
    suspend fun getUserComments(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<Comment>>>

    @GET("api/user/activities")
    suspend fun getUserActivities(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<WatchHistory>>>

    @PUT("api/user/name")
    suspend fun updateName(@Body request: UpdateNameRequest): Response<ApiResponse<User>>

    @PUT("api/user/banner")
    suspend fun updateBanner(@Body request: UpdateBannerRequest): Response<ApiResponse<User>>

    @PUT("api/user/privacy")
    suspend fun updatePrivacy(@Body request: PrivacyRequest): Response<ApiResponse<User>>

    @POST("api/user/favorites")
    suspend fun toggleFavorite(@Body request: ToggleFavoriteRequest): Response<ApiResponse<UserFavorite>>

    @PUT("api/user/progress")
    suspend fun updateProgress(@Body request: UpdateProgressRequest): Response<ApiResponse<Unit>>

    // Chat
    @GET("api/chat/conversations")
    suspend fun getConversations(): Response<ApiResponse<List<Conversation>>>

    @GET("api/chat/conversations/{id}")
    suspend fun getConversationMessages(
        @Path("id") id: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50
    ): Response<ApiResponse<List<Message>>>

    @POST("api/chat/messages")
    suspend fun sendMessage(@Body request: SendMessageRequest): Response<ApiResponse<Message>>

    @PUT("api/chat/conversations/{id}/read")
    suspend fun markConversationRead(@Path("id") id: String): Response<ApiResponse<Unit>>

    @GET("api/chat/friends")
    suspend fun getFriends(): Response<ApiResponse<List<User>>>

    @POST("api/chat/friends/request")
    suspend fun sendFriendRequest(@Body request: Map<String, String>): Response<ApiResponse<FriendRequest>>

    @PUT("api/chat/friends/request/{id}")
    suspend fun respondFriendRequest(
        @Path("id") id: String,
        @Body action: FriendRequestAction
    ): Response<ApiResponse<FriendRequest>>

    @GET("api/chat/users/search")
    suspend fun searchUsers(
        @Query("q") query: String
    ): Response<ApiResponse<List<SearchUsersResponse>>>

    // Clans
    @GET("api/clans")
    suspend fun getClans(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<Clan>>>

    @POST("api/clans")
    suspend fun createClan(@Body request: CreateClanRequest): Response<ApiResponse<Clan>>

    @GET("api/clans/{id}")
    suspend fun getClanDetail(@Path("id") id: String): Response<ApiResponse<Clan>>

    @GET("api/clans/{id}/members")
    suspend fun getClanMembers(@Path("id") id: String): Response<ApiResponse<List<ClanMember>>>

    @POST("api/clans/{id}/join")
    suspend fun joinClan(@Path("id") id: String): Response<ApiResponse<Unit>>

    @POST("api/clans/{id}/kick")
    suspend fun kickMember(
        @Path("id") id: String,
        @Body request: KickMemberRequest
    ): Response<ApiResponse<Unit>>

    @PUT("api/clans/{id}/role")
    suspend fun updateMemberRole(
        @Path("id") id: String,
        @Body request: UpdateRoleRequest
    ): Response<ApiResponse<Unit>>

    @POST("api/clans/{id}/donate")
    suspend fun donateToClan(
        @Path("id") id: String,
        @Body request: DonateRequest
    ): Response<ApiResponse<ClanWallet>>

    @POST("api/clans/{id}/boost")
    suspend fun boostClan(@Path("id") id: String): Response<ApiResponse<Unit>>

    @GET("api/clans/{id}/wallet")
    suspend fun getClanWallet(@Path("id") id: String): Response<ApiResponse<ClanWallet>>

    @GET("api/clans/upgrades")
    suspend fun getUpgradeCatalog(): Response<ApiResponse<List<ClanUpgrade>>>

    @POST("api/clans/{id}/upgrades")
    suspend fun purchaseUpgrade(
        @Path("id") id: String,
        @Body request: PurchaseUpgradeRequest
    ): Response<ApiResponse<ClanUpgrade>>

    @GET("api/clans/leaderboard")
    suspend fun getClanLeaderboard(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<Clan>>>

    @GET("api/clans/my")
    suspend fun getMyClan(): Response<ApiResponse<Clan>>

    // Giveaways
    @GET("api/giveaways")
    suspend fun getGiveaways(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<Giveaway>>>

    @GET("api/giveaways/{id}")
    suspend fun getGiveawayDetail(@Path("id") id: String): Response<ApiResponse<Giveaway>>

    @POST("api/giveaways")
    suspend fun createGiveaway(@Body request: CreateGiveawayRequest): Response<ApiResponse<Giveaway>>

    @POST("api/giveaways/{id}/claim")
    suspend fun claimGiveaway(@Path("id") id: String): Response<ApiResponse<Unit>>

    @GET("api/giveaways/top")
    suspend fun getTopGivers(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<TopGiver>>>

    // Social
    @GET("api/social/feed")
    suspend fun getSocialFeed(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<SocialPost>>>

    @POST("api/social/posts")
    suspend fun createPost(@Body request: CreatePostRequest): Response<ApiResponse<SocialPost>>

    // Notifications
    @GET("api/notifications")
    suspend fun getNotifications(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<Notification>>>

    @GET("api/notifications/unread")
    suspend fun getUnreadCount(): Response<ApiResponse<Int>>

    @PUT("api/notifications/read")
    suspend fun markAllNotificationsRead(): Response<ApiResponse<Unit>>

    @PUT("api/notifications/{id}/read")
    suspend fun markNotificationRead(@Path("id") id: String): Response<ApiResponse<Unit>>

    @POST("api/notifications/token")
    suspend fun upsertNotificationToken(@Body request: NotificationToken): Response<ApiResponse<Unit>>

    @DELETE("api/notifications/token")
    suspend fun deleteNotificationToken(): Response<ApiResponse<Unit>>

    // Premium
    @POST("api/premium/gift")
    suspend fun giftPremium(@Body request: GiftPremiumRequest): Response<ApiResponse<PremiumSubscription>>

    @PUT("api/premium")
    suspend fun updatePremium(@Body request: Map<String, String>): Response<ApiResponse<PremiumSubscription>>

    // Play
    @POST("api/play/resolve")
    suspend fun resolveStream(@Body request: StreamResolveRequest): Response<ApiResponse<StreamResolveResponse>>

    // Banners
    @GET("api/banners")
    suspend fun getBanners(): Response<ApiResponse<List<Banner>>>
}
