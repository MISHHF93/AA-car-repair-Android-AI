package com.aa.carrepair.feature.calculators

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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aa.carrepair.domain.model.CalculatorType

data class CalculatorInfo(
    val type: CalculatorType,
    val name: String,
    val description: String
)

internal val CALCULATORS = listOf(
    CalculatorInfo(CalculatorType.LABOR_TIME, "Labor Time", "Calculate labor costs"),
    CalculatorInfo(CalculatorType.PARTS_MARKUP, "Parts Markup", "Price parts with markup"),
    CalculatorInfo(CalculatorType.REPAIR_REPLACE, "Repair vs Replace", "Should you fix or replace?"),
    CalculatorInfo(CalculatorType.MAINTENANCE_SCHEDULE, "Maintenance", "Service interval calculator"),
    CalculatorInfo(CalculatorType.FLEET_COST, "Fleet Cost", "Fleet TCO analysis"),
    CalculatorInfo(CalculatorType.DIAGNOSTIC_CONFIDENCE, "Diagnostic Confidence", "Confidence scoring"),
    CalculatorInfo(CalculatorType.CO2_IMPACT, "CO\u2082 Impact", "Emissions calculator"),
    CalculatorInfo(CalculatorType.BREAK_EVEN, "Break-Even", "Profitability analysis"),
    CalculatorInfo(CalculatorType.WARRANTY_ROI, "Warranty ROI", "Warranty value analysis"),
    CalculatorInfo(CalculatorType.OBD_ANALYZER, "OBD Analyzer", "Live data analysis"),
    CalculatorInfo(CalculatorType.TIRE_WEAR, "Tire Wear", "Tread life estimator"),
    CalculatorInfo(CalculatorType.BATTERY_HEALTH, "Battery Health", "Battery diagnostics"),
    CalculatorInfo(CalculatorType.COOLANT_PRESSURE, "Coolant Pressure", "Cooling system analysis"),
    CalculatorInfo(CalculatorType.SHOP_EFFICIENCY, "Shop Efficiency", "KPI dashboard")
)

@Composable
fun CalculatorHubScreen(
    onCalculatorSelected: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            com.aa.carrepair.ui.components.AATopAppBar(
                title = "Calculators",
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
            items(CALCULATORS) { calc ->
                CalculatorCard(
                    info = calc,
                    onClick = { onCalculatorSelected(calc.type.name) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalculatorCard(info: CalculatorInfo, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "${info.name} calculator" },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = info.name,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Text(
                text = info.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f),
                textAlign = TextAlign.Start
            )
        }
    }
}
