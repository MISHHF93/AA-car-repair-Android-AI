package com.aa.carrepair.domain.model

import java.time.Instant

data class CalculationResult(
    val id: String,
    val type: CalculatorType,
    val inputs: Map<String, Double>,
    val outputs: Map<String, Double>,
    val notes: String? = null,
    val savedAt: Instant = Instant.now()
)

enum class CalculatorType {
    LABOR_TIME,
    PARTS_MARKUP,
    REPAIR_REPLACE,
    MAINTENANCE_SCHEDULE,
    FLEET_COST,
    DIAGNOSTIC_CONFIDENCE,
    CO2_IMPACT,
    BREAK_EVEN,
    WARRANTY_ROI,
    OBD_ANALYZER,
    TIRE_WEAR,
    BATTERY_HEALTH,
    COOLANT_PRESSURE,
    SHOP_EFFICIENCY
}
