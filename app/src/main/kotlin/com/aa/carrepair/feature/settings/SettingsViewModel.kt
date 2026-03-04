package com.aa.carrepair.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aa.carrepair.core.privacy.PrivacyManager
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
    val units: String = "imperial"
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val privacyManager: PrivacyManager
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
    }

    fun setPrivacyMode(enabled: Boolean) {
        viewModelScope.launch {
            privacyManager.setPrivacyMode(enabled)
        }
    }
}
