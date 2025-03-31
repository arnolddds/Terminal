package com.example.terminal.presentation

import android.os.Parcelable
import com.example.terminal.data.Bar
import kotlinx.parcelize.Parcelize
import kotlin.math.roundToInt


@Parcelize
data class TerminalState(
    val barList: List<Bar>,
    val visibleBarsCount: Int = 100,
    val terminalWidth: Float = 0f,
    val scrolledBy: Float = 0f
): Parcelable {

    val barWidth: Float
        get() = terminalWidth / visibleBarsCount

    val visibleBars: List<Bar>
        get() {
            val startIndex = (scrolledBy / barWidth).roundToInt().coerceAtLeast(0)
            val endIndex = (startIndex + visibleBarsCount).coerceAtMost(barList.size)
            return barList.subList(startIndex, endIndex)
        }


}

