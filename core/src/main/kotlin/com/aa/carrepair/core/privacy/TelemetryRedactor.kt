package com.aa.carrepair.core.privacy

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TelemetryRedactor @Inject constructor() {
    private val vinPattern = Regex("[A-HJ-NPR-Z0-9]{17}", RegexOption.IGNORE_CASE)
    private val emailPattern = Regex("[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}")
    private val phonePattern = Regex("\\b(\\+?1[-.]?)?(\\(?[0-9]{3}\\)?[-.]?[0-9]{3}[-.]?[0-9]{4})\\b")
    private val platePattern = Regex("\\b[A-Z]{1,3}[-\\s]?[0-9]{1,4}[-\\s]?[A-Z]{0,3}\\b")

    fun redact(input: String): String =
        input
            .replace(vinPattern, "[VIN_REDACTED]")
            .replace(emailPattern, "[EMAIL_REDACTED]")
            .replace(phonePattern, "[PHONE_REDACTED]")
            .replace(platePattern, "[PLATE_REDACTED]")
}
