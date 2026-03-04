package com.aa.carrepair.domain.usecase.vehicle

import com.aa.carrepair.core.result.DataResult
import com.aa.carrepair.core.util.isValidVin
import com.aa.carrepair.domain.model.Vehicle
import com.aa.carrepair.domain.repository.VehicleRepository
import javax.inject.Inject

class DecodeVinUseCase @Inject constructor(
    private val vehicleRepository: VehicleRepository
) {
    suspend operator fun invoke(vin: String): DataResult<Vehicle> {
        if (!vin.isValidVin()) {
            return DataResult.Error(IllegalArgumentException("Invalid VIN: $vin"))
        }
        return vehicleRepository.decodeVin(vin.uppercase())
    }
}
