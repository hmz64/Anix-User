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

    @POST("api/auth/google")
    suspend fun googleLogin(@Body request: GoogleLoginRequest): Response<okhttp3.ResponseBody>

    @POST("api/auth/google/complete")
    suspend fun completeGoogleRegistration(@Body request: GoogleCompleteRegistrationRequest): Response<ApiResponse<AuthResponse>>

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

    @GET("api/genres")
    suspend fun getGenres(): Response<ApiResponse<List<Genre>>>

    @GET("api/anime/{id}")
    suspend fun getAnimeDetail(@Path("id") id: String): Response<ApiResponse<AnimeSeries>>

    @GET("api/anime/{id}/episodes")
    suspend fun getAnimeEpisodes(@Path("id") id: String): Response<ApiResponse<List<Episode>>>

    // Episode
    @GET("api/episode/{episodeId}")
    suspend fun getEpisodeDetail(@Path("episodeId") id: String): Response<ApiResponse<Episode>>

    @GET("api/episode/{episodeId}/streams")
    suspend fun getEpisodeStreams(@Path("episodeId") id: String): Response<ApiResponse<List<EpisodeStream>>>

    @POST("api/play/resolve")
    suspend fun resolveStream(@Body request: StreamResolveRequest): Response<ApiResponse<StreamResolveResponse>>

    // Comments
    @GET("api/episode/{episodeId}/comments")
    suspend fun getComments(
        @Path("episodeId") episodeId: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<Comment>>>

    @POST("api/episode/{episodeId}/comment")
    suspend fun createComment(
        @Path("episodeId") episodeId: String,
        @Body request: CreateCommentRequest
    ): Response<ApiResponse<Comment>>

    @DELETE("api/episode/{episodeId}/comment/{commentId}")
    suspend fun deleteComment(
        @Path("episodeId") episodeId: String,
        @Path("commentId") commentId: String
    ): Response<ApiResponse<Unit>>

    @POST("api/episode/{episodeId}/comment/{commentId}/report")
    suspend fun reportComment(
        @Path("episodeId") episodeId: String,
        @Path("commentId") commentId: String,
        @Body request: ReportCommentRequest
    ): Response<ApiResponse<Unit>>

    @POST("api/report")
    suspend fun submitReport(@Body request: ReportRequest): Response<ApiResponse<Unit>>

    // User
    @GET("api/user/profile")
    suspend fun getMyProfile(): Response<ApiResponse<User>>

    @GET("api/user/profile/{userId}")
    suspend fun getUserProfile(@Path("userId") userId: String): Response<ApiResponse<User>>

    @GET("api/user/profile/stats")
    suspend fun getUserStats(): Response<ApiResponse<UserStats>>

    @GET("api/user/profile/history")
    suspend fun getWatchHistory(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<WatchHistory>>>

    @GET("api/user/profile/favorites")
    suspend fun getFavorites(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<UserFavorite>>>

    @GET("api/user/profile/comments")
    suspend fun getUserComments(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<Comment>>>

    @GET("api/user/profile/activities")
    suspend fun getUserActivities(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<WatchHistory>>>

    @PUT("api/user/update/name")
    suspend fun updateName(@Body request: UpdateNameRequest): Response<ApiResponse<User>>

    @PUT("api/user/update/name")
    suspend fun updateNameRaw(@Body request: Map<String, @JvmSuppressWildcards Any>): Response<ApiResponse<User>>

    @PUT("api/user/update/banner")
    suspend fun updateBanner(@Body request: UpdateBannerRequest): Response<ApiResponse<User>>

    @PUT("api/user/update/privacy")
    suspend fun updatePrivacy(@Body request: PrivacyRequest): Response<ApiResponse<User>>

    @PUT("api/user/update/privacy")
    suspend fun updateSettingsRaw(@Body request: Map<String, @JvmSuppressWildcards Any>): Response<ApiResponse<User>>

    @Multipart
    @PUT("api/user/update/avatar")
    suspend fun updateAvatar(@Part file: okhttp3.MultipartBody.Part): Response<ApiResponse<User>>

    @Multipart
    @PUT("api/user/update/banner-image")
    suspend fun updateBannerImage(@Part file: okhttp3.MultipartBody.Part): Response<ApiResponse<User>>

    @PUT("api/user/update/bio")
    suspend fun updateBio(@Body request: Map<String, String>): Response<ApiResponse<User>>

    @PUT("api/user/update/password")
    suspend fun updatePassword(@Body request: Map<String, String>): Response<ApiResponse<User>>

    @DELETE("api/user/delete")
    suspend fun deleteAccount(): Response<ApiResponse<Unit>>

    @HTTP(method = "DELETE", path = "api/user/delete", hasBody = true)
    suspend fun deleteAccountWithBody(@Body request: Map<String, String>): Response<ApiResponse<Unit>>

    @POST("api/user/favorites/{animeId}")
    suspend fun toggleFavorite(@Path("animeId") animeId: String): Response<ApiResponse<UserFavorite>>

    @POST("api/user/progress")
    suspend fun updateProgress(@Body request: UpdateProgressRequest): Response<ApiResponse<Unit>>

    // Chat
    @GET("api/chat/conversations")
    suspend fun getConversations(): Response<ApiResponse<List<Conversation>>>

    @GET("api/chat/conversation/{convId}/messages")
    suspend fun getConversationMessages(
        @Path("convId") convId: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50
    ): Response<ApiResponse<List<Message>>>

    @POST("api/chat/conversation/{convId}/send")
    suspend fun sendMessage(
        @Path("convId") convId: String,
        @Body request: SendMessageRequest
    ): Response<ApiResponse<Message>>

    @POST("api/chat/conversation/{convId}/read")
    suspend fun markConversationRead(@Path("convId") convId: String): Response<ApiResponse<Unit>>

    @GET("api/chat/friends")
    suspend fun getFriends(): Response<ApiResponse<List<User>>>

    @GET("api/chat/friend/requests")
    suspend fun getFriendRequests(): Response<ApiResponse<List<FriendRequest>>>

    @POST("api/chat/friend/request")
    suspend fun sendFriendRequest(@Body request: Map<String, String>): Response<ApiResponse<FriendRequest>>

    @POST("api/chat/friend/request/respond")
    suspend fun respondFriendRequest(@Body request: FriendRequestAction): Response<ApiResponse<FriendRequest>>

    @GET("api/chat/search/friend")
    suspend fun searchUsers(
        @Query("q") query: String
    ): Response<ApiResponse<List<SearchUsersResponse>>>

    @POST("api/chat/conversation/user/{userId}")
    suspend fun getOrCreateConversation(@Path("userId") userId: String): Response<ApiResponse<Conversation>>

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

    @GET("api/clans/me")
    suspend fun getMyClan(): Response<ApiResponse<Clan>>

    @GET("api/clans/leaderboard")
    suspend fun getClanLeaderboard(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<Clan>>>

    @GET("api/clans/upgrade/catalog")
    suspend fun getUpgradeCatalog(): Response<ApiResponse<List<ClanUpgrade>>>

    @GET("api/clans/{id}/members")
    suspend fun getClanMembers(@Path("id") id: String): Response<ApiResponse<List<ClanMember>>>

    @POST("api/clans/{id}/join")
    suspend fun joinClan(@Path("id") id: String): Response<ApiResponse<Unit>>

    @POST("api/clans/{id}/kick/{userId}")
    suspend fun kickMember(
        @Path("id") clanId: String,
        @Path("userId") userId: String
    ): Response<ApiResponse<Unit>>

    @POST("api/clans/{id}/role/{userId}")
    suspend fun updateMemberRole(
        @Path("id") clanId: String,
        @Path("userId") userId: String,
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

    @POST("api/clans/{id}/upgrade/purchase")
    suspend fun purchaseUpgrade(
        @Path("id") id: String,
        @Body request: PurchaseUpgradeRequest
    ): Response<ApiResponse<ClanUpgrade>>

    // Giveaways
    @GET("api/giveaway")
    suspend fun getGiveaways(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<Giveaway>>>

    @GET("api/giveaway/{id}")
    suspend fun getGiveawayDetail(@Path("id") id: String): Response<ApiResponse<Giveaway>>

    @POST("api/giveaway")
    suspend fun createGiveaway(@Body request: CreateGiveawayRequest): Response<ApiResponse<Giveaway>>

    @POST("api/giveaway/{id}/claim")
    suspend fun claimGiveaway(@Path("id") id: String): Response<ApiResponse<Unit>>

    @GET("api/giveaway/top-givers")
    suspend fun getTopGivers(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<TopGiver>>>

    // Notifications
    @GET("api/notification/list")
    suspend fun getNotifications(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<Notification>>>

    @GET("api/notification/unread-count")
    suspend fun getUnreadCount(): Response<ApiResponse<Int>>

    @POST("api/notification/mark-read")
    suspend fun markNotificationRead(@Body request: Map<String, String>): Response<ApiResponse<Unit>>

    @POST("api/notification/mark-read")
    suspend fun markAllNotificationsRead(): Response<ApiResponse<Unit>>

    @POST("api/notification/token/upsert")
    suspend fun upsertNotificationToken(@Body request: NotificationToken): Response<ApiResponse<Unit>>

    @DELETE("api/notification/token/delete")
    suspend fun deleteNotificationToken(): Response<ApiResponse<Unit>>

    // Premium
    @POST("api/premium/gift")
    suspend fun giftPremium(@Body request: GiftPremiumRequest): Response<ApiResponse<PremiumSubscription>>

    @POST("api/premium/update")
    suspend fun updatePremium(@Body request: Map<String, String>): Response<ApiResponse<PremiumSubscription>>

    // Banners (public - needs to be added to backend)
    @GET("api/banners")
    suspend fun getBanners(): Response<ApiResponse<List<Banner>>>

    // Leaderboard & XP
    @GET("api/leaderboard")
    suspend fun getLeaderboard(
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<LeaderboardUser>>>

    @POST("api/xp/grant")
    suspend fun grantXp(@Body request: XpGrantRequest): Response<ApiResponse<XpGrantResponse>>

    // Most Watched
    @GET("api/episodes/most-watched")
    suspend fun getMostWatched(
        @Query("limit") limit: Int = 10
    ): Response<ApiResponse<List<MostWatchedEpisode>>>

    // Continue Watching
    @GET("api/user/profile/continue-watching")
    suspend fun getContinueWatching(): Response<ApiResponse<List<ContinueWatchingItem>>>
}