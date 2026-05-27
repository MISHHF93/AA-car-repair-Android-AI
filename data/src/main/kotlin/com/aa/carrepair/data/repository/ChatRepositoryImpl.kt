package com.aa.carrepair.data.repository

import com.aa.carrepair.contracts.api.AgentChatRequest
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
import java.util.UUID
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
        return safeApiCall {
            val requestId = UUID.randomUUID().toString()
            val response = agentApi.chat(
                AgentChatRequest(
                    requestId = requestId,
                    timestampUtc = Instant.now().toString(),
                    surface = "mobile",
                    userRole = "consumer",
                    locale = "en-CA",
                    queryText = message,
                    policyProfile = "mobile_default",
                    privacyMode = "standard"
                )
            )

            val answerText = buildString {
                append("answer_text: ")
                append(response.answerText)
                append("\nconfidence: ").append(response.confidence)
                append("\nsafety_level: ").append(response.safetyLevel)
                append("\ncitations:")
                if (response.citations.isEmpty()) {
                    append(" None")
                } else {
                    response.citations.forEach { append("\n- ").append(it) }
                }
                append("\naudit_trace_id: ").append(response.auditTraceId)
            }.trim()

            chatDao.insert(
                ChatMessageEntity(
                    id = response.responseId.ifBlank { UUID.randomUUID().toString() },
                    sessionId = sessionId,
                    content = answerText,
                    role = MessageRole.ASSISTANT.name,
                    agentType = AgentType.DIAGNOSIS.name,
                    timestamp = Instant.now(),
                    confidence = response.confidence,
                    safetyLevel = response.safetyLevel,
                    attachmentUri = null
                )
            )

            AgentResponse(
                content = response.answerText,
                agentType = AgentType.DIAGNOSIS,
                confidence = response.confidence,
                safetyAssessment = null,
                suggestedActions = response.nextActions,
                metadata = mapOf(
                    "safety_level" to response.safetyLevel,
                    "citations" to response.citations.joinToString(" | "),
                    "audit_trace_id" to response.auditTraceId
                )
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
