package com.aa.carrepair.data.remote.api

import com.aa.carrepair.contracts.api.InspectionResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface InspectionApi {
    @Multipart
    @POST("v1/inspection/analyze")
    suspend fun analyzeImage(
        @Part image: MultipartBody.Part,
        @Part("mode") mode: RequestBody,
        @Part("vehicle_vin") vehicleVin: RequestBody?
    ): InspectionResponse
}
