package com.aa.carrepair.domain.model

data class SafetyClassification(
    val level: SafetyLevel,
    val triggers: List<String>,
    val recommendedAction: String,
    val isDrivable: Boolean
)

enum class SafetyLevel {
    RESTRICTED,
    CRITICAL,
    HIGH,
    MEDIUM,
    LOW;

    val isDrivable: Boolean get() = this != CRITICAL && this != RESTRICTED
}
