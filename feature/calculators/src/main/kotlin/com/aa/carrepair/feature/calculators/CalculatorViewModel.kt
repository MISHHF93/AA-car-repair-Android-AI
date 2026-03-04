package com.aa.carrepair.feature.calculators

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aa.carrepair.core.result.DataResult
import com.aa.carrepair.domain.model.CalculationResult
import com.aa.carrepair.domain.model.CalculatorType
import com.aa.carrepair.domain.repository.CalculatorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

data class CalculatorUiState(
    val inputs: Map<String, String> = emptyMap(),
    val result: CalculationResult? = null,
    val error: String? = null,
    val isSaved: Boolean = false
)

@HiltViewModel
class CalculatorViewModel @Inject constructor(
    private val calculatorRepository: CalculatorRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    fun onInputChanged(key: String, value: String) {
        _uiState.update { it.copy(inputs = it.inputs + (key to value), error = null) }
    }

    fun calculate(type: CalculatorType, inputs: Map<String, Double>): Map<String, Double> {
        return when (type) {
            CalculatorType.LABOR_TIME -> calculateLaborTime(inputs)
            CalculatorType.PARTS_MARKUP -> calculatePartsMarkup(inputs)
            CalculatorType.REPAIR_REPLACE -> calculateRepairReplace(inputs)
            CalculatorType.MAINTENANCE_SCHEDULE -> calculateMaintenanceSchedule(inputs)
            CalculatorType.FLEET_COST -> calculateFleetCost(inputs)
            CalculatorType.DIAGNOSTIC_CONFIDENCE -> calculateDiagnosticConfidence(inputs)
            CalculatorType.CO2_IMPACT -> calculateCo2Impact(inputs)
            CalculatorType.BREAK_EVEN -> calculateBreakEven(inputs)
            CalculatorType.WARRANTY_ROI -> calculateWarrantyRoi(inputs)
            CalculatorType.OBD_ANALYZER -> calculateObdAnalysis(inputs)
            CalculatorType.TIRE_WEAR -> calculateTireWear(inputs)
            CalculatorType.BATTERY_HEALTH -> calculateBatteryHealth(inputs)
            CalculatorType.COOLANT_PRESSURE -> calculateCoolantPressure(inputs)
            CalculatorType.SHOP_EFFICIENCY -> calculateShopEfficiency(inputs)
        }
    }

    fun runCalculation(type: CalculatorType) {
        val inputs = _uiState.value.inputs.mapValues { it.value.toDoubleOrNull() ?: 0.0 }
        val outputs = calculate(type, inputs)
        val result = CalculationResult(
            id = UUID.randomUUID().toString(),
            type = type,
            inputs = inputs,
            outputs = outputs,
            savedAt = Instant.now()
        )
        _uiState.update { it.copy(result = result) }
    }

    fun saveResult() {
        val result = _uiState.value.result ?: return
        viewModelScope.launch {
            when (calculatorRepository.saveCalculation(result)) {
                is DataResult.Success -> _uiState.update { it.copy(isSaved = true) }
                is DataResult.Error -> _uiState.update { it.copy(error = "Failed to save result") }
                is DataResult.Loading -> Unit
            }
        }
    }

    private fun calculateLaborTime(inputs: Map<String, Double>): Map<String, Double> {
        val hours = inputs["hours"] ?: 0.0
        val rate = inputs["rate"] ?: 125.0
        return mapOf(
            "labor_cost" to hours * rate,
            "with_overhead" to hours * rate * 1.15
        )
    }

    private fun calculatePartsMarkup(inputs: Map<String, Double>): Map<String, Double> {
        val cost = inputs["cost"] ?: 0.0
        val markup = inputs["markup_percent"] ?: 40.0
        val sellingPrice = cost * (1 + markup / 100)
        return mapOf(
            "selling_price" to sellingPrice,
            "gross_profit" to sellingPrice - cost,
            "gross_margin_percent" to (sellingPrice - cost) / sellingPrice * 100
        )
    }

    private fun calculateRepairReplace(inputs: Map<String, Double>): Map<String, Double> {
        val repairCost = inputs["repair_cost"] ?: 0.0
        val vehicleValue = inputs["vehicle_value"] ?: 0.0
        val monthlyDepreciation = inputs["monthly_depreciation"] ?: 200.0
        val repairRatio = if (vehicleValue > 0) repairCost / vehicleValue else 0.0
        val monthsOfDepreciation = if (monthlyDepreciation > 0) repairCost / monthlyDepreciation else 0.0
        return mapOf(
            "repair_to_value_ratio" to repairRatio * 100,
            "months_of_depreciation_covered" to monthsOfDepreciation,
            "recommendation" to if (repairRatio > 0.5) 1.0 else 0.0 // 1 = replace, 0 = repair
        )
    }

    private fun calculateMaintenanceSchedule(inputs: Map<String, Double>): Map<String, Double> {
        val mileage = inputs["current_mileage"] ?: 0.0
        val lastOilChangeMileage = inputs["last_oil_change_mileage"] ?: 0.0
        val oilChangeInterval = inputs["oil_change_interval"] ?: 5000.0
        val milesSinceOilChange = mileage - lastOilChangeMileage
        val milesUntilOilChange = oilChangeInterval - milesSinceOilChange
        return mapOf(
            "miles_since_oil_change" to milesSinceOilChange,
            "miles_until_oil_change" to maxOf(0.0, milesUntilOilChange),
            "oil_change_overdue" to if (milesSinceOilChange > oilChangeInterval) 1.0 else 0.0,
            "next_tire_rotation_miles" to maxOf(0.0, 7500 - (mileage % 7500))
        )
    }

    private fun calculateFleetCost(inputs: Map<String, Double>): Map<String, Double> {
        val vehicles = inputs["vehicle_count"] ?: 1.0
        val avgMaintenanceCost = inputs["avg_maintenance_cost"] ?: 0.0
        val fuelCostPerMonth = inputs["fuel_cost_per_month"] ?: 0.0
        val insuranceCost = inputs["insurance_cost"] ?: 0.0
        val monthlyTotal = (avgMaintenanceCost + fuelCostPerMonth + insuranceCost) * vehicles
        return mapOf(
            "monthly_total" to monthlyTotal,
            "annual_total" to monthlyTotal * 12,
            "cost_per_vehicle_monthly" to monthlyTotal / vehicles,
            "cost_per_vehicle_annual" to monthlyTotal * 12 / vehicles
        )
    }

    private fun calculateDiagnosticConfidence(inputs: Map<String, Double>): Map<String, Double> {
        val symptomsMatched = inputs["symptoms_matched"] ?: 0.0
        val totalSymptoms = inputs["total_symptoms"] ?: 1.0
        val dtcCodesPresent = inputs["dtc_codes_present"] ?: 0.0
        val historyMatch = inputs["history_match_percent"] ?: 0.0
        val confidence = (symptomsMatched / totalSymptoms * 0.4 +
            minOf(dtcCodesPresent / 3, 1.0) * 0.4 +
            historyMatch / 100 * 0.2) * 100
        return mapOf("confidence_percent" to minOf(confidence, 99.0))
    }

    private fun calculateCo2Impact(inputs: Map<String, Double>): Map<String, Double> {
        val milesPerYear = inputs["miles_per_year"] ?: 12000.0
        val mpg = inputs["mpg"] ?: 25.0
        val gallonsPerYear = milesPerYear / mpg
        val co2PerGallon = 8.887
        val co2Lbs = gallonsPerYear * co2PerGallon
        val improvedMpg = inputs["improved_mpg"] ?: (mpg * 1.1)
        val improvedCo2 = (milesPerYear / improvedMpg) * co2PerGallon
        return mapOf(
            "co2_kg_per_year" to co2Lbs * 0.4536,
            "co2_reduction_percent" to (co2Lbs - improvedCo2) / co2Lbs * 100,
            "fuel_savings_gallons" to gallonsPerYear - (milesPerYear / improvedMpg)
        )
    }

    private fun calculateBreakEven(inputs: Map<String, Double>): Map<String, Double> {
        val fixedCosts = inputs["fixed_costs"] ?: 0.0
        val variableCostPerUnit = inputs["variable_cost_per_unit"] ?: 0.0
        val sellingPricePerUnit = inputs["selling_price_per_unit"] ?: 1.0
        val contributionMargin = sellingPricePerUnit - variableCostPerUnit
        val breakEvenUnits = if (contributionMargin > 0) fixedCosts / contributionMargin else 0.0
        return mapOf(
            "break_even_units" to breakEvenUnits,
            "break_even_revenue" to breakEvenUnits * sellingPricePerUnit,
            "contribution_margin" to contributionMargin,
            "contribution_margin_ratio" to contributionMargin / sellingPricePerUnit * 100
        )
    }

    private fun calculateWarrantyRoi(inputs: Map<String, Double>): Map<String, Double> {
        val warrantyCost = inputs["warranty_cost"] ?: 0.0
        val avgRepairCost = inputs["avg_repair_cost"] ?: 0.0
        val repairProbability = inputs["repair_probability_percent"] ?: 50.0
        val expectedValue = avgRepairCost * (repairProbability / 100)
        val roi = if (warrantyCost > 0) (expectedValue - warrantyCost) / warrantyCost * 100 else 0.0
        return mapOf(
            "expected_value" to expectedValue,
            "net_benefit" to expectedValue - warrantyCost,
            "roi_percent" to roi,
            "is_worthwhile" to if (expectedValue > warrantyCost) 1.0 else 0.0
        )
    }

    private fun calculateObdAnalysis(inputs: Map<String, Double>): Map<String, Double> {
        val engineLoad = inputs["engine_load_percent"] ?: 50.0
        val coolantTemp = inputs["coolant_temp_f"] ?: 195.0
        val fuelTrimShortPct = inputs["fuel_trim_short_pct"] ?: 0.0
        val fuelTrimLongPct = inputs["fuel_trim_long_pct"] ?: 0.0
        val healthScore = 100 -
            (if (engineLoad > 80) 20.0 else 0.0) -
            (if (coolantTemp > 220) 30.0 else 0.0) -
            minOf(Math.abs(fuelTrimShortPct) * 2, 20.0) -
            minOf(Math.abs(fuelTrimLongPct) * 2, 20.0)
        return mapOf(
            "engine_health_score" to maxOf(0.0, healthScore),
            "fuel_system_status" to if (Math.abs(fuelTrimLongPct) > 10) 1.0 else 0.0,
            "overheating_risk" to if (coolantTemp > 215) 1.0 else 0.0
        )
    }

    private fun calculateTireWear(inputs: Map<String, Double>): Map<String, Double> {
        val currentTreadDepth = inputs["current_tread_depth_32nds"] ?: 8.0
        val originalTreadDepth = inputs["original_tread_depth_32nds"] ?: 10.0
        val mileageOnTires = inputs["mileage_on_tires"] ?: 0.0
        val wearRate = if (mileageOnTires > 0) (originalTreadDepth - currentTreadDepth) / mileageOnTires else 0.0
        val legalMinimum = 2.0
        val remainingTread = currentTreadDepth - legalMinimum
        val estimatedRemainingMiles = if (wearRate > 0) remainingTread / wearRate else 0.0
        return mapOf(
            "wear_percent" to (originalTreadDepth - currentTreadDepth) / originalTreadDepth * 100,
            "estimated_remaining_miles" to estimatedRemainingMiles,
            "replacement_recommended" to if (currentTreadDepth <= 4.0) 1.0 else 0.0
        )
    }

    private fun calculateBatteryHealth(inputs: Map<String, Double>): Map<String, Double> {
        val voltage = inputs["voltage_v"] ?: 12.6
        val coldCrankingAmps = inputs["cca_measured"] ?: 500.0
        val originalCca = inputs["original_cca"] ?: 600.0
        val ageYears = inputs["age_years"] ?: 3.0
        val voltageHealth = when {
            voltage >= 12.6 -> 100.0
            voltage >= 12.4 -> 75.0
            voltage >= 12.2 -> 50.0
            voltage >= 12.0 -> 25.0
            else -> 0.0
        }
        val ccaHealth = coldCrankingAmps / originalCca * 100
        val ageDerating = maxOf(0.0, 100 - ageYears * 10)
        val overallHealth = (voltageHealth * 0.4 + ccaHealth * 0.4 + ageDerating * 0.2)
        return mapOf(
            "health_percent" to overallHealth,
            "replacement_recommended" to if (overallHealth < 60 || voltage < 12.2) 1.0 else 0.0,
            "estimated_months_remaining" to maxOf(0.0, (overallHealth - 50) / 5)
        )
    }

    private fun calculateCoolantPressure(inputs: Map<String, Double>): Map<String, Double> {
        val systemPressure = inputs["system_pressure_psi"] ?: 15.0
        val capRating = inputs["cap_rating_psi"] ?: 16.0
        val ambientTemp = inputs["ambient_temp_f"] ?: 70.0
        val boilingPoint = 212 + systemPressure * 2.9
        val freezingPoint = if (inputs["antifreeze_concentration_pct"] != null) {
            32 - (inputs["antifreeze_concentration_pct"]!! / 100 * 80)
        } else -34.0
        return mapOf(
            "boiling_point_f" to boilingPoint,
            "freezing_point_f" to freezingPoint,
            "pressure_status" to if (systemPressure > capRating * 1.1) 1.0 else 0.0,
            "overheat_risk" to if (ambientTemp > 95 && systemPressure < capRating * 0.8) 1.0 else 0.0
        )
    }

    private fun calculateShopEfficiency(inputs: Map<String, Double>): Map<String, Double> {
        val billedHours = inputs["billed_hours"] ?: 0.0
        val availableHours = inputs["available_hours"] ?: 1.0
        val totalRevenue = inputs["total_revenue"] ?: 0.0
        val laborCost = inputs["labor_cost"] ?: 0.0
        val efficiency = billedHours / availableHours * 100
        val laborGrossProfit = totalRevenue - laborCost
        val laborGrossMargin = if (totalRevenue > 0) laborGrossProfit / totalRevenue * 100 else 0.0
        val revenuePerTech = if (inputs["tech_count"] != null && inputs["tech_count"]!! > 0) {
            totalRevenue / inputs["tech_count"]!!
        } else totalRevenue
        return mapOf(
            "efficiency_percent" to efficiency,
            "labor_gross_profit" to laborGrossProfit,
            "labor_gross_margin_percent" to laborGrossMargin,
            "revenue_per_tech" to revenuePerTech,
            "target_met" to if (efficiency >= 85) 1.0 else 0.0
        )
    }
}
