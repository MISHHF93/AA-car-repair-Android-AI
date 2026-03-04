package com.aa.carrepair.domain.usecase.vehicle

import com.aa.carrepair.core.result.DataResult
import com.aa.carrepair.domain.model.Vehicle
import com.aa.carrepair.domain.repository.VehicleRepository
import javax.inject.Inject

class SaveVehicleUseCase @Inject constructor(
    private val vehicleRepository: VehicleRepository
) {
    suspend operator fun invoke(vehicle: Vehicle): DataResult<Unit> =
        vehicleRepository.saveVehicle(vehicle)
}
