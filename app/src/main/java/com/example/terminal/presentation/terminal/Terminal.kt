package com.example.terminal.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.terminal.R
import com.example.terminal.domain.model.Bar
import com.example.terminal.domain.model.TimeFrame
import com.example.terminal.presentation.terminal.TerminalScreenState
import com.example.terminal.presentation.terminal.TerminalState
import com.example.terminal.presentation.terminal.TerminalViewModel
import com.example.terminal.presentation.terminal.rememberTerminalState
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt


private const val MIN_VISIBLE_BARS_COUNT = 20

@Composable
fun Terminal(
    modifier: Modifier = Modifier,
) {

    val viewModel: TerminalViewModel = viewModel()
    val screenState = viewModel.state.collectAsState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .statusBarsPadding()
    ) {
        when (val currentState = screenState.value) {
            is TerminalScreenState.Content -> {
                val terminalState = rememberTerminalState(bars = currentState.barList)

                Chart(
                    modifier = modifier,
                    terminalState = terminalState,
                    onTerminalStateChanged = {
                        terminalState.value = it
                    },
                    timeFrame = currentState.timeFrame
                )
                currentState.barList.firstOrNull()?.let {
                    Prices(
                        modifier = modifier,
                        terminalState = terminalState,
                        lastPrice = it.close
                    )
                }

                TimeFrames(
                    selectedFrame = currentState.timeFrame,
                    onTimeFrameSelected = { viewModel.loadBarList(it) }
                )
            }

            is TerminalScreenState.Initial -> {

            }

            TerminalScreenState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }

            is TerminalScreenState.Error -> {
                val (errorMessage, icon) = when (currentState.error) {
                    is com.example.terminal.domain.model.Error.NetworkError -> Pair(
                        stringResource(R.string.error_network_message),
                        Icons.Default.Refresh
                    )
                    is com.example.terminal.domain.model.Error.RateLimitError -> Pair(
                        stringResource(R.string.error_rate_limit_message),
                        Icons.Default.Refresh
                    )
                    is com.example.terminal.domain.model.Error.ServerError -> Pair(
                        stringResource(R.string.error_server_message),
                        Icons.Default.Refresh
                    )
                    else -> Pair(
                        stringResource(R.string.error_unknown_message),
                        Icons.Default.Refresh
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = "Error icon",
                        tint = Color.White,
                        modifier = Modifier
                            .clickable {
                                currentState.timeFrame?.let { frame ->
                                    viewModel.loadBarList(frame)
                                } ?: viewModel.loadBarList(TimeFrame.HOUR_1)
                            }
                            .size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = errorMessage,
                        fontSize = 20.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    if (currentState.error is com.example.terminal.domain.model.Error.RateLimitError) {
                        val retryAfter = (currentState.error).retryAfterSeconds
                        if (retryAfter != null) {
                            Spacer(modifier = Modifier
                                .height(8.dp))
                            Text(
                                text = stringResource(R.string.retry_available_in, retryAfter),
                                fontSize = 14.sp,
                                color = Color.LightGray,
                                modifier = Modifier
                                    .clickable {
                                        currentState.timeFrame?.let { frame ->
                                            viewModel.loadBarList(frame)
                                        } ?: viewModel.loadBarList(TimeFrame.HOUR_1)
                                    }
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.tap_to_retry),
                            fontSize = 16.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

    }
}

private fun DrawScope.drawTimeDelimiter(
    bar: Bar,
    nextBar: Bar?,
    timeFrame: TimeFrame,
    offsetX: Float,
    textMeasurer: TextMeasurer
) {
    val calendar = bar.calendar

    val minutes = calendar.get(Calendar.MINUTE)
    val hours = calendar.get(Calendar.HOUR_OF_DAY)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val shouldDrawDelimiter = when (timeFrame) {
        TimeFrame.MIN_5 -> {
            minutes == 0
        }

        TimeFrame.MIN_15 -> {
            minutes == 0 && hours % 2 == 0
        }

        TimeFrame.MIN_30, TimeFrame.HOUR_1 -> {
            val nextBarDay = nextBar?.calendar?.get(Calendar.DAY_OF_MONTH)
            day != nextBarDay
        }
    }

    if (!shouldDrawDelimiter) return

    drawLine(
        color = Color.White.copy(alpha = 0.5f),
        start = Offset(offsetX, 0f),
        end = Offset(offsetX, size.height),
        strokeWidth = 1f,
        pathEffect = PathEffect.dashPathEffect(
            intervals = floatArrayOf(4.dp.toPx(), 4.dp.toPx())
        )
    )

    val nameOfMonth = calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault())
    val text = when (timeFrame) {
        TimeFrame.MIN_5, TimeFrame.MIN_15 -> {
            String.format("%02d:00", hours)
        }

        TimeFrame.MIN_30, TimeFrame.HOUR_1 -> {
            String.format("%s %s", day, nameOfMonth)
        }
    }
    val textLayoutResult = textMeasurer.measure(
        text = text,
        style = TextStyle(
            color = Color.White,
            fontSize = 12.sp
        )
    )
    drawText(
        textLayoutResult = textLayoutResult,
        topLeft = Offset(offsetX - textLayoutResult.size.width / 2, size.height)
    )
}

@Composable
private fun TimeFrames(
    selectedFrame: TimeFrame,
    onTimeFrameSelected: (TimeFrame) -> Unit
) {
    Row(
        modifier = Modifier
            .wrapContentSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        TimeFrame.entries.forEach { timeFrame ->
            val labelResId = when (timeFrame) {
                TimeFrame.MIN_5 -> R.string.timeframe_5_minutes
                TimeFrame.MIN_15 -> R.string.timeframe_15_minutes
                TimeFrame.MIN_30 -> R.string.timeframe_30_minutes
                TimeFrame.HOUR_1 -> R.string.timeframe_1_hour
            }
            val isSelected = timeFrame == selectedFrame
            AssistChip(
                onClick = { onTimeFrameSelected(timeFrame) },
                label = { Text(text = stringResource(labelResId)) },
                colors = AssistChipDefaults.assistChipColors(
                    containerColor = if (isSelected) Color.White else Color.Black,
                    labelColor = if (isSelected) Color.Black else Color.White,
                )
            )
        }
    }

}


@Composable
private fun Chart(
    modifier: Modifier = Modifier,
    terminalState: State<TerminalState>,
    onTerminalStateChanged: (TerminalState) -> Unit,
    timeFrame: TimeFrame
) {
    val currentState = terminalState.value
    val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
        val visibleBarsCount = (currentState.visibleBarsCount / zoomChange)
            .roundToInt()
            .coerceIn(MIN_VISIBLE_BARS_COUNT, currentState.barList.size)

        val scrolledBy = (currentState.scrolledBy + panChange.x)
            .coerceAtLeast(0f)
            .coerceAtMost(currentState.barList.size * currentState.barWidth - currentState.terminalWidth)

        onTerminalStateChanged(
            currentState.copy(
                visibleBarsCount = visibleBarsCount,
                scrolledBy = scrolledBy
            )
        )
    }

    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .clipToBounds()
            .padding(
                top = 64.dp,
                bottom = 32.dp,
                end = 32.dp
            )
            .transformable(transformableState)
            .onSizeChanged {
                onTerminalStateChanged(
                    currentState.copy(
                        terminalWidth = it.width.toFloat(),
                        terminalHeight = it.height.toFloat()
                    )
                )
            }

    ) {
        val min = currentState.min
        val pxPerPoint = currentState.pxPerPoint
        translate(left = currentState.scrolledBy) {
            currentState.barList.forEachIndexed { index, bar ->
                val offsetX = size.width - (index * currentState.barWidth)
                drawTimeDelimiter(
                    bar = bar,
                    nextBar = if (index < currentState.barList.size - 1) {
                        currentState.barList[index + 1]
                    } else {
                        null
                    },
                    timeFrame = timeFrame,
                    offsetX = offsetX,
                    textMeasurer = textMeasurer
                )
                drawLine(
                    color = Color.White,
                    start = Offset(offsetX, size.height - ((bar.low - min) * pxPerPoint)),
                    end = Offset(offsetX, size.height - ((bar.high - min) * pxPerPoint)),
                    strokeWidth = 1f
                )
                drawLine(
                    color = if (bar.open < bar.close) Color.Green else Color.Red,
                    start = Offset(offsetX, size.height - ((bar.open - min) * pxPerPoint)),
                    end = Offset(offsetX, size.height - ((bar.close - min) * pxPerPoint)),
                    strokeWidth = currentState.barWidth / 2
                )
            }
        }

    }
}

@Composable
private fun Prices(
    modifier: Modifier = Modifier,
    terminalState: State<TerminalState>,
    lastPrice: Float
) {
    val currentState = terminalState.value
    val textMeasurer = rememberTextMeasurer()

    val max = currentState.max
    val min = currentState.min
    val pxPerPoint = currentState.pxPerPoint
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .clipToBounds()
            .padding(vertical = 32.dp)
    ) {
        drawPrices(max, min, pxPerPoint, lastPrice, textMeasurer)
    }
}


