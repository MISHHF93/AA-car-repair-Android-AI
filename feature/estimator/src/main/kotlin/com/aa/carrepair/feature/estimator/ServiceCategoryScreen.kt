package com.aa.carrepair.feature.estimator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aa.carrepair.feature.estimator.R

private val SERVICE_CATEGORIES = listOf(
    "Engine & Drivetrain",
    "Transmission",
    "Brakes",
    "Suspension & Steering",
    "Electrical & Electronics",
    "A/C & Heating",
    "Exhaust System",
    "Fuel System",
    "Tires & Wheels",
    "Body & Glass",
    "Scheduled Maintenance",
    "Diagnostics",
    "Cooling System",
    "Safety Systems"
)

@Composable
fun ServiceCategoryScreen(
    onCategorySelected: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: EstimatorViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            com.aa.carrepair.ui.components.AATopAppBar(
                title = stringResource(R.string.estimator_step_category),
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            items(SERVICE_CATEGORIES) { category ->
                CategoryCard(
                    category = category,
                    onClick = {
                        viewModel.selectCategory(category)
                        onCategorySelected()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryCard(
    category: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Text(
            text = category,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp).fillMaxWidth()
        )
    }
}
