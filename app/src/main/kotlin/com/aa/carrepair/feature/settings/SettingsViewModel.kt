package com.aa.carrepair.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aa.carrepair.core.preferences.UserPreferencesManager
import com.aa.carrepair.core.privacy.PrivacyManager
import com.aa.carrepair.domain.model.UserPersona
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val privacyModeEnabled: Boolean = false,
    val analyticsEnabled: Boolean = true,
    val theme: String = "system",
    val units: String = "imperial",
    val selectedPersonaDisplayName: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val privacyManager: PrivacyManager,
    private val userPreferencesManager: UserPreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        privacyManager.isPrivacyModeEnabled
            .onEach { enabled -> _uiState.update { it.copy(privacyModeEnabled = enabled) } }
            .launchIn(viewModelScope)

        privacyManager.isAnalyticsEnabled
            .onEach { enabled -> _uiState.update { it.copy(analyticsEnabled = enabled) } }
            .launchIn(viewModelScope)

        userPreferencesManager.selectedPersonaName
            .onEach { name ->
                val displayName = name?.let {
                    try { UserPersona.valueOf(it).displayName } catch (_: IllegalArgumentException) { it }
                }
                _uiState.update { it.copy(selectedPersonaDisplayName = displayName) }
            }
            .launchIn(viewModelScope)
    }

    fun setPrivacyMode(enabled: Boolean) {
        viewModelScope.launch {
            privacyManager.setPrivacyMode(enabled)
        }
    }

    /** Clears the onboarding state so the persona-selection screen is shown on next launch. */
    fun resetPersona(onComplete: () -> Unit) {
        viewModelScope.launch {
            userPreferencesManager.resetOnboarding()
            onComplete()
        }
    }
}
