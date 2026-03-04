package com.aa.carrepair.feature.estimator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
fun VehicleIdScreen(
    onVehicleIdentified: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: EstimatorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            com.aa.carrepair.ui.components.AATopAppBar(
                title = stringResource(R.string.estimator_step_vehicle),
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
                text = stringResource(R.string.estimator_vin_label),
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = uiState.vinInput,
                onValueChange = viewModel::onVinChanged,
                label = { Text(stringResource(R.string.estimator_vin_hint)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = uiState.error != null
            )

            uiState.error?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            uiState.selectedVehicle?.let { vehicle ->
                Text(
                    text = vehicle.displayName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                Button(
                    onClick = {
                        if (uiState.selectedVehicle != null) {
                            viewModel.proceedToCategory()
                            onVehicleIdentified()
                        } else {
                            viewModel.decodeVin()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (uiState.selectedVehicle != null)
                            stringResource(R.string.action_next)
                        else
                            stringResource(R.string.estimator_vin_decode)
                    )
                }
            }
        }
    }
}
