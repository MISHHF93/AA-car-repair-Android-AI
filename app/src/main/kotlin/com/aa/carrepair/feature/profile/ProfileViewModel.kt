package com.aa.carrepair.feature.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aa.carrepair.core.preferences.UserPreferencesManager
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

data class ProfileUiState(
    val displayName: String = "Guest",
    val email: String = "Not signed in",
    val authProvider: String? = null,
    val persona: String? = null
) {
    val initials: String
        get() {
            val parts = displayName.split(" ").filter { it.isNotBlank() }
            return when {
                parts.size >= 2 -> "${parts[0].first().uppercase()}${parts[1].first().uppercase()}"
                parts.size == 1 -> parts[0].take(2).uppercase()
                else -> "AA"
            }
        }
    val authProviderDisplay: String
        get() = when (authProvider) {
            "google" -> "Google"
            "email" -> "Email"
            else -> "Guest"
        }
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userPreferencesManager: UserPreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        userPreferencesManager.userDisplayName
            .onEach { name -> _uiState.update { it.copy(displayName = name ?: "Guest") } }
            .launchIn(viewModelScope)

        userPreferencesManager.userEmail
            .onEach { email -> _uiState.update { it.copy(email = email ?: "Not signed in") } }
            .launchIn(viewModelScope)

        userPreferencesManager.authProvider
            .onEach { provider -> _uiState.update { it.copy(authProvider = provider) } }
            .launchIn(viewModelScope)

        userPreferencesManager.selectedPersonaName
            .onEach { name ->
                val displayName = name?.let {
                    try { UserPersona.valueOf(it).displayName } catch (_: IllegalArgumentException) { it }
                }
                _uiState.update { it.copy(persona = displayName) }
            }
            .launchIn(viewModelScope)
    }

    fun signOut(onComplete: () -> Unit) {
        viewModelScope.launch {
            userPreferencesManager.signOut()
            onComplete()
        }
    }
}
