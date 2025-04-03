package com.example.terminal.domain.model

sealed class Error {
    data object NetworkError : Error()
    data object ServerError : Error()
    data object UnknownError : Error()
    data class RateLimitError(
        val retryAfterSeconds: Int? = null
    ) : Error()
}