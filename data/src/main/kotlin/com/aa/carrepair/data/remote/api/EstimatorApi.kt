package com.aa.carrepair.data.remote.api

import com.aa.carrepair.contracts.api.EstimateRequest
import com.aa.carrepair.contracts.api.EstimateResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface EstimatorApi {
    @POST("v1/agent/estimate")
    suspend fun generateEstimate(@Body request: EstimateRequest): EstimateResponse
}
