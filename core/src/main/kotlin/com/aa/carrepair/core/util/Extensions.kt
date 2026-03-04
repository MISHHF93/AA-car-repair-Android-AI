package com.aa.carrepair.core.util

import com.aa.carrepair.core.result.DataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

fun String.isValidVin(): Boolean {
    if (length != 17) return false
    val vinRegex = Regex("^[A-HJ-NPR-Z0-9]{17}$")
    return vinRegex.matches(this.uppercase())
}

fun String.isValidDtcCode(): Boolean {
    val dtcRegex = Regex("^[PBCU][0-9]{4}$")
    return dtcRegex.matches(this.uppercase())
}

fun String.maskPii(): String {
    if (length <= 4) return "****"
    return "****" + takeLast(4)
}

fun String.toTitleCase(): String =
    split(" ").joinToString(" ") { word ->
        word.lowercase().replaceFirstChar { it.uppercase() }
    }

fun Double.toCurrencyString(): String = String.format("$%,.2f", this)

fun Double.toPercentString(): String = String.format("%.1f%%", this * 100)

fun Int.toMileageString(metric: Boolean = false): String =
    if (metric) String.format("%,d km", this) else String.format("%,d miles", this)

fun <T> Flow<T>.asDataResultFlow(): Flow<DataResult<T>> =
    this
        .map<T, DataResult<T>> { DataResult.Success(it) }
        .onStart { emit(DataResult.Loading) }
        .catch { emit(DataResult.Error(it)) }
