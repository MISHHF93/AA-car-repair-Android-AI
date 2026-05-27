package com.aa.carrepair.contracts.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AgentChatRequest(
    @Json(name = "request_id") val requestId: String,
    @Json(name = "timestamp_utc") val timestampUtc: String,
    @Json(name = "surface") val surface: String,
    @Json(name = "user_role") val userRole: String,
    @Json(name = "locale") val locale: String,
    @Json(name = "query_text") val queryText: String,
    @Json(name = "policy_profile") val policyProfile: String,
    @Json(name = "privacy_mode") val privacyMode: String
)

@JsonClass(generateAdapter = true)
data class AgentChatResponse(
    @Json(name = "response_id") val responseId: String = "",
    @Json(name = "request_id") val requestId: String = "",
    @Json(name = "surface") val surface: String = "mobile",
    @Json(name = "answer_text") val answerText: String,
    @Json(name = "answer_format") val answerFormat: String = "text",
    @Json(name = "confidence") val confidence: Int,
    @Json(name = "safety_level") val safetyLevel: String,
    @Json(name = "citations") val citations: List<String> = emptyList(),
    @Json(name = "next_actions") val nextActions: List<String> = emptyList(),
    @Json(name = "audit_trace_id") val auditTraceId: String
)
