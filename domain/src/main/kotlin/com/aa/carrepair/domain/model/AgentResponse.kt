package com.aa.carrepair.domain.model

data class AgentResponse(
    val content: String,
    val agentType: AgentType,
    val confidence: Int,
    val safetyAssessment: SafetyClassification?,
    val suggestedActions: List<String> = emptyList(),
    val metadata: Map<String, String> = emptyMap()
)
