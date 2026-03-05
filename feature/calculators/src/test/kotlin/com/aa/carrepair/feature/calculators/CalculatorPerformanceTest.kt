package com.aa.carrepair.feature.calculators

import com.aa.carrepair.domain.model.CalculatorType
import io.mockk.mockk
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Performance tests for the 14 calculator types.
 * Ensures all calculations complete rapidly for real-time UI updates.
 */
class CalculatorPerformanceTest {

    private lateinit var viewModel: CalculatorViewModel

    @Before
    fun setUp() {
        viewModel = CalculatorViewModel(mockk(relaxed = true))
    }

    @Test
    fun `all 14 calculators complete 10_000 iterations within 3 seconds`() {
        val testCases = mapOf(
            CalculatorType.LABOR_TIME to mapOf("hours" to 2.5, "rate" to 100.0),
            CalculatorType.PARTS_MARKUP to mapOf("cost" to 100.0, "markup_percent" to 40.0),
            CalculatorType.REPAIR_REPLACE to mapOf("repair_cost" to 1000.0, "vehicle_value" to 10000.0, "monthly_depreciation" to 200.0),
            CalculatorType.MAINTENANCE_SCHEDULE to mapOf("current_mileage" to 50000.0, "last_oil_change_mileage" to 45000.0, "oil_change_interval" to 5000.0),
            CalculatorType.FLEET_COST to mapOf("vehicle_count" to 10.0, "avg_maintenance_cost" to 500.0, "fuel_cost_per_month" to 300.0, "insurance_cost" to 200.0),
            CalculatorType.DIAGNOSTIC_CONFIDENCE to mapOf("symptoms_matched" to 3.0, "total_symptoms" to 5.0, "dtc_codes_present" to 2.0, "history_match_percent" to 70.0),
            CalculatorType.CO2_IMPACT to mapOf("miles_per_year" to 12000.0, "mpg" to 25.0),
            CalculatorType.BREAK_EVEN to mapOf("fixed_costs" to 10000.0, "variable_cost_per_unit" to 50.0, "selling_price_per_unit" to 100.0),
            CalculatorType.WARRANTY_ROI to mapOf("warranty_cost" to 500.0, "avg_repair_cost" to 1000.0, "repair_probability_percent" to 60.0),
            CalculatorType.OBD_ANALYZER to mapOf("engine_load_percent" to 50.0, "coolant_temp_f" to 195.0, "fuel_trim_short_pct" to 2.0, "fuel_trim_long_pct" to 3.0),
            CalculatorType.TIRE_WEAR to mapOf("current_tread_depth_32nds" to 6.0, "original_tread_depth_32nds" to 10.0, "mileage_on_tires" to 20000.0),
            CalculatorType.BATTERY_HEALTH to mapOf("voltage_v" to 12.4, "cca_measured" to 450.0, "original_cca" to 600.0, "age_years" to 4.0),
            CalculatorType.COOLANT_PRESSURE to mapOf("system_pressure_psi" to 15.0, "cap_rating_psi" to 16.0, "ambient_temp_f" to 85.0),
            CalculatorType.SHOP_EFFICIENCY to mapOf("billed_hours" to 80.0, "available_hours" to 100.0, "total_revenue" to 10000.0, "labor_cost" to 4000.0, "tech_count" to 2.0)
        )
        val iterations = 10_000

        val startTime = System.nanoTime()
        repeat(iterations) {
            for ((type, inputs) in testCases) {
                viewModel.calculate(type, inputs)
            }
        }
        val elapsedMs = (System.nanoTime() - startTime) / 1_000_000

        assertTrue(
            "All 14 calculators × 10k iterations took ${elapsedMs}ms (expected < 3000ms)",
            elapsedMs < 3000
        )
    }

    @Test
    fun `single calculator average latency under 10 microseconds`() {
        val inputs = mapOf("hours" to 2.5, "rate" to 100.0)
        val iterations = 50_000

        // Warm up
        repeat(100) { viewModel.calculate(CalculatorType.LABOR_TIME, inputs) }

        val startTime = System.nanoTime()
        repeat(iterations) {
            viewModel.calculate(CalculatorType.LABOR_TIME, inputs)
        }
        val avgMicros = (System.nanoTime() - startTime) / 1_000 / iterations

        assertTrue(
            "Average labor time calc latency was ${avgMicros}μs (expected < 10μs)",
            avgMicros < 10
        )
    }

