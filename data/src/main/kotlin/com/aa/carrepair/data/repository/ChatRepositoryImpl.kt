package com.aa.carrepair.data.repository

import com.aa.carrepair.contracts.api.AgentChatRequest
import com.aa.carrepair.contracts.api.MessageContext
import com.aa.carrepair.core.result.DataResult
import com.aa.carrepair.core.result.safeApiCall
import com.aa.carrepair.data.local.dao.ChatDao
import com.aa.carrepair.data.local.entity.ChatMessageEntity
import com.aa.carrepair.data.remote.api.AgentApi
import com.aa.carrepair.domain.model.AgentResponse
import com.aa.carrepair.domain.model.AgentType
import com.aa.carrepair.domain.model.ChatMessage
import com.aa.carrepair.domain.model.MessageRole
import com.aa.carrepair.domain.model.SafetyLevel
import com.aa.carrepair.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val chatDao: ChatDao,
    private val agentApi: AgentApi
) : ChatRepository {

    override fun getChatSession(sessionId: String): Flow<List<ChatMessage>> =
        chatDao.getBySession(sessionId).map { entities -> entities.map { it.toDomain() } }

    override fun getAllSessions(): Flow<List<String>> = chatDao.getAllSessionIds()

    override suspend fun sendMessage(
        sessionId: String,
        message: String,
        vehicleVin: String?
    ): DataResult<AgentResponse> {
        val recentMessages = chatDao.getRecentMessages(sessionId, 10)
        val context = recentMessages.map { MessageContext(it.role, it.content) }

        return safeApiCall {
            val response = agentApi.chat(
                AgentChatRequest(
                    sessionId = sessionId,
                    message = message,
                    context = context,
                    vehicleVin = vehicleVin
                )
            )
            AgentResponse(
                content = response.content,
                agentType = AgentType.values().firstOrNull {
                    it.name.equals(response.agentType, ignoreCase = true)
                } ?: AgentType.GENERAL,
                confidence = response.confidence,
                safetyAssessment = null,
                suggestedActions = response.suggestedActions
            )
        }
    }

    override suspend fun saveMessage(message: ChatMessage): DataResult<Unit> =
        safeApiCall { chatDao.insert(message.toEntity()) }

    override suspend fun deleteSession(sessionId: String): DataResult<Unit> =
        safeApiCall { chatDao.deleteSession(sessionId) }

    override suspend fun summarizeSession(sessionId: String): DataResult<String> {
        val messages = chatDao.getRecentMessages(sessionId, 50)
        val summary = messages.joinToString("\n") { "[${it.role}]: ${it.content}" }
        return DataResult.Success(summary.take(500))
    }

    private fun ChatMessageEntity.toDomain() = ChatMessage(
        id = id,
        sessionId = sessionId,
        content = content,
        role = MessageRole.valueOf(role),
        agentType = AgentType.values().firstOrNull { it.name == agentType } ?: AgentType.GENERAL,
        timestamp = timestamp,
        confidence = confidence,
        safetyLevel = safetyLevel?.let { SafetyLevel.valueOf(it) },
        attachmentUri = attachmentUri
    )

    private fun ChatMessage.toEntity() = ChatMessageEntity(
        id = id,
        sessionId = sessionId,
        content = content,
        role = role.name,
        agentType = agentType.name,
        timestamp = timestamp,
        confidence = confidence,
        safetyLevel = safetyLevel?.name,
        attachmentUri = attachmentUri
    )
}
