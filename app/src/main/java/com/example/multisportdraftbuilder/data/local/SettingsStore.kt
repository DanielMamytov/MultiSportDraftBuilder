package com.example.multisportdraftbuilder.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "draft_builder_settings")

class SettingsStore(private val context: Context) {

    private val darkThemeKey = booleanPreferencesKey("dark_theme")
    private val notificationsKey = booleanPreferencesKey("notifications_enabled")
    private val onboardingCompletedKey = booleanPreferencesKey("onboarding_completed")

    val darkThemeEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[darkThemeKey] ?: true
    }

    val notificationsEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[notificationsKey] ?: true
    }

    val onboardingCompleted: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[onboardingCompletedKey] ?: false
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { preferences -> preferences[darkThemeKey] = enabled }
    }

    suspend fun setNotifications(enabled: Boolean) {
        context.dataStore.edit { preferences -> preferences[notificationsKey] = enabled }
    }

    suspend fun reset() {
        context.dataStore.edit { emptyPreferences() }
    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences -> preferences[onboardingCompletedKey] = completed }
    }
}
