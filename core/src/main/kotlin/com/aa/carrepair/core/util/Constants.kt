package com.aa.carrepair.core.util

object Constants {
    // API
    const val API_TIMEOUT_SECONDS = 30L
    const val API_RETRY_COUNT = 3
    const val API_RETRY_DELAY_MS = 1000L

    // Database
    const val DATABASE_NAME = "aa_carrepair.db"
    const val DATABASE_VERSION = 1

    // Sync
    const val SYNC_INTERVAL_HOURS = 6L
    const val SYNC_FLEX_INTERVAL_HOURS = 1L
    const val SYNC_WORK_NAME = "aa_periodic_sync"

    // Chat
    const val MAX_CHAT_HISTORY = 100
    const val CHAT_CONTEXT_WINDOW = 10

    // VIN
    const val VIN_LENGTH = 17

    // Safety
    const val SAFETY_CRITICAL_THRESHOLD = 0.9f
    const val SAFETY_HIGH_THRESHOLD = 0.7f
    const val SAFETY_MEDIUM_THRESHOLD = 0.4f

    // Confidence
    const val HIGH_CONFIDENCE_THRESHOLD = 80
    const val MEDIUM_CONFIDENCE_THRESHOLD = 60

    // Calculators
    const val DEFAULT_LABOR_RATE = 125.0
    const val DEFAULT_MARKUP_PERCENTAGE = 40.0
    const val REPAIR_THRESHOLD_PERCENTAGE = 0.5

    // Fleet
    const val FLEET_MAX_VEHICLES = 500

    // Inspection
    const val INSPECTION_IMAGE_MAX_SIZE_MB = 10
    const val DAMAGE_DETECTION_MIN_CONFIDENCE = 0.6f
}
