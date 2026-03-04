package com.aa.carrepair.analytics

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PredictiveInsights @Inject constructor() {

    fun predictMaintenanceCost(
        vehicleAge: Int,
        mileage: Int,
        historicalCosts: List<Double>
    ): PredictionResult {
        val baseCostPerYear = 800.0
        val mileageFactor = mileage / 10000.0 * 150.0
        val ageFactor = vehicleAge * 100.0

        val historicalAvg = if (historicalCosts.isNotEmpty()) historicalCosts.average() else 0.0
        val trend = calculateTrend(historicalCosts)

        val predicted = (baseCostPerYear + mileageFactor + ageFactor + historicalAvg) / 2 * (1 + trend)
        val lowerBound = predicted * 0.7
        val upperBound = predicted * 1.4

        return PredictionResult(
            predictedValue = predicted,
            lowerBound = lowerBound,
            upperBound = upperBound,
            confidence = calculateConfidence(historicalCosts.size),
            trend = if (trend > 0) "increasing" else if (trend < 0) "decreasing" else "stable"
        )
    }

    fun identifyMaintenancePattern(
        serviceDates: List<Long>,
        mileages: List<Int>
    ): MaintenancePattern {
        if (serviceDates.size < 2) {
            return MaintenancePattern(
                avgIntervalDays = 90,
                avgIntervalMiles = 5000,
                isRegular = false,
                nextDueDays = 90
            )
        }

        val intervals = serviceDates.zipWithNext { a, b -> (b - a) / 86400000L }
        val avgInterval = intervals.average().toInt()
        val mileageIntervals = if (mileages.size >= 2) {
            mileages.zipWithNext { a, b -> b - a }.average().toInt()
        } else 5000
        val isRegular = intervals.all { it in (avgInterval * 0.7).toInt()..(avgInterval * 1.3).toInt() }

        return MaintenancePattern(
            avgIntervalDays = avgInterval,
            avgIntervalMiles = mileageIntervals,
            isRegular = isRegular,
            nextDueDays = avgInterval
        )
    }

    private fun calculateTrend(values: List<Double>): Double {
        if (values.size < 2) return 0.0
        val recent = values.takeLast(3).average()
        val older = values.dropLast(3).takeIf { it.isNotEmpty() }?.average() ?: return 0.0
        return if (older > 0) (recent - older) / older else 0.0
    }

    private fun calculateConfidence(dataPoints: Int): Double = when {
        dataPoints >= 10 -> 0.9
        dataPoints >= 5 -> 0.75
        dataPoints >= 3 -> 0.6
        else -> 0.4
    }
}

data class PredictionResult(
    val predictedValue: Double,
    val lowerBound: Double,
    val upperBound: Double,
    val confidence: Double,
    val trend: String
)

data class MaintenancePattern(
    val avgIntervalDays: Int,
    val avgIntervalMiles: Int,
    val isRegular: Boolean,
    val nextDueDays: Int
)
