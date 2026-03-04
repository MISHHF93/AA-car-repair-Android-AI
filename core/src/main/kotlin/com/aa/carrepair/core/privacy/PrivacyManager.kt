package com.aa.carrepair.core.privacy

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

private val Context.privacyDataStore: DataStore<Preferences> by preferencesDataStore("privacy_prefs")

@Singleton
class PrivacyManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val privacyModeKey = booleanPreferencesKey("privacy_mode_enabled")
    private val analyticsEnabledKey = booleanPreferencesKey("analytics_enabled")
    private val telemetryEnabledKey = booleanPreferencesKey("telemetry_enabled")

    val isPrivacyModeEnabled: Flow<Boolean> = context.privacyDataStore.data
        .map { prefs -> prefs[privacyModeKey] ?: false }

    val isAnalyticsEnabled: Flow<Boolean> = context.privacyDataStore.data
        .map { prefs -> prefs[analyticsEnabledKey] ?: true }

    val isTelemetryEnabled: Flow<Boolean> = context.privacyDataStore.data
        .map { prefs -> prefs[telemetryEnabledKey] ?: true }

    suspend fun setPrivacyMode(enabled: Boolean) {
        context.privacyDataStore.edit { prefs ->
            prefs[privacyModeKey] = enabled
            if (enabled) {
                prefs[analyticsEnabledKey] = false
                prefs[telemetryEnabledKey] = false
            }
        }
        Timber.d("Privacy mode set to: %b", enabled)
    }

    suspend fun setAnalyticsEnabled(enabled: Boolean) {
        context.privacyDataStore.edit { prefs ->
            prefs[analyticsEnabledKey] = enabled
        }
    }

    suspend fun deleteAllUserData(onComplete: suspend () -> Unit) {
        context.privacyDataStore.edit { prefs -> prefs.clear() }
        onComplete()
        Timber.d("All user data deleted")
    }

    fun exportDataDescription(): String =
        "Your data includes: vehicle records, chat history, repair estimates, calculator results, and fleet data."
}
