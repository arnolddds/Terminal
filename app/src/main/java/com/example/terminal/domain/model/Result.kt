package com.example.terminal.domain.model

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Failure(val error: Error) : Result<Nothing>()
} 