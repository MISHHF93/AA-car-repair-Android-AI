package com.aa.carrepair.feature.calculators

import com.aa.carrepair.domain.model.CalculatorType
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CalculatorTest {

    private lateinit var viewModel: CalculatorViewModel

    @Before
    fun setUp() {
        viewModel = CalculatorViewModel(mockk(relaxed = true))
    }

    @Test
    fun `labor time calculation is correct`() {
        val inputs = mapOf("hours" to 2.5, "rate" to 100.0)
        val result = viewModel.calculate(CalculatorType.LABOR_TIME, inputs)
        assertEquals(250.0, result["labor_cost"]!!, 0.01)
    }

    @Test
    fun `parts markup calculation is correct`() {
        val inputs = mapOf("cost" to 100.0, "markup_percent" to 40.0)
        val result = viewModel.calculate(CalculatorType.PARTS_MARKUP, inputs)
        assertEquals(140.0, result["selling_price"]!!, 0.01)
        assertEquals(40.0, result["gross_profit"]!!, 0.01)
    }

    @Test
    fun `repair replace recommends repair when below threshold`() {
        val inputs = mapOf(
            "repair_cost" to 1000.0,
            "vehicle_value" to 10000.0,
            "monthly_depreciation" to 200.0
        )
        val result = viewModel.calculate(CalculatorType.REPAIR_REPLACE, inputs)
        assertEquals(0.0, result["recommendation"]!!, 0.01) // 0 = repair
    }

    @Test
    fun `repair replace recommends replace when above threshold`() {
        val inputs = mapOf(
            "repair_cost" to 6000.0,
            "vehicle_value" to 10000.0,
            "monthly_depreciation" to 200.0
        )
        val result = viewModel.calculate(CalculatorType.REPAIR_REPLACE, inputs)
        assertEquals(1.0, result["recommendation"]!!, 0.01) // 1 = replace
    }

    @Test
    fun `break even calculation`() {
        val inputs = mapOf(
            "fixed_costs" to 10000.0,
            "variable_cost_per_unit" to 50.0,
            "selling_price_per_unit" to 100.0
        )
        val result = viewModel.calculate(CalculatorType.BREAK_EVEN, inputs)
        assertEquals(200.0, result["break_even_units"]!!, 0.01)
        assertEquals(20000.0, result["break_even_revenue"]!!, 0.01)
    }

    @Test
    fun `warranty roi calculates correctly`() {
        val inputs = mapOf(
            "warranty_cost" to 500.0,
            "avg_repair_cost" to 1000.0,
            "repair_probability_percent" to 60.0
        )
        val result = viewModel.calculate(CalculatorType.WARRANTY_ROI, inputs)
        assertEquals(600.0, result["expected_value"]!!, 0.01)
        assertTrue(result["is_worthwhile"]!! > 0)
    }

    @Test
    fun `battery health flags replacement when voltage low`() {
        val inputs = mapOf(
            "voltage_v" to 11.8,
            "cca_measured" to 400.0,
            "original_cca" to 600.0,
            "age_years" to 5.0
        )
        val result = viewModel.calculate(CalculatorType.BATTERY_HEALTH, inputs)
        assertEquals(1.0, result["replacement_recommended"]!!, 0.01)
    }

    @Test
    fun `shop efficiency reports correctly`() {
        val inputs = mapOf(
            "billed_hours" to 80.0,
            "available_hours" to 100.0,
            "total_revenue" to 10000.0,
            "labor_cost" to 4000.0,
            "tech_count" to 2.0
        )
        val result = viewModel.calculate(CalculatorType.SHOP_EFFICIENCY, inputs)
        assertEquals(80.0, result["efficiency_percent"]!!, 0.01)
        assertEquals(6000.0, result["labor_gross_profit"]!!, 0.01)
        assertEquals(5000.0, result["revenue_per_tech"]!!, 0.01)
    }

    @Test
    fun `fleet cost calculates annual total`() {
        val inputs = mapOf(
            "vehicle_count" to 10.0,
            "avg_maintenance_cost" to 500.0,
            "fuel_cost_per_month" to 300.0,
            "insurance_cost" to 200.0
        )
        val result = viewModel.calculate(CalculatorType.FLEET_COST, inputs)
        assertEquals(10000.0, result["monthly_total"]!!, 0.01)
        assertEquals(120000.0, result["annual_total"]!!, 0.01)
    }

    @Test
    fun `tire wear calculates remaining miles`() {
        val inputs = mapOf(
            "current_tread_depth_32nds" to 6.0,
            "original_tread_depth_32nds" to 10.0,
            "mileage_on_tires" to 20000.0
        )
        val result = viewModel.calculate(CalculatorType.TIRE_WEAR, inputs)
        assertTrue(result["estimated_remaining_miles"]!! > 0)
    }

    @Test
    fun `co2 impact calculates savings`() {
        val inputs = mapOf(
            "miles_per_year" to 12000.0,
            "mpg" to 25.0,
            "improved_mpg" to 30.0
        )
        val result = viewModel.calculate(CalculatorType.CO2_IMPACT, inputs)
        assertTrue(result["fuel_savings_gallons"]!! > 0)
    }
}
