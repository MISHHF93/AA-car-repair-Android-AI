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

/**
 * Manages persistent user preferences for onboarding state and persona selection.
 */
@Singleton
class UserPreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val onboardingCompletedKey = booleanPreferencesKey("onboarding_completed")
    private val selectedPersonaKey = stringPreferencesKey("selected_persona")

    /** Emits true once the user has completed the onboarding / persona-selection flow. */
    val hasCompletedOnboarding: Flow<Boolean> = context.userPrefsDataStore.data
        .map { prefs -> prefs[onboardingCompletedKey] ?: false }

    /** Emits the raw persona name string chosen during onboarding, or null if not yet set. */
    val selectedPersonaName: Flow<String?> = context.userPrefsDataStore.data
        .map { prefs -> prefs[selectedPersonaKey] }

    /**
     * Marks onboarding as complete and persists the selected persona.
     * @param personaName the `UserPersona.name` string to persist
     */
    suspend fun completeOnboarding(personaName: String) {
        context.userPrefsDataStore.edit { prefs ->
            prefs[onboardingCompletedKey] = true
            prefs[selectedPersonaKey] = personaName
        }
        Timber.d("Onboarding completed with persona: %s", personaName)
    }

    /** Resets onboarding state (useful for settings → reset or testing). */
    suspend fun resetOnboarding() {
        context.userPrefsDataStore.edit { prefs ->
            prefs.remove(onboardingCompletedKey)
            prefs.remove(selectedPersonaKey)
        }
        Timber.d("Onboarding state reset")
    }
}
