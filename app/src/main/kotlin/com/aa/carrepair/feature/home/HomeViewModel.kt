package com.aa.carrepair.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aa.carrepair.core.preferences.UserPreferencesManager
import com.aa.carrepair.domain.model.UserPersona
import com.aa.carrepair.domain.repository.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class HomeUiState(
    val persona: UserPersona = UserPersona.DIY_OWNER,
    val recentVehicleCount: Int = 0,
    val isLoading: Boolean = false,
    val userDisplayName: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository,
    private val userPreferencesManager: UserPreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        vehicleRepository.getVehicles()
            .onEach { vehicles ->
                _uiState.update { it.copy(recentVehicleCount = vehicles.size) }
            }
            .launchIn(viewModelScope)

        userPreferencesManager.userDisplayName
            .onEach { name -> _uiState.update { it.copy(userDisplayName = name) } }
            .launchIn(viewModelScope)

        userPreferencesManager.selectedPersonaName
            .onEach { name ->
                val persona = name?.let {
                    try { UserPersona.valueOf(it) } catch (_: IllegalArgumentException) { null }
                } ?: UserPersona.DIY_OWNER
                _uiState.update { it.copy(persona = persona) }
            }
            .launchIn(viewModelScope)
    }

    fun setPersona(persona: UserPersona) {
        _uiState.update { it.copy(persona = persona) }
    }
}
