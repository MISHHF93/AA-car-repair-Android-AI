package com.aa.carrepair.domain.usecase.chat

import com.aa.carrepair.core.result.DataResult
import com.aa.carrepair.domain.model.AgentResponse
import com.aa.carrepair.domain.model.ChatMessage
import com.aa.carrepair.domain.model.MessageRole
import com.aa.carrepair.domain.repository.ChatRepository
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(
        sessionId: String,
        userMessage: String,
        vehicleVin: String? = null
    ): DataResult<AgentResponse> {
        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            sessionId = sessionId,
            content = userMessage,
            role = MessageRole.USER,
            timestamp = Instant.now()
        )
        chatRepository.saveMessage(userMsg)
        return chatRepository.sendMessage(sessionId, userMessage, vehicleVin)
    }
}
