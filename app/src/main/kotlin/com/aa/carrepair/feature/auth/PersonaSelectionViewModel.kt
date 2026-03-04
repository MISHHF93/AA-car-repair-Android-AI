package com.aa.carrepair.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aa.carrepair.core.preferences.UserPreferencesManager
import com.aa.carrepair.domain.model.UserPersona
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the persona-selection step of onboarding. Persists the chosen persona and
 * marks onboarding as complete so the app starts at Home on subsequent launches.
 */
@HiltViewModel
class PersonaSelectionViewModel @Inject constructor(
    private val userPreferencesManager: UserPreferencesManager
) : ViewModel() {

    fun selectPersona(persona: UserPersona, onComplete: () -> Unit) {
        viewModelScope.launch {
            userPreferencesManager.completeOnboarding(persona.name)
            onComplete()
        }
    }
}
