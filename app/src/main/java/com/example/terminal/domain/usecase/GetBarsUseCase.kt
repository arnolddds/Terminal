package com.example.terminal.domain.usecase

import com.example.terminal.domain.model.Bar
import com.example.terminal.domain.model.Result
import com.example.terminal.domain.model.TimeFrame
import com.example.terminal.domain.repository.TerminalRepository
import javax.inject.Inject

class
GetBarsUseCase @Inject constructor(
    private val repository: TerminalRepository
) {
    suspend operator fun invoke(timeFrame: TimeFrame): Result<List<Bar>> {
        return repository.getBars(timeFrame)
    }
} 