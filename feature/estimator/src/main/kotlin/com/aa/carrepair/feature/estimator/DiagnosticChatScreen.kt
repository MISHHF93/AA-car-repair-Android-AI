package com.aa.carrepair.feature.estimator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aa.carrepair.feature.estimator.R

@Composable
fun DiagnosticChatScreen(
    onEstimateReady: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: EstimatorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            com.aa.carrepair.ui.components.AATopAppBar(
                title = stringResource(R.string.estimator_step_diagnostic),
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
            Text(
                text = stringResource(R.string.estimator_describe_issue),
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = uiState.issueDescription,
                onValueChange = viewModel::onIssueDescriptionChanged,
                label = { Text(stringResource(R.string.estimator_issue_hint)) },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                maxLines = 6
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.estimator_oem_parts),
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = uiState.preferOem,
                    onCheckedChange = viewModel::onOemToggled
                )
            }

            uiState.error?.let { error ->
                Text(text = error, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.weight(1f))

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                Text(
                    text = stringResource(R.string.estimator_generating),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                Button(
                    onClick = {
                        viewModel.generateEstimate()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = uiState.issueDescription.isNotBlank()
                ) {
                    Text(stringResource(R.string.action_submit))
                }
            }
        }
    }
}
