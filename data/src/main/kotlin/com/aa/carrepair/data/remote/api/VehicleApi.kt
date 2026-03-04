package com.aa.carrepair.data.remote.api

import com.aa.carrepair.contracts.api.VinDecodeResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface VehicleApi {
    @GET("v1/vehicle/vin/{vin}")
    suspend fun decodeVin(@Path("vin") vin: String): VinDecodeResponse
}
