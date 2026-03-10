package com.example.multisportdraftbuilder.data.local

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "draft_builder_settings")

class SettingsStore(private val context: Context) {

    private val notificationsKey = booleanPreferencesKey("notifications_enabled")
    private val onboardingCompletedKey = booleanPreferencesKey("onboarding_completed")


    val notificationsEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[notificationsKey] ?: true
    }

    val onboardingCompleted: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[onboardingCompletedKey] ?: false
    }


    suspend fun setNotifications(enabled: Boolean) {
        context.dataStore.edit { preferences -> preferences[notificationsKey] = enabled }
    }

    suspend fun resetSettings() {
        context.dataStore.edit { preferences ->
            preferences.remove(notificationsKey)
        }
    }
    suspend fun reset() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences -> preferences[onboardingCompletedKey] = completed }
    }

}
