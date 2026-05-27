package com.aa.carrepair.data.remote.api

import com.aa.carrepair.contracts.api.AgentChatRequest
import com.aa.carrepair.contracts.api.AgentChatResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AgentApi {
    @POST("v1/chat")
    suspend fun chat(@Body request: AgentChatRequest): AgentChatResponse
}
