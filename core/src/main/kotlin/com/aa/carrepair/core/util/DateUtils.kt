package com.aa.carrepair.core.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object DateUtils {
    private val DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM d, yyyy")
    private val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm")
    private val SHORT_DATE_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yy")
    private val TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm")

    fun formatDate(instant: Instant): String =
        DATE_FORMATTER.format(instant.atZone(ZoneId.systemDefault()).toLocalDate())

    fun formatDateTime(instant: Instant): String =
        DATE_TIME_FORMATTER.format(instant.atZone(ZoneId.systemDefault()))

    fun formatShortDate(instant: Instant): String =
        SHORT_DATE_FORMATTER.format(instant.atZone(ZoneId.systemDefault()).toLocalDate())

    fun formatTime(instant: Instant): String =
        TIME_FORMATTER.format(instant.atZone(ZoneId.systemDefault()))

    fun daysBetween(from: Instant, to: Instant): Long =
        ChronoUnit.DAYS.between(from, to)

    fun isOverdue(dueDate: Instant): Boolean =
        Instant.now().isAfter(dueDate)

    fun daysUntil(date: Instant): Long =
        ChronoUnit.DAYS.between(Instant.now(), date)

    fun monthsAgo(months: Long): Instant =
        Instant.now().atZone(ZoneId.systemDefault())
            .minusMonths(months)
            .toInstant()

    fun currentYear(): Int = LocalDate.now().year
}
