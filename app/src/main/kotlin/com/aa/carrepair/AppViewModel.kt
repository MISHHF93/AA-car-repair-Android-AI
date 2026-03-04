package com.aa.carrepair

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aa.carrepair.core.preferences.UserPreferencesManager
import com.aa.carrepair.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * Root ViewModel that determines the navigation start destination based on whether the user has
 * completed the onboarding flow. A `null` value means the preference is still loading.
 */
@HiltViewModel
class AppViewModel @Inject constructor(
    userPreferencesManager: UserPreferencesManager
) : ViewModel() {

    /**
     * Emits the starting navigation route once the onboarding preference is read from DataStore.
     * Null while loading (splash screen should be kept visible during this time).
     */
    val startDestination: StateFlow<String?> = userPreferencesManager.hasCompletedOnboarding
        .map { completed ->
            if (completed) Screen.Home.route else Screen.SignIn.route
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )
}
