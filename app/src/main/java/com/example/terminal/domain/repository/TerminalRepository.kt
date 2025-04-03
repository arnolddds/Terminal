package com.example.terminal.domain.repository

import com.example.terminal.domain.model.Bar
import com.example.terminal.domain.model.Result
import com.example.terminal.domain.model.TimeFrame

interface TerminalRepository {
    suspend fun getBars(timeFrame: TimeFrame): Result<List<Bar>>
    suspend fun getLatestBar(timeFrame: TimeFrame): Result<Bar?>
}