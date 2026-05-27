package com.aa.carrepair.data.repository

import com.aa.carrepair.contracts.api.AgentChatRequest
import com.aa.carrepair.contracts.api.AgentChatResponse
import com.aa.carrepair.contracts.api.DiagnosticReportDto
import com.aa.carrepair.contracts.api.FixNodeDto
import com.aa.carrepair.contracts.api.ObdContextDto
import com.aa.carrepair.contracts.api.OutcomeBranchDto
import com.aa.carrepair.contracts.api.SymptomNodeDto
import com.aa.carrepair.contracts.api.TestNodeDto
import com.aa.carrepair.contracts.api.TroubleshootingTreeDto
import com.aa.carrepair.contracts.api.VehicleContextDto
import com.aa.carrepair.core.result.DataResult
import com.aa.carrepair.core.result.safeApiCall
import com.aa.carrepair.data.BuildConfig
import com.aa.carrepair.data.local.dao.ChatDao
import com.aa.carrepair.data.local.entity.ChatMessageEntity
import com.aa.carrepair.data.remote.api.AgentApi
import com.aa.carrepair.domain.model.AgentResponse
import com.aa.carrepair.domain.model.AgentType
import com.aa.carrepair.domain.model.ChatMessage
import com.aa.carrepair.domain.model.MessageRole
import com.aa.carrepair.domain.model.ObdContext
import com.aa.carrepair.domain.model.SafetyLevel
import com.aa.carrepair.domain.model.VehicleContext
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
        vehicleContext: VehicleContext?,
        obdContext: ObdContext?
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
                    privacyMode = "standard",
                    vehicleContext = vehicleContext?.toDto(),
                    obdContext = obdContext?.toDto()
                )
            )

            val troubleshootingTree = response.troubleshootingTree
                ?: mockTroubleshootingTreeForDevelopment(message)
            val diagnosticReport = response.diagnosticReport
                ?: mockDiagnosticReportForDevelopment(message, response, vehicleContext, obdContext)
            val answerText = response.toStoredAssistantContent(
                troubleshootingTree = troubleshootingTree,
                diagnosticReport = diagnosticReport
            )

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
                    "response_type" to when {
                        diagnosticReport != null -> RESPONSE_TYPE_DIAGNOSTIC_REPORT
                        troubleshootingTree != null -> RESPONSE_TYPE_TREE
                        else -> response.responseType
                    },
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
        safetyLevel = safetyLevel?.let { value ->
            SafetyLevel.values().firstOrNull { it.name.equals(value, ignoreCase = true) }
        },
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

    private fun VehicleContext.toDto(): VehicleContextDto = VehicleContextDto(
        vin = vin,
        year = year,
        make = make,
        model = model,
        mileageKm = mileageKm
    )

    private fun ObdContext.toDto(): ObdContextDto = ObdContextDto(
        dtcCodes = dtcCodes,
        pendingCodes = pendingCodes,
        freezeFrame = freezeFrame,
        livePids = livePids
    )

    private fun AgentChatResponse.toStoredAssistantContent(
        troubleshootingTree: TroubleshootingTreeDto?,
        diagnosticReport: DiagnosticReportDto?
    ): String = buildString {
        when {
            diagnosticReport != null -> append("response_type: ").append(RESPONSE_TYPE_DIAGNOSTIC_REPORT).append('\n')
            troubleshootingTree != null -> append("response_type: ").append(RESPONSE_TYPE_TREE).append('\n')
        }
        append("answer_text: ")
        append(answerText)
        append("\nconfidence: ").append(confidence)
        append("\nsafety_level: ").append(safetyLevel)
        if (diagnosticReport == null) {
            append("\ndiagnosis_candidates:")
            appendListOrNone(diagnosisCandidates)
            append("\nrecommended_tests:")
            appendListOrNone(recommendedTests)
            append("\nparts_and_tools:")
            appendListOrNone(partsAndTools)
            estimatedTime?.let { append("\nestimated_time: ").append(it) }
        }
        diagnosticReport?.let { report ->
            append("\nvehicle_summary: ").append(report.vehicleSummary.ifBlank { "Not provided" })
            append("\nsymptoms:")
            appendListOrNone(report.symptoms)
            append("\ndtc_codes:")
            appendListOrNone(report.dtcCodes)
            append("\nai_diagnostic_summary: ").append(report.diagnosticSummary)
            append("\nrecommended_tests:")
            appendListOrNone(report.recommendedTests.ifEmpty { recommendedTests })
            append("\ndiagnosis_candidates:")
            appendListOrNone(diagnosisCandidates)
            append("\nparts_and_tools:")
            appendListOrNone(partsAndTools)
            estimatedTime?.let { append("\nestimated_time: ").append(it) }
        }
        troubleshootingTree?.let { tree ->
            append("\nsymptom_node: ").append(tree.symptomNode.title)
            if (tree.symptomNode.description.isNotBlank()) {
                append("\nsymptom_description: ").append(tree.symptomNode.description)
            }
            append("\ntest_nodes:")
            if (tree.testNodes.isEmpty()) {
                append(" None")
            } else {
                tree.testNodes.forEach { test ->
                    append("\n- test: ").append(test.title)
                    if (test.instructions.isNotBlank()) {
                        append("\n  instruction: ").append(test.instructions)
                    }
                    test.outcomeBranches.forEach { branch ->
                        append("\n  - branch: ").append(branch.outcome)
                        append(" | fix: ").append(branch.fixNode.title)
                        if (branch.fixNode.details.isNotBlank()) {
                            append(" - ").append(branch.fixNode.details)
                        }
                        val criteria = branch.completionCriteria.joinToString("; ")
                        if (criteria.isNotBlank()) {
                            append(" | completion: ").append(criteria)
                        }
                    }
                }
            }
            append("\ncompletion_criteria:")
            if (tree.completionCriteria.isEmpty()) {
                append(" None")
            } else {
                tree.completionCriteria.forEach { append("\n- ").append(it) }
            }
        }
        escalation?.let { append("\nescalation: ").append(it) }
        append("\nrisk_flags:")
        appendListOrNone(riskFlags)
        append("\ncitations:")
        if (citations.isEmpty()) {
            append(" None")
        } else {
            citations.forEach { append("\n- ").append(it) }
        }
        append("\naudit_trace_id: ").append(auditTraceId)
    }.trim()

    private fun StringBuilder.appendListOrNone(values: List<String>) {
        if (values.isEmpty()) {
            append(" None")
        } else {
            values.forEach { append("\n- ").append(it) }
        }
    }

    private fun mockDiagnosticReportForDevelopment(
        query: String,
        response: AgentChatResponse,
        vehicleContext: VehicleContext?,
        obdContext: ObdContext?
    ): DiagnosticReportDto? {
        if (!BuildConfig.DEBUG || !query.requestsDiagnosticReport()) return null

        return DiagnosticReportDto(
            vehicleSummary = vehicleContext?.toReportSummary().orEmpty(),
            symptoms = listOf(query.trim()).filter { it.isNotBlank() },
            dtcCodes = obdContext?.allDtcCodes().orEmpty(),
            diagnosticSummary = response.answerText,
            recommendedTests = response.nextActions.ifEmpty {
                listOf(
                    "Confirm symptoms under safe conditions",
                    "Scan current and pending DTCs",
                    "Inspect related components before replacing parts"
                )
            }
        )
    }

    private fun VehicleContext.toReportSummary(): String =
        listOfNotNull(
            listOfNotNull(year?.toString(), make, model).joinToString(" ").takeIf { it.isNotBlank() },
            mileageKm?.let { "$it km" },
            vin?.let { "VIN $it" }
        ).joinToString(" • ")

    private fun ObdContext.allDtcCodes(): List<String> =
        dtcCodes + pendingCodes

    private fun mockTroubleshootingTreeForDevelopment(query: String): TroubleshootingTreeDto? {
        if (!BuildConfig.DEBUG || !query.requestsTroubleshootingTree()) return null

        return TroubleshootingTreeDto(
            symptomNode = SymptomNodeDto(
                title = "Reported vehicle symptom",
                description = query.trim().take(140)
            ),
            testNodes = listOf(
                TestNodeDto(
                    id = "test-visual-check",
                    title = "Confirm the symptom and visible warning signs",
                    instructions = "Park safely, set the parking brake, and note warning lights, leaks, odors, or abnormal sounds.",
                    outcomeBranches = listOf(
                        OutcomeBranchDto(
                            outcome = "Warning light, leak, burning smell, smoke, or loss of control is present",
                            fixNode = FixNodeDto(
                                title = "Stop driving and arrange professional inspection",
                                details = "High-risk symptoms should be handled by a qualified technician before further driving.",
                                priority = "high"
                            ),
                            completionCriteria = listOf("Vehicle is inspected and the unsafe condition is corrected")
                        ),
                        OutcomeBranchDto(
                            outcome = "No immediate safety warning signs are present",
                            fixNode = FixNodeDto(
                                title = "Continue with controlled checks",
                                details = "Use the next test node to narrow down the likely system.",
                                priority = "normal"
                            ),
                            completionCriteria = listOf("Symptom can be reproduced without creating a safety risk")
                        )
                    )
                ),
                TestNodeDto(
                    id = "test-reproduce",
                    title = "Reproduce the symptom at low risk",
                    instructions = "Only test in a safe location and stop immediately if braking, steering, airbags, SRS, high voltage, EV battery, or fuel system concerns appear.",
                    outcomeBranches = listOf(
                        OutcomeBranchDto(
                            outcome = "Symptom is repeatable",
                            fixNode = FixNodeDto(
                                title = "Document conditions and inspect the matching system",
                                details = "Record speed, temperature, road conditions, warning lights, and recent service history.",
                                priority = "normal"
                            ),
                            completionCriteria = listOf("Cause is isolated to a system or a technician has enough details to diagnose")
                        ),
                        OutcomeBranchDto(
                            outcome = "Symptom cannot be reproduced",
                            fixNode = FixNodeDto(
                                title = "Monitor and avoid unnecessary part replacement",
                                details = "Do not replace parts until the symptom or diagnostic evidence is confirmed.",
                                priority = "low"
                            ),
                            completionCriteria = listOf("No active warning lights or drivability issues remain")
                        )
                    )
                )
            ),
            completionCriteria = listOf(
                "The original symptom is no longer present",
                "No new warning lights or leaks are present",
                "A short safe test drive confirms normal operation"
            )
        )
    }

    private fun String.requestsTroubleshootingTree(): Boolean {
        val normalized = lowercase()
        return listOf("troubleshoot", "troubleshooting", "diagnostic tree", "tree", "step by step")
            .any { normalized.contains(it) }
    }

    private fun String.requestsDiagnosticReport(): Boolean {
        val normalized = lowercase()
        return listOf("diagnostic report", "ai report", "report", "diagnosis report")
            .any { normalized.contains(it) }
    }

    private companion object {
        const val RESPONSE_TYPE_DIAGNOSTIC_REPORT = "diagnostic_report"
        const val RESPONSE_TYPE_TREE = "troubleshooting_tree"
    }
}
