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
    val notificationsEnabled: Boolean = true,
    val selectedPersonaDisplayName: String? = null,
    val userDisplayName: String? = null,
    val userEmail: String? = null,
    val showDeleteConfirmation: Boolean = false
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

        userPreferencesManager.userDisplayName
            .onEach { name -> _uiState.update { it.copy(userDisplayName = name) } }
            .launchIn(viewModelScope)

        userPreferencesManager.userEmail
            .onEach { email -> _uiState.update { it.copy(userEmail = email) } }
            .launchIn(viewModelScope)

        userPreferencesManager.theme
            .onEach { theme -> _uiState.update { it.copy(theme = theme) } }
            .launchIn(viewModelScope)

        userPreferencesManager.units
            .onEach { units -> _uiState.update { it.copy(units = units) } }
            .launchIn(viewModelScope)

        userPreferencesManager.notificationsEnabled
            .onEach { enabled -> _uiState.update { it.copy(notificationsEnabled = enabled) } }
            .launchIn(viewModelScope)
    }

    fun setPrivacyMode(enabled: Boolean) {
        viewModelScope.launch {
            privacyManager.setPrivacyMode(enabled)
        }
    }

    fun setTheme(theme: String) {
        viewModelScope.launch {
            userPreferencesManager.setTheme(theme)
        }
    }

    fun setUnits(units: String) {
        viewModelScope.launch {
            userPreferencesManager.setUnits(units)
        }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesManager.setNotificationsEnabled(enabled)
        }
    }

    fun showDeleteConfirmation() {
        _uiState.update { it.copy(showDeleteConfirmation = true) }
    }

    fun dismissDeleteConfirmation() {
        _uiState.update { it.copy(showDeleteConfirmation = false) }
    }

    fun deleteAllData(onComplete: () -> Unit) {
        viewModelScope.launch {
            privacyManager.deleteAllUserData {
                userPreferencesManager.signOut()
            }
            _uiState.update { it.copy(showDeleteConfirmation = false) }
            onComplete()
        }
    }

    fun resetPersona(onComplete: () -> Unit) {
        viewModelScope.launch {
            userPreferencesManager.resetOnboarding()
            onComplete()
        }
    }

    fun signOut(onComplete: () -> Unit) {
        viewModelScope.launch {
            userPreferencesManager.signOut()
            onComplete()
        }
    }
}
