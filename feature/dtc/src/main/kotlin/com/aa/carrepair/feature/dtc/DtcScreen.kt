package com.aa.carrepair.feature.dtc

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aa.carrepair.domain.model.DtcCode
import com.aa.carrepair.feature.dtc.R

@Composable
fun DtcScreen(
    initialCode: String = "",
    onNavigateBack: () -> Unit,
    viewModel: DtcViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(initialCode) {
        if (initialCode.isNotBlank()) {
            viewModel.initWithCode(initialCode)
        }
    }

    Scaffold(
        topBar = {
            com.aa.carrepair.ui.components.AATopAppBar(
                title = stringResource(R.string.dtc_title),
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = viewModel::onSearchQueryChanged,
                label = { Text(stringResource(R.string.dtc_search_hint)) },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                singleLine = true
            )

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            uiState.selectedCode?.let { code ->
                DtcCodeDetail(dtcCode = code)
            }

            if (uiState.searchResults.isNotEmpty() && uiState.selectedCode == null) {
                LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
                    items(uiState.searchResults) { code ->
                        DtcSearchResult(
                            code = code,
                            onClick = { viewModel.analyzeCode(code.code) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DtcSearchResult(code: DtcCode, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(code.code, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
            Text(code.definition, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun DtcCodeDetail(dtcCode: DtcCode) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(dtcCode.code, style = MaterialTheme.typography.headlineSmall)
            Text(dtcCode.definition, style = MaterialTheme.typography.bodyLarge)

            com.aa.carrepair.ui.components.SafetyBadge(level = dtcCode.safetyLevel)
            com.aa.carrepair.ui.components.ConfidenceBadge(confidence = dtcCode.confidenceScore)

            if (dtcCode.symptoms.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.dtc_symptoms_label),
                    style = MaterialTheme.typography.titleSmall
                )
                dtcCode.symptoms.forEach { symptom ->
                    Text("• $symptom", style = MaterialTheme.typography.bodySmall)
                }
            }

            if (dtcCode.causes.isNotEmpty()) {
                Text(
                    text = stringResource(R.string.dtc_causes_label),
                    style = MaterialTheme.typography.titleSmall
                )
                dtcCode.causes.forEach { cause ->
                    Column {
                        Text("• ${cause.cause}", style = MaterialTheme.typography.bodySmall)
                        LinearProgressIndicator(
                            progress = { cause.probability.toFloat() },
                            modifier = Modifier.fillMaxWidth().height(4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}
