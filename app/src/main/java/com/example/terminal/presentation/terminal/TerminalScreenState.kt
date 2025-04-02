package com.example.terminal.presentation.terminal

import com.example.terminal.domain.model.Bar
import com.example.terminal.domain.model.TimeFrame

sealed class TerminalScreenState {

    data object Initial: TerminalScreenState()

    data object Loading: TerminalScreenState()

    data class Content(
        val barList: List<Bar>,
        val timeFrame: TimeFrame,
        val lastUpdateTime: Long = System.currentTimeMillis()
    ): TerminalScreenState()

    data class Error(
        val error: com.example.terminal.domain.model.Error,
        val timeFrame: TimeFrame? = null
    ): TerminalScreenState()

}