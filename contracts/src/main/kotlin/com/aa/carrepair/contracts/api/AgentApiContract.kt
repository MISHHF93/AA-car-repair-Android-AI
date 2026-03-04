package com.aa.carrepair.contracts.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AgentChatRequest(
    @Json(name = "session_id") val sessionId: String,
    @Json(name = "message") val message: String,
    @Json(name = "agent_type") val agentType: String? = null,
    @Json(name = "persona") val persona: String? = null,
    @Json(name = "context") val context: List<MessageContext> = emptyList(),
    @Json(name = "vehicle_vin") val vehicleVin: String? = null
)

@JsonClass(generateAdapter = true)
data class MessageContext(
    @Json(name = "role") val role: String,
    @Json(name = "content") val content: String
)

@JsonClass(generateAdapter = true)
data class AgentChatResponse(
    @Json(name = "session_id") val sessionId: String,
    @Json(name = "message_id") val messageId: String,
    @Json(name = "content") val content: String,
    @Json(name = "agent_type") val agentType: String,
    @Json(name = "confidence") val confidence: Int,
    @Json(name = "safety_level") val safetyLevel: String?,
    @Json(name = "suggested_actions") val suggestedActions: List<String> = emptyList(),
    @Json(name = "metadata") val metadata: Map<String, String> = emptyMap()
)

@JsonClass(generateAdapter = true)
data class AgentDiagnoseRequest(
    @Json(name = "symptoms") val symptoms: List<String>,
    @Json(name = "dtc_codes") val dtcCodes: List<String> = emptyList(),
    @Json(name = "vehicle_vin") val vehicleVin: String? = null,
    @Json(name = "mileage") val mileage: Int? = null
)

@JsonClass(generateAdapter = true)
data class AgentDiagnoseResponse(
    @Json(name = "diagnosis") val diagnosis: String,
    @Json(name = "confidence") val confidence: Int,
    @Json(name = "possible_causes") val possibleCauses: List<String>,
    @Json(name = "recommended_repairs") val recommendedRepairs: List<String>,
    @Json(name = "safety_level") val safetyLevel: String,
    @Json(name = "estimated_cost_min") val estimatedCostMin: Double,
    @Json(name = "estimated_cost_max") val estimatedCostMax: Double
)
