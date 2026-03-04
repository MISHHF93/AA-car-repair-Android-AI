package com.aa.carrepair.data.remote.api

import com.aa.carrepair.contracts.api.AgentChatRequest
import com.aa.carrepair.contracts.api.AgentChatResponse
import com.aa.carrepair.contracts.api.AgentDiagnoseRequest
import com.aa.carrepair.contracts.api.AgentDiagnoseResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AgentApi {
    @POST("v1/agent/chat")
    suspend fun chat(@Body request: AgentChatRequest): AgentChatResponse

    @POST("v1/agent/diagnose")
    suspend fun diagnose(@Body request: AgentDiagnoseRequest): AgentDiagnoseResponse
}
