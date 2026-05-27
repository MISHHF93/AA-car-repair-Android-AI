package com.aa.carrepair.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aa.carrepair.domain.model.MessageRole
import com.aa.carrepair.domain.model.SafetyLevel
import com.aa.carrepair.ui.R
import com.aa.carrepair.ui.theme.AAAmber
import com.aa.carrepair.ui.theme.AANavy
import com.aa.carrepair.ui.theme.SafetyCritical
import com.aa.carrepair.ui.theme.SafetyHigh
import com.aa.carrepair.ui.theme.SafetyLow
import com.aa.carrepair.ui.theme.SafetyMedium
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ChatBubble(
    content: String,
    role: MessageRole,
    timestamp: Instant,
    confidence: Int? = null,
    safetyLevel: SafetyLevel? = null,
    modifier: Modifier = Modifier
) {
    val isUser = role == MessageRole.USER
    val parsedResponse = if (isUser) null else remember(content) { parseAssistantResponse(content) }
    val cdText = stringResource(
        if (isUser) R.string.cd_chat_bubble_user else R.string.cd_chat_bubble_ai
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isUser) {
            AiAvatar()
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.widthIn(max = 300.dp),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            BubbleBox(
                content = content,
                isUser = isUser,
                cdText = cdText,
                parsedResponse = parsedResponse,
                confidence = confidence,
                safetyLevel = safetyLevel
            )
            Spacer(modifier = Modifier.height(4.dp))
            BubbleFooter(
                timestamp = timestamp,
                isUser = isUser,
                content = content,
                confidence = if (parsedResponse == null) confidence else null
            )
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            UserAvatar()
        }
    }
}

