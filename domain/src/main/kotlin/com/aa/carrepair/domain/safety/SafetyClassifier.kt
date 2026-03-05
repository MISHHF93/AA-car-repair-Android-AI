package com.aa.carrepair.domain.safety

import com.aa.carrepair.domain.model.SafetyClassification
import com.aa.carrepair.domain.model.SafetyLevel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SafetyClassifier @Inject constructor() {

    private val criticalKeywords = setOf(
        "brake failure", "no brakes", "brake line", "loss of steering", "steering failure",
        "tire blowout", "airbag", "srs", "fire", "smoke", "fuel leak", "gas leak",
        "engine seizure", "oil pressure", "do not drive", "unsafe to drive",
        "P0301", "P0302", "P0303", "P0304", "P0305", "P0306" // misfires on all cylinders
    )

    private val highKeywords = setOf(
        "brake wear", "abs fault", "traction control", "stability control",
        "power steering", "overheating", "coolant leak", "transmission slip",
        "suspension damage", "wheel bearing", "cv joint",
        "P0420", "P0430" // catalyst efficiency
    )

    private val mediumKeywords = setOf(
        "check engine", "service soon", "oil change", "battery weak",
        "tire pressure", "tpms", "oxygen sensor", "mass airflow",
        "egr", "evap", "P0171", "P0174", "P0300"
    )

    fun classify(content: String, dtcCodes: List<String> = emptyList()): SafetyClassification {
        val lowerContent = content.lowercase()
        val allText = (listOf(lowerContent) + dtcCodes.map { it.lowercase() }).joinToString(" ")

        val criticalTriggers = criticalKeywords.filter { allText.contains(it.lowercase()) }
        val highTriggers = highKeywords.filter { allText.contains(it.lowercase()) }
        val mediumTriggers = mediumKeywords.filter { allText.contains(it.lowercase()) }

        return when {
            criticalTriggers.isNotEmpty() -> SafetyClassification(
                level = SafetyLevel.CRITICAL,
                triggers = criticalTriggers,
                recommendedAction = "Stop driving immediately and contact a professional mechanic.",
                isDrivable = false
            )
            highTriggers.isNotEmpty() -> SafetyClassification(
                level = SafetyLevel.HIGH,
                triggers = highTriggers,
                recommendedAction = "Schedule repair as soon as possible. Drive with caution.",
                isDrivable = true
            )
            mediumTriggers.isNotEmpty() -> SafetyClassification(
                level = SafetyLevel.MEDIUM,
                triggers = mediumTriggers,
                recommendedAction = "Schedule repair within 1-2 weeks. Monitor closely.",
                isDrivable = true
            )
            else -> SafetyClassification(
                level = SafetyLevel.LOW,
                triggers = emptyList(),
                recommendedAction = "Routine maintenance. Can be addressed at next service.",
                isDrivable = true
            )
        }
    }
}
