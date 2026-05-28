package com.anix.app.core.di

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesKeys {
    val AUTH_TOKEN = stringPreferencesKey("auth_token")
    val DARK_MODE = booleanPreferencesKey("dark_mode")
}
