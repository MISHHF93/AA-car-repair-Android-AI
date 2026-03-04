package com.aa.carrepair.feature.fleet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aa.carrepair.core.result.DataResult
import com.aa.carrepair.domain.model.FleetVehicleStatus
import com.aa.carrepair.domain.repository.FleetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class FleetViewModel @Inject constructor(
    private val fleetRepository: FleetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FleetUiState())
    val uiState: StateFlow<FleetUiState> = _uiState.asStateFlow()

    init {
        loadFleet()
    }

    private fun loadFleet() {
        _uiState.update { it.copy(isLoading = true) }
        fleetRepository.getFleetVehicles()
            .onEach { vehicles ->
                val totalCost = vehicles.sumOf { it.totalCostYtd }
                val avgCost = if (vehicles.isNotEmpty()) totalCost / vehicles.size else 0.0
                val maintenanceDue = vehicles.count { vehicle ->
                    vehicle.nextMaintenanceDue?.isBefore(Instant.now()) == true ||
                        vehicle.status == FleetVehicleStatus.IN_MAINTENANCE
                }
                _uiState.update {
                    it.copy(
                        vehicles = vehicles,
                        totalCostYtd = totalCost,
                        avgCostPerVehicle = avgCost,
                        maintenanceDueCount = maintenanceDue,
                        isLoading = false
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun refreshFleet() {
        loadFleet()
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