    @Test
    fun `most complex calculator (OBD) averages under 20 microseconds`() {
        val inputs = mapOf(
            "engine_load_percent" to 75.0,
            "coolant_temp_f" to 210.0,
            "fuel_trim_short_pct" to 5.0,
            "fuel_trim_long_pct" to 8.0
        )
        val iterations = 50_000

        repeat(100) { viewModel.calculate(CalculatorType.OBD_ANALYZER, inputs) }

        val startTime = System.nanoTime()
        repeat(iterations) {
            viewModel.calculate(CalculatorType.OBD_ANALYZER, inputs)
        }
        val avgMicros = (System.nanoTime() - startTime) / 1_000 / iterations

        assertTrue(
            "Average OBD calc latency was ${avgMicros}μs (expected < 20μs)",
            avgMicros < 20
        )
    }

    @Test
    fun `fleet cost calculator handles large fleet sizes efficiently`() {
        val iterations = 10_000
        val inputs = mapOf(
            "vehicle_count" to 500.0, // max fleet size
            "avg_maintenance_cost" to 500.0,
            "fuel_cost_per_month" to 300.0,
            "insurance_cost" to 200.0
        )

        val startTime = System.nanoTime()
        repeat(iterations) {
            viewModel.calculate(CalculatorType.FLEET_COST, inputs)
        }
        val elapsedMs = (System.nanoTime() - startTime) / 1_000_000

        assertTrue(
            "Fleet cost calculator with 500 vehicles × 10k iterations took ${elapsedMs}ms (expected < 500ms)",
            elapsedMs < 500
        )
    }

    @Test
    fun `break even calculator with extreme values performs well`() {
        val iterations = 10_000
        val inputs = mapOf(
            "fixed_costs" to 1_000_000.0,
            "variable_cost_per_unit" to 0.01,
            "selling_price_per_unit" to 0.02
        )

        val startTime = System.nanoTime()
        repeat(iterations) {
            viewModel.calculate(CalculatorType.BREAK_EVEN, inputs)
        }
        val elapsedMs = (System.nanoTime() - startTime) / 1_000_000

        assertTrue(
            "Break-even with extreme values took ${elapsedMs}ms (expected < 500ms)",
            elapsedMs < 500
        )
    }

    @Test
    fun `all calculators produce valid results under load`() {
        val iterations = 1_000
        var totalCalculations = 0

        val startTime = System.nanoTime()
        repeat(iterations) {
            for (type in CalculatorType.values()) {
                val inputs = mapOf(
                    "hours" to 2.0, "rate" to 100.0,
                    "cost" to 50.0, "markup_percent" to 40.0,
                    "repair_cost" to 1000.0, "vehicle_value" to 10000.0,
                    "monthly_depreciation" to 200.0,
                    "current_mileage" to 50000.0, "last_oil_change_mileage" to 45000.0,
                    "oil_change_interval" to 5000.0,
                    "vehicle_count" to 5.0, "avg_maintenance_cost" to 300.0,
                    "fuel_cost_per_month" to 200.0, "insurance_cost" to 100.0,
                    "symptoms_matched" to 3.0, "total_symptoms" to 5.0,
                    "dtc_codes_present" to 1.0, "history_match_percent" to 50.0,
                    "miles_per_year" to 12000.0, "mpg" to 25.0,
                    "fixed_costs" to 5000.0, "variable_cost_per_unit" to 50.0,
                    "selling_price_per_unit" to 100.0,
                    "warranty_cost" to 400.0, "avg_repair_cost" to 800.0,
                    "repair_probability_percent" to 50.0,
                    "engine_load_percent" to 50.0, "coolant_temp_f" to 195.0,
                    "fuel_trim_short_pct" to 1.0, "fuel_trim_long_pct" to 2.0,
                    "current_tread_depth_32nds" to 7.0, "original_tread_depth_32nds" to 10.0,
                    "mileage_on_tires" to 15000.0,
                    "voltage_v" to 12.5, "cca_measured" to 500.0,
                    "original_cca" to 600.0, "age_years" to 3.0,
                    "system_pressure_psi" to 15.0, "cap_rating_psi" to 16.0,
                    "ambient_temp_f" to 80.0,
                    "billed_hours" to 75.0, "available_hours" to 100.0,
                    "total_revenue" to 9000.0, "labor_cost" to 4000.0,
                    "tech_count" to 2.0
                )
                val result = viewModel.calculate(type, inputs)
                assertTrue("$type produced empty results", result.isNotEmpty())
                totalCalculations++
            }
        }
        val elapsedMs = (System.nanoTime() - startTime) / 1_000_000

        assertTrue(
            "$totalCalculations calculations took ${elapsedMs}ms (expected < 3000ms)",
            elapsedMs < 3000
        )
    }
}