@Composable
private fun AiAvatar() {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(AANavy),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Build,
            contentDescription = null,
            tint = AAAmber,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun UserAvatar() {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Me",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun BubbleBox(
    content: String,
    isUser: Boolean,
    cdText: String,
    parsedResponse: ParsedAssistantResponse?,
    confidence: Int?,
    safetyLevel: SafetyLevel?
) {
    val bubbleColor = if (isUser) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surface
    }
    val textColor = if (isUser) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    val shape = RoundedCornerShape(
        topStart = 18.dp,
        topEnd = 18.dp,
        bottomStart = if (isUser) 18.dp else 4.dp,
        bottomEnd = if (isUser) 4.dp else 18.dp
    )

    Box(
        modifier = Modifier
            .clip(shape)
            .background(bubbleColor)
            .padding(horizontal = 14.dp, vertical = 10.dp)
            .semantics { contentDescription = "$cdText: $content" }
    ) {
        if (parsedResponse != null && !isUser) {
            AssistantResponseCard(
                response = parsedResponse,
                confidence = confidence ?: parsedResponse.confidence,
                safetyLevel = safetyLevel ?: parsedResponse.safetyLevel
            )
        } else {
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
private fun AssistantResponseCard(
    response: ParsedAssistantResponse,
    confidence: Int?,
    safetyLevel: SafetyLevel?
) {
    var citationsExpanded by remember { mutableStateOf(false) }
    val highRiskTerms = remember(response.answerText) {
        HIGH_RISK_TERMS.filter { term ->
            response.answerText.contains(term, ignoreCase = true)
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (response.diagnosticReport != null) {
            DiagnosticReportCard(
                report = response.diagnosticReport,
                safetyLevel = safetyLevel ?: response.safetyLevel,
                confidence = confidence ?: response.confidence,
                citations = response.citations,
                auditTraceId = response.auditTraceId
            )
        } else {
            if (highRiskTerms.isNotEmpty()) {
                HighRiskWarning(highRiskTerms = highRiskTerms)
            }

            if (safetyLevel != null) {
                SafetyBanner(level = safetyLevel)
            }

            Text(
                text = response.answerText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 22.sp
            )

            response.troubleshootingTree?.let { tree ->
                TroubleshootingTreeCard(tree = tree)
            }

            confidence?.let {
                ConfidenceBadge(confidence = it)
            }

            if (response.diagnosisCandidates.isNotEmpty()) {
                ReportListSection(title = "Diagnosis candidates", items = response.diagnosisCandidates)
            }
            if (response.recommendedTests.isNotEmpty()) {
                ReportListSection(title = "Recommended tests", items = response.recommendedTests)
            }
            if (response.partsAndTools.isNotEmpty()) {
                ReportListSection(title = "Parts/tools", items = response.partsAndTools)
            }
            response.estimatedTime?.let { ReportTextSection(title = "Estimated time", body = it) }

            if (response.riskFlags.isNotEmpty()) {
                ReportListSection(title = "Risk flags", items = response.riskFlags)
            }

            response.escalation?.let { escalation ->
                ReportTextSection(title = "Escalation", body = escalation)
            }

            if (response.citations.isNotEmpty()) {
                TextButton(
                    onClick = { citationsExpanded = !citationsExpanded },
                    modifier = Modifier.padding(start = 0.dp)
                ) {
                    Text(
                        text = if (citationsExpanded) {
                            "Hide citations"
                        } else {
                            "Show citations (${response.citations.size})"
                        }
                    )
                }
                if (citationsExpanded) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        response.citations.forEach { citation ->
                            Text(
                                text = "• $citation",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }

            response.auditTraceId?.let { traceId ->
                Text(
                    text = "Audit trace: $traceId",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun DiagnosticReportCard(
    report: ParsedDiagnosticReport,
    safetyLevel: SafetyLevel?,
    confidence: Int?,
    citations: List<String>,
    auditTraceId: String?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "AI Diagnostic Report",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        ReportTextSection(title = "Vehicle summary", body = report.vehicleSummary.ifBlank { "Not provided" })
        ReportListSection(title = "Symptoms", items = report.symptoms)
        ReportListSection(title = "DTC codes", items = report.dtcCodes)
        ReportTextSection(title = "AI diagnostic summary", body = report.diagnosticSummary)
        ReportListSection(title = "Diagnosis candidates", items = report.diagnosisCandidates)
        ReportListSection(title = "Recommended tests", items = report.recommendedTests)
        ReportListSection(title = "Parts/tools", items = report.partsAndTools)
        report.estimatedTime?.let { ReportTextSection(title = "Estimated time", body = it) }

        safetyLevel?.let { SafetyBanner(level = it) }
        confidence?.let { ConfidenceBadge(confidence = it) }
        ReportListSection(title = "Risk flags", items = report.riskFlags)
        report.escalation?.let { ReportTextSection(title = "Escalation", body = it) }

        ReportListSection(title = "Citations", items = citations)

        auditTraceId?.let { traceId ->
            ReportTextSection(title = "Audit trace ID", body = traceId)
        }

        TextButton(
            onClick = {},
            enabled = false,
            modifier = Modifier.padding(start = 0.dp)
        ) {
            Text("Export report (PDF placeholder)")
        }
    }
}

@Composable
private fun ReportTextSection(
    title: String,
    body: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = body.ifBlank { "None" },
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun ReportListSection(
    title: String,
    items: List<String>
) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        if (items.isEmpty()) {
            Text(
                text = "None",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            items.forEach { item ->
                Text(
                    text = "- $item",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun TroubleshootingTreeCard(tree: ParsedTroubleshootingTree) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        SymptomNodeView(symptom = tree.symptom)

        tree.tests.forEachIndexed { index, test ->
            TestNodeView(
                number = index + 1,
                test = test
            )
        }

        if (tree.completionCriteria.isNotEmpty()) {
            CompletionCriteriaView(criteria = tree.completionCriteria)
        }
    }
}

@Composable
private fun SymptomNodeView(symptom: ParsedSymptomNode) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(
            text = "Symptom node",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = symptom.title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
        if (symptom.description.isNotBlank()) {
            Text(
                text = symptom.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun TestNodeView(
    number: Int,
    test: ParsedTestNode
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "Test $number: ${test.title}",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
        if (test.instructions.isNotBlank()) {
            Text(
                text = test.instructions,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )
        }
        test.branches.forEach { branch ->
            OutcomeBranchView(branch = branch)
        }
    }
}

@Composable
private fun OutcomeBranchView(branch: ParsedOutcomeBranch) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "Outcome: ${branch.outcome}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 18.sp
        )
        Text(
            text = "Fix: ${branch.fix}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 18.sp
        )
        if (branch.completionCriteria.isNotEmpty()) {
            Text(
                text = "Done when: ${branch.completionCriteria.joinToString("; ")}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun CompletionCriteriaView(criteria: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "Completion criteria",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        criteria.forEach { item ->
            Text(
                text = "- $item",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun SafetyBanner(level: SafetyLevel) {
    val color = when (level) {
        SafetyLevel.RESTRICTED -> SafetyCritical
        SafetyLevel.CRITICAL -> SafetyCritical
        SafetyLevel.HIGH -> SafetyHigh
        SafetyLevel.MEDIUM -> SafetyMedium
        SafetyLevel.LOW -> SafetyLow
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Safety level",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        SafetyBadge(level = level)
        if (level == SafetyLevel.RESTRICTED) {
            Text(
                text = "Escalate to a qualified technician before continuing.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun HighRiskWarning(highRiskTerms: List<String>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SafetyCritical.copy(alpha = 0.12f))
            .padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = SafetyCritical,
            modifier = Modifier.size(18.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "High-risk safety topic",
                style = MaterialTheme.typography.labelMedium,
                color = SafetyCritical,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Detected: ${highRiskTerms.joinToString(", ")}. Do not drive if safety is uncertain; consult a qualified technician.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun BubbleFooter(
    timestamp: Instant,
    isUser: Boolean,
    content: String,
    confidence: Int?
) {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault())
    val clipboard = LocalClipboardManager.current
    var thumbState by remember { mutableStateOf<Boolean?>(null) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        if (!isUser) {
            if (confidence != null) {
                ConfidenceBadge(confidence = confidence)
                Spacer(modifier = Modifier.width(6.dp))
            }
            IconButton(
                onClick = { clipboard.setText(AnnotatedString(content)) },
                modifier = Modifier.size(22.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = stringResource(R.string.chat_copy_message),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
            }
            IconButton(
                onClick = { thumbState = true },
                modifier = Modifier.size(22.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ThumbUp,
                    contentDescription = stringResource(R.string.chat_feedback_helpful),
                    tint = if (thumbState == true) {
                        SafetyLow
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.size(14.dp)
                )
            }
            IconButton(
                onClick = { thumbState = false },
                modifier = Modifier.size(22.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ThumbDown,
                    contentDescription = stringResource(R.string.chat_feedback_not_helpful),
                    tint = if (thumbState == false) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    modifier = Modifier.size(14.dp)
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
        }

        Text(
            text = timeFormatter.format(timestamp),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private data class ParsedAssistantResponse(
    val answerText: String,
    val confidence: Int?,
    val safetyLevel: SafetyLevel?,
    val citations: List<String>,
    val auditTraceId: String?,
    val troubleshootingTree: ParsedTroubleshootingTree?,
    val diagnosticReport: ParsedDiagnosticReport?,
    val diagnosisCandidates: List<String>,
    val recommendedTests: List<String>,
    val partsAndTools: List<String>,
    val estimatedTime: String?,
    val escalation: String?,
    val riskFlags: List<String>
)

private data class ParsedDiagnosticReport(
    val vehicleSummary: String,
    val symptoms: List<String>,
    val dtcCodes: List<String>,
    val diagnosticSummary: String,
    val diagnosisCandidates: List<String>,
    val recommendedTests: List<String>,
    val partsAndTools: List<String>,
    val estimatedTime: String?,
    val escalation: String?,
    val riskFlags: List<String>
)

private data class ParsedTroubleshootingTree(
    val symptom: ParsedSymptomNode,
    val tests: List<ParsedTestNode>,
    val completionCriteria: List<String>
)

private data class ParsedSymptomNode(
    val title: String,
    val description: String
)

private data class ParsedTestNode(
    val title: String,
    val instructions: String,
    val branches: List<ParsedOutcomeBranch>
)

private data class ParsedOutcomeBranch(
    val outcome: String,
    val fix: String,
    val completionCriteria: List<String>
)

private val HIGH_RISK_TERMS = listOf(
    "brakes",
    "steering",
    "airbag",
    "airbags",
    "SRS",
    "high voltage",
    "EV battery",
    "fuel system",
    "jacking",
    "lifting"
)

private fun parseAssistantResponse(content: String): ParsedAssistantResponse? {
    val lines = content.lines()
    val answerText = lines.valueAfter("answer_text:") ?: return null
    val confidence = lines.valueAfter("confidence:")?.toIntOrNull()
    val safetyLevel = lines.valueAfter("safety_level:")?.toSafetyLevelOrNull()
    val auditTraceId = lines.valueAfter("audit_trace_id:")
    val troubleshootingTree = if (
        lines.valueAfter("response_type:").equals("troubleshooting_tree", ignoreCase = true)
    ) {
        parseTroubleshootingTree(lines)
    } else {
        null
    }
    val diagnosticReport = if (
        lines.valueAfter("response_type:").equals("diagnostic_report", ignoreCase = true)
    ) {
        parseDiagnosticReport(lines)
    } else {
        null
    }
    val citations = lines
        .dropWhile { !it.trim().equals("citations:", ignoreCase = true) }
        .drop(1)
        .takeWhile { !it.trim().startsWith("audit_trace_id:", ignoreCase = true) }
        .map { it.trim().removePrefix("-").trim() }
        .filter { it.isNotBlank() && !it.equals("None", ignoreCase = true) }
    val riskFlags = lines.betweenSections(
        startLabel = "risk_flags:",
        endLabels = listOf("citations:", "audit_trace_id:")
    ).listItems()
    val diagnosisCandidates = lines.betweenSections(
        startLabel = "diagnosis_candidates:",
        endLabels = listOf("recommended_tests:", "parts_and_tools:", "estimated_time:", "escalation:", "risk_flags:", "citations:", "audit_trace_id:")
    ).listItems()
    val recommendedTests = lines.betweenSections(
        startLabel = "recommended_tests:",
        endLabels = listOf("parts_and_tools:", "estimated_time:", "escalation:", "risk_flags:", "citations:", "audit_trace_id:")
    ).listItems()
    val partsAndTools = lines.betweenSections(
        startLabel = "parts_and_tools:",
        endLabels = listOf("estimated_time:", "escalation:", "risk_flags:", "citations:", "audit_trace_id:")
    ).listItems()

    return ParsedAssistantResponse(
        answerText = answerText,
        confidence = confidence,
        safetyLevel = safetyLevel,
        citations = citations,
        auditTraceId = auditTraceId,
        troubleshootingTree = troubleshootingTree,
        diagnosticReport = diagnosticReport,
        diagnosisCandidates = diagnosisCandidates,
        recommendedTests = recommendedTests,
        partsAndTools = partsAndTools,
        estimatedTime = lines.valueAfter("estimated_time:"),
        escalation = lines.valueAfter("escalation:"),
        riskFlags = riskFlags
    )
}

private fun parseDiagnosticReport(lines: List<String>): ParsedDiagnosticReport? {
    val diagnosticSummary = lines.valueAfter("ai_diagnostic_summary:")
        ?: lines.valueAfter("answer_text:")
        ?: return null
    val symptoms = lines.betweenSections(
        startLabel = "symptoms:",
        endLabels = listOf("dtc_codes:", "ai_diagnostic_summary:", "recommended_tests:", "citations:", "audit_trace_id:")
    ).listItems()
    val dtcCodes = lines.betweenSections(
        startLabel = "dtc_codes:",
        endLabels = listOf("ai_diagnostic_summary:", "recommended_tests:", "citations:", "audit_trace_id:")
    ).listItems()
    val recommendedTests = lines.betweenSections(
        startLabel = "recommended_tests:",
        endLabels = listOf("diagnosis_candidates:", "parts_and_tools:", "estimated_time:", "escalation:", "risk_flags:", "citations:", "audit_trace_id:")
    ).listItems()
    val diagnosisCandidates = lines.betweenSections(
        startLabel = "diagnosis_candidates:",
        endLabels = listOf("parts_and_tools:", "estimated_time:", "escalation:", "risk_flags:", "citations:", "audit_trace_id:")
    ).listItems()
    val partsAndTools = lines.betweenSections(
        startLabel = "parts_and_tools:",
        endLabels = listOf("estimated_time:", "escalation:", "risk_flags:", "citations:", "audit_trace_id:")
    ).listItems()
    val riskFlags = lines.betweenSections(
        startLabel = "risk_flags:",
        endLabels = listOf("citations:", "audit_trace_id:")
    ).listItems()

    return ParsedDiagnosticReport(
        vehicleSummary = lines.valueAfter("vehicle_summary:").orEmpty(),
        symptoms = symptoms,
        dtcCodes = dtcCodes,
        diagnosticSummary = diagnosticSummary,
        diagnosisCandidates = diagnosisCandidates,
        recommendedTests = recommendedTests,
        partsAndTools = partsAndTools,
        estimatedTime = lines.valueAfter("estimated_time:"),
        escalation = lines.valueAfter("escalation:"),
        riskFlags = riskFlags
    )
}

private fun parseTroubleshootingTree(lines: List<String>): ParsedTroubleshootingTree? {
    val symptomTitle = lines.valueAfter("symptom_node:") ?: return null
    val symptomDescription = lines.valueAfter("symptom_description:").orEmpty()
    val testLines = lines.betweenSections(
        startLabel = "test_nodes:",
        endLabels = listOf("completion_criteria:", "citations:", "audit_trace_id:")
    )
    val tests = parseTestNodes(testLines)
    val completionCriteria = lines.betweenSections(
        startLabel = "completion_criteria:",
        endLabels = listOf("citations:", "audit_trace_id:")
    ).listItems()

    return ParsedTroubleshootingTree(
        symptom = ParsedSymptomNode(
            title = symptomTitle,
            description = symptomDescription
        ),
        tests = tests,
        completionCriteria = completionCriteria
    )
}

private fun parseTestNodes(lines: List<String>): List<ParsedTestNode> {
    data class TestBuilder(
        val title: String,
        var instructions: String = "",
        val branches: MutableList<ParsedOutcomeBranch> = mutableListOf()
    )

    val tests = mutableListOf<TestBuilder>()
    var current: TestBuilder? = null

    lines.forEach { rawLine ->
        val line = rawLine.trim()
        when {
            line.startsWith("- test:", ignoreCase = true) -> {
                current = TestBuilder(line.substringAfter(":").trim())
                tests += current!!
            }
            line.startsWith("instruction:", ignoreCase = true) -> {
                current?.instructions = line.substringAfter(":").trim()
            }
            line.startsWith("- branch:", ignoreCase = true) -> {
                parseOutcomeBranch(line)?.let { current?.branches?.add(it) }
            }
        }
    }

    return tests.map { builder ->
        ParsedTestNode(
            title = builder.title,
            instructions = builder.instructions,
            branches = builder.branches
        )
    }
}

private fun parseOutcomeBranch(line: String): ParsedOutcomeBranch? {
    val body = line.substringAfter(":").trim()
    val outcome = body.substringBefore(" | fix:").trim()
    val fixAndCompletion = body.substringAfter(" | fix:", missingDelimiterValue = "")
    val fix = fixAndCompletion.substringBefore(" | completion:").trim()
    val criteria = fixAndCompletion
        .substringAfter(" | completion:", missingDelimiterValue = "")
        .split(";")
        .map { it.trim() }
        .filter { it.isNotBlank() }

    if (outcome.isBlank() || fix.isBlank()) return null

    return ParsedOutcomeBranch(
        outcome = outcome,
        fix = fix,
        completionCriteria = criteria
    )
}

private fun List<String>.valueAfter(label: String): String? =
    firstOrNull { it.trim().startsWith(label, ignoreCase = true) }
        ?.substringAfter(":")
        ?.trim()
        ?.takeIf { it.isNotBlank() }

private fun List<String>.betweenSections(
    startLabel: String,
    endLabels: List<String>
): List<String> =
    dropWhile { !it.trim().startsWith(startLabel, ignoreCase = true) }
        .drop(1)
        .takeWhile { line ->
            endLabels.none { label -> line.trim().startsWith(label, ignoreCase = true) }
        }

private fun List<String>.listItems(): List<String> =
    map { it.trim().removePrefix("-").trim() }
        .filter { it.isNotBlank() && !it.equals("None", ignoreCase = true) }

private fun String.toSafetyLevelOrNull(): SafetyLevel? =
    SafetyLevel.values().firstOrNull { it.name.equals(this, ignoreCase = true) }
