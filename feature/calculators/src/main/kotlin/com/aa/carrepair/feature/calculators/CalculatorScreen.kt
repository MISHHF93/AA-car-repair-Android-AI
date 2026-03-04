package com.aa.carrepair.feature.calculators

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.aa.carrepair.domain.model.CalculatorType

@Composable
fun CalculatorScreen(
    calculatorType: String,
    onNavigateBack: () -> Unit,
    viewModel: CalculatorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val type = runCatching { CalculatorType.valueOf(calculatorType) }.getOrElse { CalculatorType.LABOR_TIME }
    val calcInfo = CALCULATORS.firstOrNull { it.type == type }

    Scaffold(
        topBar = {
            com.aa.carrepair.ui.components.AATopAppBar(
                title = calcInfo?.name ?: "Calculator",
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
            getInputFields(type).forEach { field ->
                OutlinedTextField(
                    value = uiState.inputs[field.key] ?: "",
                    onValueChange = { viewModel.onInputChanged(field.key, it) },
                    label = { Text(field.label) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
            }

            Button(
                onClick = { viewModel.runCalculation(type) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Calculate")
            }

            uiState.result?.let { result ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Results", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        result.outputs.forEach { (key, value) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = key.replace("_", " ").replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = String.format("%.2f", value),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }

                Button(
                    onClick = { viewModel.saveResult() },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isSaved
                ) {
                    Text(if (uiState.isSaved) "Saved" else "Save Result")
                }
            }
        }
    }
}

data class InputField(val key: String, val label: String)

private fun getInputFields(type: CalculatorType): List<InputField> = when (type) {
    CalculatorType.LABOR_TIME -> listOf(
        InputField("hours", "Hours"),
        InputField("rate", "Hourly Rate (\$)")
    )
    CalculatorType.PARTS_MARKUP -> listOf(
        InputField("cost", "Part Cost (\$)"),
        InputField("markup_percent", "Markup (%)")
    )
    CalculatorType.REPAIR_REPLACE -> listOf(
        InputField("repair_cost", "Repair Cost (\$)"),
        InputField("vehicle_value", "Vehicle Value (\$)"),
        InputField("monthly_depreciation", "Monthly Depreciation (\$)")
    )
    CalculatorType.MAINTENANCE_SCHEDULE -> listOf(
        InputField("current_mileage", "Current Mileage"),
        InputField("last_oil_change_mileage", "Last Oil Change Mileage"),
        InputField("oil_change_interval", "Oil Change Interval (miles)")
    )
    CalculatorType.FLEET_COST -> listOf(
        InputField("vehicle_count", "Number of Vehicles"),
        InputField("avg_maintenance_cost", "Avg Monthly Maintenance (\$)"),
        InputField("fuel_cost_per_month", "Monthly Fuel Cost (\$)"),
        InputField("insurance_cost", "Monthly Insurance (\$)")
    )
    CalculatorType.DIAGNOSTIC_CONFIDENCE -> listOf(
        InputField("symptoms_matched", "Symptoms Matched"),
        InputField("total_symptoms", "Total Symptoms"),
        InputField("dtc_codes_present", "DTC Codes Present"),
        InputField("history_match_percent", "History Match (%)")
    )
    CalculatorType.CO2_IMPACT -> listOf(
        InputField("miles_per_year", "Miles Per Year"),
        InputField("mpg", "Current MPG"),
        InputField("improved_mpg", "Improved MPG (after repair)")
    )
    CalculatorType.BREAK_EVEN -> listOf(
        InputField("fixed_costs", "Fixed Costs (\$)"),
        InputField("variable_cost_per_unit", "Variable Cost Per Unit (\$)"),
        InputField("selling_price_per_unit", "Selling Price Per Unit (\$)")
    )
    CalculatorType.WARRANTY_ROI -> listOf(
        InputField("warranty_cost", "Warranty Cost (\$)"),
        InputField("avg_repair_cost", "Average Repair Cost (\$)"),
        InputField("repair_probability_percent", "Repair Probability (%)")
    )
    CalculatorType.OBD_ANALYZER -> listOf(
        InputField("engine_load_percent", "Engine Load (%)"),
        InputField("coolant_temp_f", "Coolant Temp (°F)"),
        InputField("fuel_trim_short_pct", "Short-Term Fuel Trim (%)"),
        InputField("fuel_trim_long_pct", "Long-Term Fuel Trim (%)")
    )
    CalculatorType.TIRE_WEAR -> listOf(
        InputField("current_tread_depth_32nds", "Current Tread Depth (32nds)"),
        InputField("original_tread_depth_32nds", "Original Tread Depth (32nds)"),
        InputField("mileage_on_tires", "Miles on Tires")
    )
    CalculatorType.BATTERY_HEALTH -> listOf(
        InputField("voltage_v", "Battery Voltage (V)"),
        InputField("cca_measured", "Measured CCA"),
        InputField("original_cca", "Original CCA Rating"),
        InputField("age_years", "Battery Age (years)")
    )
    CalculatorType.COOLANT_PRESSURE -> listOf(
        InputField("system_pressure_psi", "System Pressure (PSI)"),
        InputField("cap_rating_psi", "Radiator Cap Rating (PSI)"),
        InputField("ambient_temp_f", "Ambient Temp (°F)"),
        InputField("antifreeze_concentration_pct", "Antifreeze Concentration (%)")
    )
    CalculatorType.SHOP_EFFICIENCY -> listOf(
        InputField("billed_hours", "Billed Hours"),
        InputField("available_hours", "Available Hours"),
        InputField("total_revenue", "Total Revenue (\$)"),
        InputField("labor_cost", "Labor Cost (\$)"),
        InputField("tech_count", "Number of Technicians")
    )
}
