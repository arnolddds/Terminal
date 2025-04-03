package com.example.terminal.data.repository

import com.example.terminal.data.model.BarDto
import com.example.terminal.data.network.ApiService
import com.example.terminal.domain.model.Bar
import com.example.terminal.domain.model.Error
import com.example.terminal.domain.model.Result
import com.example.terminal.domain.model.TimeFrame
import com.example.terminal.domain.repository.TerminalRepository
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

class TerminalRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : TerminalRepository {
    
    override suspend fun getBars(timeFrame: TimeFrame): Result<List<Bar>> {
        return try {
            val response = apiService.loadBars(timeFrame.value)
            Result.Success(response.barList.map { it.toDomain() })
        } catch (e: Exception) {
            Result.Failure(e.toError())
        }
    }

    override suspend fun getLatestBar(timeFrame: TimeFrame): Result<Bar?> {
        return try {
            val response = apiService.loadBars(timeFrame.value)
            Result.Success(response.barList.firstOrNull()?.toDomain())
        } catch (e: Exception) {
            Result.Failure(e.toError())
        }
    }

    private fun BarDto.toDomain(): Bar {
        return Bar(
            timestamp = time,
            open = open,
            high = high,
            low = low,
            close = close,
        )
    }

    private fun Exception.toError(): Error {
        return when (this) {
            is UnknownHostException -> Error.NetworkError
            is SocketTimeoutException -> Error.NetworkError
            is HttpException -> Error.ServerError
            else -> Error.UnknownError
        }
    }

    private fun processError(e: Exception): Error {
        return when (e) {
            is UnknownHostException, is SocketTimeoutException -> Error.NetworkError
            is HttpException -> {
                if (e.code() == 429) {
                    val retryAfter = e.response()?.headers()?.get("Retry-After")?.toIntOrNull()
                    Error.RateLimitError(retryAfter)
                } else {
                    Error.ServerError
                }
            }
            else -> Error.UnknownError
        }
    }
} 