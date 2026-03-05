package com.aa.carrepair.core.result

sealed class DataResult<out T> {
    data class Success<T>(val data: T) : DataResult<T>()
    data class Error(val exception: Throwable, val message: String? = null) : DataResult<Nothing>()
    object Loading : DataResult<Nothing>()

    val isSuccess get() = this is Success
    val isError get() = this is Error
    val isLoading get() = this is Loading

    fun getOrNull(): T? = (this as? Success)?.data

    fun getOrThrow(): T = when (this) {
        is Success -> data
        is Error -> throw exception
        is Loading -> throw IllegalStateException("Result is still loading")
    }

    inline fun onSuccess(action: (T) -> Unit): DataResult<T> {
        if (this is Success) action(data)
        return this
    }

    inline fun onError(action: (Throwable) -> Unit): DataResult<T> {
        if (this is Error) action(exception)
        return this
    }

    inline fun <R> map(transform: (T) -> R): DataResult<R> = when (this) {
        is Success -> Success(transform(data))
        is Error -> this
        is Loading -> Loading
    }
}

suspend fun <T> safeApiCall(block: suspend () -> T): DataResult<T> =
    try {
        DataResult.Success(block())
    } catch (e: java.io.IOException) {
        DataResult.Error(e, "Network error. Please check your internet connection.")
    } catch (e: Exception) {
        val httpCode = try {
            e.javaClass.getMethod("code").invoke(e) as? Int
        } catch (_: Exception) { null }

        val errorMsg = when (httpCode) {
            502 -> "Server is temporarily unavailable (502). Please try again later."
            503 -> "Service is under maintenance (503). Please try again later."
            504 -> "Server timed out (504). Please check your connection."
            in 500..599 -> "Server error ($httpCode). Please try again later."
            401, 403 -> "Authentication failed ($httpCode). Please check your API key."
            null -> e.message
            else -> "Request failed with code $httpCode"
        }
        DataResult.Error(e, errorMsg)
    }
