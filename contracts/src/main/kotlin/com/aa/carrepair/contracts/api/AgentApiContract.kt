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
    @Json(name = "privacy_mode") val privacyMode: String,
    @Json(name = "vehicle_context") val vehicleContext: VehicleContextDto? = null,
    @Json(name = "obd_context") val obdContext: ObdContextDto? = null
)

@JsonClass(generateAdapter = true)
data class AgentDiagnoseRequest(
    @Json(name = "symptoms") val symptoms: List<String>,
    @Json(name = "dtc_codes") val dtcCodes: List<String> = emptyList(),
    @Json(name = "vehicle_vin") val vehicleVin: String? = null,
    @Json(name = "mileage") val mileage: Int? = null
)

@JsonClass(generateAdapter = true)
data class VehicleContextDto(
    @Json(name = "vin") val vin: String? = null,
    @Json(name = "year") val year: Int? = null,
    @Json(name = "make") val make: String? = null,
    @Json(name = "model") val model: String? = null,
    @Json(name = "mileage_km") val mileageKm: Int? = null
)

@JsonClass(generateAdapter = true)
data class ObdContextDto(
    @Json(name = "dtc_codes") val dtcCodes: List<String> = emptyList(),
    @Json(name = "pending_codes") val pendingCodes: List<String> = emptyList(),
    @Json(name = "freeze_frame") val freezeFrame: Map<String, String> = emptyMap(),
    @Json(name = "live_pids") val livePids: Map<String, String> = emptyMap()
)

@JsonClass(generateAdapter = true)
data class AgentChatResponse(
    @Json(name = "response_id") val responseId: String = "",
    @Json(name = "request_id") val requestId: String = "",
    @Json(name = "surface") val surface: String = "mobile",
    @Json(name = "response_type") val responseType: String = "text",
    @Json(name = "answer_text") val answerText: String,
    @Json(name = "answer_format") val answerFormat: String = "text",
    @Json(name = "confidence") val confidence: Int,
    @Json(name = "safety_level") val safetyLevel: String,
    @Json(name = "citations") val citations: List<String> = emptyList(),
    @Json(name = "next_actions") val nextActions: List<String> = emptyList(),
    @Json(name = "diagnosis_candidates") val diagnosisCandidates: List<String> = emptyList(),
    @Json(name = "recommended_tests") val recommendedTests: List<String> = emptyList(),
    @Json(name = "parts_and_tools") val partsAndTools: List<String> = emptyList(),
    @Json(name = "estimated_time") val estimatedTime: String? = null,
    @Json(name = "escalation") val escalation: String? = null,
    @Json(name = "risk_flags") val riskFlags: List<String> = emptyList(),
    @Json(name = "troubleshooting_tree") val troubleshootingTree: TroubleshootingTreeDto? = null,
    @Json(name = "diagnostic_report") val diagnosticReport: DiagnosticReportDto? = null,
    @Json(name = "audit_trace_id") val auditTraceId: String
)

@JsonClass(generateAdapter = true)
data class DiagnosticReportDto(
    @Json(name = "vehicle_summary") val vehicleSummary: String = "",
    @Json(name = "symptoms") val symptoms: List<String> = emptyList(),
    @Json(name = "dtc_codes") val dtcCodes: List<String> = emptyList(),
    @Json(name = "diagnostic_summary") val diagnosticSummary: String,
    @Json(name = "recommended_tests") val recommendedTests: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class TroubleshootingTreeDto(
    @Json(name = "symptom_node") val symptomNode: SymptomNodeDto,
    @Json(name = "test_nodes") val testNodes: List<TestNodeDto> = emptyList(),
    @Json(name = "completion_criteria") val completionCriteria: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class SymptomNodeDto(
    @Json(name = "title") val title: String,
    @Json(name = "description") val description: String = ""
)

@JsonClass(generateAdapter = true)
data class TestNodeDto(
    @Json(name = "id") val id: String = "",
    @Json(name = "title") val title: String,
    @Json(name = "instructions") val instructions: String = "",
    @Json(name = "outcome_branches") val outcomeBranches: List<OutcomeBranchDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class OutcomeBranchDto(
    @Json(name = "outcome") val outcome: String,
    @Json(name = "fix_node") val fixNode: FixNodeDto,
    @Json(name = "completion_criteria") val completionCriteria: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class FixNodeDto(
    @Json(name = "title") val title: String,
    @Json(name = "details") val details: String = "",
    @Json(name = "priority") val priority: String = "normal"
)