private fun DrawScope.drawPrices(
    max: Float,
    min: Float,
    pxPerPoint: Float,
    lastPrice: Float,
    textMeasurer: TextMeasurer
) {

    val maxPriceOffsetY = 0f
    drawDashedLine(
        start = Offset(0f, maxPriceOffsetY),
        end = Offset(size.width, maxPriceOffsetY),
    )
    drawTextPrice(
        textMeasurer = textMeasurer,
        price = max,
        offsetY = maxPriceOffsetY
    )


    val lastPriceOffsetY = size.height - ((lastPrice - min) * pxPerPoint)
    drawDashedLine(
        start = Offset(0f, lastPriceOffsetY),
        end = Offset(size.width, lastPriceOffsetY),
    )
    drawTextPrice(
        textMeasurer = textMeasurer,
        price = lastPrice,
        offsetY = lastPriceOffsetY
    )


    val minPriceOffsetY = size.height
    drawDashedLine(
        start = Offset(0f, minPriceOffsetY),
        end = Offset(size.width, minPriceOffsetY),
    )
    drawTextPrice(
        textMeasurer = textMeasurer,
        price = min,
        offsetY = minPriceOffsetY
    )
}


private fun DrawScope.drawTextPrice(
    textMeasurer: TextMeasurer,
    price: Float,
    offsetY: Float
) {
    val textLayoutResult = textMeasurer.measure(
        text = price.toString(),
        style = TextStyle(
            color = Color.White,
            fontSize = 12.sp
        )
    )
    drawText(
        textLayoutResult = textLayoutResult,
        topLeft = Offset(size.width - textLayoutResult.size.width - 4.dp.toPx(), offsetY)
    )
}

private fun DrawScope.drawDashedLine(
    color: Color = Color.White,
    start: Offset,
    end: Offset,
    strokeWidth: Float = 1f
) {
    drawLine(
        color = color,
        start = start,
        end = end,
        strokeWidth = strokeWidth,
        pathEffect = PathEffect.dashPathEffect(
            intervals = floatArrayOf(
                4.dp.toPx(), 4.dp.toPx()
            )
        )
    )
}