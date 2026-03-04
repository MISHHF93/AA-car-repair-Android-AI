package com.aa.carrepair.domain.repository

import com.aa.carrepair.core.result.DataResult
import com.aa.carrepair.domain.model.AgentResponse
import com.aa.carrepair.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getChatSession(sessionId: String): Flow<List<ChatMessage>>
    fun getAllSessions(): Flow<List<String>>
    suspend fun sendMessage(sessionId: String, message: String, vehicleVin: String? = null): DataResult<AgentResponse>
    suspend fun saveMessage(message: ChatMessage): DataResult<Unit>
    suspend fun deleteSession(sessionId: String): DataResult<Unit>
    suspend fun summarizeSession(sessionId: String): DataResult<String>
}
