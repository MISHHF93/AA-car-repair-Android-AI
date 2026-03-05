package com.aa.carrepair.feature.calculators

import com.aa.carrepair.domain.model.CalculatorType
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CalculatorViewModelAdditionalTest {

    private lateinit var viewModel: CalculatorViewModel

    @Before
    fun setUp() {
        viewModel = CalculatorViewModel(mockk(relaxed = true))
    }

    // ── Maintenance Schedule ────────────────────────────────────────────

    @Test
    fun `maintenance schedule shows oil change overdue`() {
        val inputs = mapOf(
            "current_mileage" to 60000.0,
            "last_oil_change_mileage" to 53000.0,
            "oil_change_interval" to 5000.0
        )
        val result = viewModel.calculate(CalculatorType.MAINTENANCE_SCHEDULE, inputs)
        assertEquals(7000.0, result["miles_since_oil_change"]!!, 0.01)
        assertEquals(1.0, result["oil_change_overdue"]!!, 0.01)
        assertEquals(0.0, result["miles_until_oil_change"]!!, 0.01)
    }

    @Test
    fun `maintenance schedule shows not overdue`() {
        val inputs = mapOf(
            "current_mileage" to 53000.0,
            "last_oil_change_mileage" to 51000.0,
            "oil_change_interval" to 5000.0
        )
        val result = viewModel.calculate(CalculatorType.MAINTENANCE_SCHEDULE, inputs)
        assertEquals(0.0, result["oil_change_overdue"]!!, 0.01)
        assertEquals(3000.0, result["miles_until_oil_change"]!!, 0.01)
    }

    @Test
    fun `maintenance schedule calculates tire rotation`() {
        val inputs = mapOf(
            "current_mileage" to 15000.0,
            "last_oil_change_mileage" to 14000.0,
            "oil_change_interval" to 5000.0
        )
        val result = viewModel.calculate(CalculatorType.MAINTENANCE_SCHEDULE, inputs)
        assertTrue(result.containsKey("next_tire_rotation_miles"))
        assertTrue(result["next_tire_rotation_miles"]!! >= 0)
    }

    // ── Diagnostic Confidence ───────────────────────────────────────────

    @Test
    fun `diagnostic confidence with all symptoms matched`() {
        val inputs = mapOf(
            "symptoms_matched" to 5.0,
            "total_symptoms" to 5.0,
            "dtc_codes_present" to 3.0,
            "history_match_percent" to 100.0
        )
        val result = viewModel.calculate(CalculatorType.DIAGNOSTIC_CONFIDENCE, inputs)
        assertTrue(result["confidence_percent"]!! <= 99.0)
        assertTrue(result["confidence_percent"]!! > 90.0)
    }

    @Test
    fun `diagnostic confidence is capped at 99`() {
        val inputs = mapOf(
            "symptoms_matched" to 10.0,
            "total_symptoms" to 10.0,
            "dtc_codes_present" to 10.0,
            "history_match_percent" to 100.0
        )
        val result = viewModel.calculate(CalculatorType.DIAGNOSTIC_CONFIDENCE, inputs)
        assertEquals(99.0, result["confidence_percent"]!!, 0.01)
    }

    @Test
    fun `diagnostic confidence zero for no matches`() {
        val inputs = mapOf(
            "symptoms_matched" to 0.0,
            "total_symptoms" to 5.0,
            "dtc_codes_present" to 0.0,
            "history_match_percent" to 0.0
        )
        val result = viewModel.calculate(CalculatorType.DIAGNOSTIC_CONFIDENCE, inputs)
        assertEquals(0.0, result["confidence_percent"]!!, 0.01)
    }

    // ── CO2 Impact ──────────────────────────────────────────────────────

    @Test
    fun `CO2 impact calculation produces positive values`() {
        val inputs = mapOf(
            "miles_per_year" to 12000.0,
            "mpg" to 25.0
        )
        val result = viewModel.calculate(CalculatorType.CO2_IMPACT, inputs)
        assertTrue(result["co2_kg_per_year"]!! > 0)
        assertTrue(result["co2_reduction_percent"]!! > 0)
        assertTrue(result["fuel_savings_gallons"]!! > 0)
    }

    @Test
    fun `CO2 impact with improved mpg shows savings`() {
        val inputs = mapOf(
            "miles_per_year" to 12000.0,
            "mpg" to 20.0,
            "improved_mpg" to 30.0
        )
        val result = viewModel.calculate(CalculatorType.CO2_IMPACT, inputs)
        assertTrue(result["co2_reduction_percent"]!! > 20)
    }

    // ── OBD Analyzer ────────────────────────────────────────────────────

    @Test
    fun `OBD analyzer healthy engine scores high`() {
        val inputs = mapOf(
            "engine_load_percent" to 50.0,
            "coolant_temp_f" to 195.0,
            "fuel_trim_short_pct" to 1.0,
            "fuel_trim_long_pct" to 1.0
        )
        val result = viewModel.calculate(CalculatorType.OBD_ANALYZER, inputs)
        assertTrue(result["engine_health_score"]!! > 90)
        assertEquals(0.0, result["overheating_risk"]!!, 0.01)
        assertEquals(0.0, result["fuel_system_status"]!!, 0.01)
    }

    @Test
    fun `OBD analyzer detects overheating`() {
        val inputs = mapOf(
            "engine_load_percent" to 50.0,
            "coolant_temp_f" to 230.0,
            "fuel_trim_short_pct" to 0.0,
            "fuel_trim_long_pct" to 0.0
        )
        val result = viewModel.calculate(CalculatorType.OBD_ANALYZER, inputs)
        assertEquals(1.0, result["overheating_risk"]!!, 0.01)
    }

    @Test
    fun `OBD analyzer detects fuel system issue`() {
        val inputs = mapOf(
            "engine_load_percent" to 50.0,
            "coolant_temp_f" to 195.0,
            "fuel_trim_short_pct" to 0.0,
            "fuel_trim_long_pct" to 15.0
        )
        val result = viewModel.calculate(CalculatorType.OBD_ANALYZER, inputs)
        assertEquals(1.0, result["fuel_system_status"]!!, 0.01)
    }

    // ── Coolant Pressure ────────────────────────────────────────────────

    @Test
    fun `coolant pressure calculates boiling point`() {
        val inputs = mapOf(
            "system_pressure_psi" to 15.0,
            "cap_rating_psi" to 16.0,
            "ambient_temp_f" to 70.0
        )
        val result = viewModel.calculate(CalculatorType.COOLANT_PRESSURE, inputs)
        assertTrue(result["boiling_point_f"]!! > 212)
        assertEquals(0.0, result["pressure_status"]!!, 0.01) // normal
    }

    @Test
    fun `coolant pressure detects over-pressure`() {
        val inputs = mapOf(
            "system_pressure_psi" to 20.0,
            "cap_rating_psi" to 16.0,
            "ambient_temp_f" to 70.0
        )
        val result = viewModel.calculate(CalculatorType.COOLANT_PRESSURE, inputs)
        assertEquals(1.0, result["pressure_status"]!!, 0.01)
    }

    @Test
    fun `coolant pressure detects overheat risk in hot weather`() {
        val inputs = mapOf(
            "system_pressure_psi" to 10.0,
            "cap_rating_psi" to 16.0,
            "ambient_temp_f" to 100.0
        )
        val result = viewModel.calculate(CalculatorType.COOLANT_PRESSURE, inputs)
        assertEquals(1.0, result["overheat_risk"]!!, 0.01)
    }

    // ── Battery Health ──────────────────────────────────────────────────

    @Test
    fun `battery health good battery scores high`() {
        val inputs = mapOf(
            "voltage_v" to 12.7,
            "cca_measured" to 580.0,
            "original_cca" to 600.0,
            "age_years" to 1.0
        )
        val result = viewModel.calculate(CalculatorType.BATTERY_HEALTH, inputs)
        assertTrue(result["health_percent"]!! > 80)
        assertEquals(0.0, result["replacement_recommended"]!!, 0.01)
    }

    @Test
    fun `battery health old battery with low voltage gets flagged`() {
        val inputs = mapOf(
            "voltage_v" to 11.5,
            "cca_measured" to 300.0,
            "original_cca" to 600.0,
            "age_years" to 6.0
        )
        val result = viewModel.calculate(CalculatorType.BATTERY_HEALTH, inputs)
        assertEquals(1.0, result["replacement_recommended"]!!, 0.01)
    }

    // ── Tire Wear ───────────────────────────────────────────────────────

    @Test
    fun `tire wear calculates replacement for worn tires`() {
        val inputs = mapOf(
            "current_tread_depth_32nds" to 3.0,
            "original_tread_depth_32nds" to 10.0,
            "mileage_on_tires" to 40000.0
        )
        val result = viewModel.calculate(CalculatorType.TIRE_WEAR, inputs)
        assertEquals(1.0, result["replacement_recommended"]!!, 0.01) // <= 4
    }

    @Test
    fun `tire wear healthy tires not flagged`() {
        val inputs = mapOf(
            "current_tread_depth_32nds" to 8.0,
            "original_tread_depth_32nds" to 10.0,
            "mileage_on_tires" to 10000.0
        )
        val result = viewModel.calculate(CalculatorType.TIRE_WEAR, inputs)
        assertEquals(0.0, result["replacement_recommended"]!!, 0.01)
        assertTrue(result["estimated_remaining_miles"]!! > 0)
    }

    // ── Shop Efficiency ─────────────────────────────────────────────────

    @Test
    fun `shop efficiency meets target at 85 percent or above`() {
        val inputs = mapOf(
            "billed_hours" to 85.0,
            "available_hours" to 100.0,
            "total_revenue" to 12000.0,
            "labor_cost" to 4000.0,
            "tech_count" to 2.0
        )
        val result = viewModel.calculate(CalculatorType.SHOP_EFFICIENCY, inputs)
        assertEquals(85.0, result["efficiency_percent"]!!, 0.01)
        assertEquals(1.0, result["target_met"]!!, 0.01)
    }

    @Test
    fun `shop efficiency below target`() {
        val inputs = mapOf(
            "billed_hours" to 60.0,
            "available_hours" to 100.0,
            "total_revenue" to 8000.0,
            "labor_cost" to 4000.0,
            "tech_count" to 2.0
        )
        val result = viewModel.calculate(CalculatorType.SHOP_EFFICIENCY, inputs)
        assertEquals(60.0, result["efficiency_percent"]!!, 0.01)
        assertEquals(0.0, result["target_met"]!!, 0.01)
    }

    // ── Edge cases / all calculators produce results ────────────────────

    @Test
    fun `all calculator types produce non-empty results`() {
        val testInputs = mapOf(
            CalculatorType.LABOR_TIME to mapOf("hours" to 1.0, "rate" to 100.0),
            CalculatorType.PARTS_MARKUP to mapOf("cost" to 100.0, "markup_percent" to 40.0),
            CalculatorType.REPAIR_REPLACE to mapOf("repair_cost" to 1000.0, "vehicle_value" to 10000.0),
            CalculatorType.MAINTENANCE_SCHEDULE to mapOf("current_mileage" to 50000.0, "last_oil_change_mileage" to 45000.0),
            CalculatorType.FLEET_COST to mapOf("vehicle_count" to 5.0, "avg_maintenance_cost" to 200.0),
            CalculatorType.DIAGNOSTIC_CONFIDENCE to mapOf("symptoms_matched" to 3.0, "total_symptoms" to 5.0),
            CalculatorType.CO2_IMPACT to mapOf("miles_per_year" to 12000.0, "mpg" to 25.0),
            CalculatorType.BREAK_EVEN to mapOf("fixed_costs" to 5000.0, "variable_cost_per_unit" to 50.0, "selling_price_per_unit" to 100.0),
            CalculatorType.WARRANTY_ROI to mapOf("warranty_cost" to 500.0, "avg_repair_cost" to 1000.0),
            CalculatorType.OBD_ANALYZER to mapOf("engine_load_percent" to 50.0, "coolant_temp_f" to 195.0),
            CalculatorType.TIRE_WEAR to mapOf("current_tread_depth_32nds" to 6.0, "original_tread_depth_32nds" to 10.0, "mileage_on_tires" to 20000.0),
            CalculatorType.BATTERY_HEALTH to mapOf("voltage_v" to 12.5, "cca_measured" to 500.0, "original_cca" to 600.0, "age_years" to 3.0),
            CalculatorType.COOLANT_PRESSURE to mapOf("system_pressure_psi" to 15.0, "cap_rating_psi" to 16.0, "ambient_temp_f" to 70.0),
            CalculatorType.SHOP_EFFICIENCY to mapOf("billed_hours" to 80.0, "available_hours" to 100.0, "total_revenue" to 10000.0, "labor_cost" to 4000.0)
        )

        for ((type, inputs) in testInputs) {
            val result = viewModel.calculate(type, inputs)
            assertTrue("Calculator $type returned empty results", result.isNotEmpty())
        }
    }
}
