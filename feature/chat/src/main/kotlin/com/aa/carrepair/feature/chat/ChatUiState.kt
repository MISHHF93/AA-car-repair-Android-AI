package com.aa.carrepair.feature.chat

import com.aa.carrepair.domain.model.AgentType
import com.aa.carrepair.domain.model.ChatMessage

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val isTyping: Boolean = false,
    val currentAgentType: AgentType = AgentType.GENERAL,
    val inputText: String = "",
    val error: String? = null,
    val sessionId: String = ""
)
