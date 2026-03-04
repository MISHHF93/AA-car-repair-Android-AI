package com.aa.carrepair.feature.inspection

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aa.carrepair.domain.model.FindingSeverity
import com.aa.carrepair.domain.model.InspectionFinding
import com.aa.carrepair.domain.model.InspectionMode
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun InspectionScreen(
    onNavigateBack: () -> Unit,
    viewModel: InspectionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? -> uri?.let { viewModel.onImageCaptured(it) } }

    Scaffold(
        topBar = {
            com.aa.carrepair.ui.components.AATopAppBar(
                title = "Visual Inspection",
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Inspection Mode", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                InspectionMode.values().forEach { mode ->
                    FilterChip(
                        selected = uiState.selectedMode == mode,
                        onClick = { viewModel.onModeSelected(mode) },
                        label = {
                            Text(
                                when (mode) {
                                    InspectionMode.DAMAGE_ASSESSMENT -> "Damage"
                                    InspectionMode.PARTS_IDENTIFICATION -> "Parts"
                                    InspectionMode.WEAR_ANALYSIS -> "Wear"
                                }
                            )
                        }
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = {
                        if (!cameraPermission.status.isGranted) {
                            cameraPermission.launchPermissionRequest()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                    Text(" Take Photo")
                }
                Button(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Photo, contentDescription = null)
                    Text(" Gallery")
                }
            }

            if (uiState.isAnalyzing) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                Text("Analyzing image…", modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            uiState.error?.let { error ->
                com.aa.carrepair.ui.components.ErrorCard(
                    message = error,
                    onRetry = { viewModel.clearError() }
                )
            }

            uiState.result?.let { result ->
                Text("Findings", style = MaterialTheme.typography.titleMedium)
                Text("Severity Score: ${String.format("%.1f", result.severityScore * 10)}/10")

                result.findings.forEach { finding ->
                    FindingCard(finding = finding)
                }

                if (result.recommendations.isNotEmpty()) {
                    Text("Recommendations", style = MaterialTheme.typography.titleSmall)
                    result.recommendations.forEach { rec ->
                        Text("• $rec", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun FindingCard(finding: InspectionFinding) {
    val containerColor = when (finding.severity) {
        FindingSeverity.CRITICAL -> MaterialTheme.colorScheme.errorContainer
        FindingSeverity.HIGH -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)
        FindingSeverity.MEDIUM -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(finding.type, style = MaterialTheme.typography.titleSmall)
            Text(finding.description, style = MaterialTheme.typography.bodySmall)
            Text(
                text = "Confidence: ${(finding.confidence * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
