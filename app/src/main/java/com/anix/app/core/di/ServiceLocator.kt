package com.anix.app.core.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.anix.app.core.network.ApiClient
import com.anix.app.core.network.ApiService
import com.anix.app.data.repositories.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "anix_preferences")

object ServiceLocator {

    private var context: Context? = null
    private var _apiService: ApiService? = null
    private var _animeRepository: AnimeRepository? = null
    private var _authRepository: AuthRepository? = null
    private var _chatRepository: ChatRepository? = null
    private var _clanRepository: ClanRepository? = null
    private var _giveawayRepository: GiveawayRepository? = null
    private var _notificationRepository: NotificationRepository? = null
    private var _socialRepository: SocialRepository? = null
    private var _userRepository: UserRepository? = null

    fun init(ctx: Context) {
        context = ctx.applicationContext
        _apiService = ApiClient.getApiService(ctx)
        _animeRepository = AnimeRepository(getApiService())
        _authRepository = AuthRepository(getApiService(), ctx)
        _chatRepository = ChatRepository(getApiService())
        _clanRepository = ClanRepository(getApiService())
        _giveawayRepository = GiveawayRepository(getApiService())
        _notificationRepository = NotificationRepository(getApiService())
        _socialRepository = SocialRepository(getApiService())
        _userRepository = UserRepository(getApiService())
    }

    fun getApiService(): ApiService = _apiService!!
    fun getAnimeRepository(): AnimeRepository = _animeRepository!!
    fun getAuthRepository(): AuthRepository = _authRepository!!
    fun getChatRepository(): ChatRepository = _chatRepository!!
    fun getClanRepository(): ClanRepository = _clanRepository!!
    fun getGiveawayRepository(): GiveawayRepository = _giveawayRepository!!
    fun getNotificationRepository(): NotificationRepository = _notificationRepository!!
    fun getSocialRepository(): SocialRepository = _socialRepository!!
    fun getUserRepository(): UserRepository = _userRepository!!

    fun getDataStore(): DataStore<Preferences> {
        return context!!.dataStore
    }

    fun getToken(): String? {
        return runBlocking {
            context?.let {
                it.dataStore.data.first()[PreferencesKeys.AUTH_TOKEN]
            }
        }
    }

    fun saveToken(token: String) {
        runBlocking {
            context?.let {
                it.dataStore.edit { preferences ->
                    preferences[PreferencesKeys.AUTH_TOKEN] = token
                }
            }
        }
    }

    fun clearToken() {
        runBlocking {
            context?.let {
                it.dataStore.edit { preferences ->
                    preferences.remove(PreferencesKeys.AUTH_TOKEN)
                }
            }
        }
    }
}
