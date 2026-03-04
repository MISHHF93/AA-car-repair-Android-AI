package com.aa.carrepair.data.remote.api

import com.aa.carrepair.contracts.api.DtcAnalysisResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DtcApi {
    @GET("v1/dtc/{code}")
    suspend fun analyzeDtc(
        @Path("code") code: String,
        @Query("vin") vehicleVin: String? = null
    ): DtcAnalysisResponse
}
