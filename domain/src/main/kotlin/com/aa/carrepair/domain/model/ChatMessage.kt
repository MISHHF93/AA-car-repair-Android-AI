package com.aa.carrepair.domain.model

import java.time.Instant

data class ChatMessage(
    val id: String,
    val sessionId: String,
    val content: String,
    val role: MessageRole,
    val agentType: AgentType = AgentType.GENERAL,
    val timestamp: Instant = Instant.now(),
    val confidence: Int? = null,
    val safetyLevel: SafetyLevel? = null,
    val attachmentUri: String? = null
)

enum class MessageRole {
    USER,
    ASSISTANT,
    SYSTEM
}

enum class AgentType {
    GENERAL,
    DIAGNOSIS,
    ESTIMATOR,
    SAFETY
}
