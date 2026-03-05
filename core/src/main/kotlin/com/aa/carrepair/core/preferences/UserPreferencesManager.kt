package com.aa.carrepair.core.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

private val Context.userPrefsDataStore: DataStore<Preferences> by preferencesDataStore("user_prefs")

@Singleton
class UserPreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val onboardingCompletedKey = booleanPreferencesKey("onboarding_completed")
    private val selectedPersonaKey = stringPreferencesKey("selected_persona")
    private val userDisplayNameKey = stringPreferencesKey("user_display_name")
    private val userEmailKey = stringPreferencesKey("user_email")
    private val authProviderKey = stringPreferencesKey("auth_provider")
    private val themeKey = stringPreferencesKey("theme")
    private val unitsKey = stringPreferencesKey("units")
    private val notificationsEnabledKey = booleanPreferencesKey("notifications_enabled")

    val hasCompletedOnboarding: Flow<Boolean> = context.userPrefsDataStore.data
        .map { prefs -> prefs[onboardingCompletedKey] ?: false }

    val selectedPersonaName: Flow<String?> = context.userPrefsDataStore.data
        .map { prefs -> prefs[selectedPersonaKey] }

    val userDisplayName: Flow<String?> = context.userPrefsDataStore.data
        .map { prefs -> prefs[userDisplayNameKey] }

    val userEmail: Flow<String?> = context.userPrefsDataStore.data
        .map { prefs -> prefs[userEmailKey] }

    val authProvider: Flow<String?> = context.userPrefsDataStore.data
        .map { prefs -> prefs[authProviderKey] }

    val theme: Flow<String> = context.userPrefsDataStore.data
        .map { prefs -> prefs[themeKey] ?: "system" }

    val units: Flow<String> = context.userPrefsDataStore.data
        .map { prefs -> prefs[unitsKey] ?: "imperial" }

    val notificationsEnabled: Flow<Boolean> = context.userPrefsDataStore.data
        .map { prefs -> prefs[notificationsEnabledKey] ?: true }

    suspend fun completeOnboarding(personaName: String) {
        context.userPrefsDataStore.edit { prefs ->
            prefs[onboardingCompletedKey] = true
            prefs[selectedPersonaKey] = personaName
        }
        Timber.d("Onboarding completed with persona: %s", personaName)
    }

    suspend fun saveUserProfile(displayName: String, email: String, provider: String) {
        context.userPrefsDataStore.edit { prefs ->
            prefs[userDisplayNameKey] = displayName
            prefs[userEmailKey] = email
            prefs[authProviderKey] = provider
        }
        Timber.d("User profile saved: %s (%s)", displayName, provider)
    }

    suspend fun setTheme(theme: String) {
        context.userPrefsDataStore.edit { prefs -> prefs[themeKey] = theme }
    }

    suspend fun setUnits(units: String) {
        context.userPrefsDataStore.edit { prefs -> prefs[unitsKey] = units }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.userPrefsDataStore.edit { prefs -> prefs[notificationsEnabledKey] = enabled }
    }

    suspend fun resetOnboarding() {
        context.userPrefsDataStore.edit { prefs ->
            prefs.remove(onboardingCompletedKey)
            prefs.remove(selectedPersonaKey)
        }
        Timber.d("Onboarding state reset")
    }

    suspend fun signOut() {
        context.userPrefsDataStore.edit { prefs -> prefs.clear() }
        Timber.d("User signed out — all preferences cleared")
    }
}
