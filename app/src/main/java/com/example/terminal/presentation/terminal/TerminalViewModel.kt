package com.example.terminal.presentation.terminal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terminal.domain.model.Error
import com.example.terminal.domain.model.Result
import com.example.terminal.domain.model.TimeFrame
import com.example.terminal.domain.usecase.GetBarsUseCase
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.util.concurrent.RateLimiter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TerminalViewModel @Inject constructor(
    private val getBarsUseCase: GetBarsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<TerminalScreenState>(TerminalScreenState.Initial)
    val state = _state.asStateFlow()

    private val rateLimiter = RateLimiter(
        maxRequests = 5,
        timeWindowSeconds = 60
    )

    init {
        loadBarList()
    }

    fun loadBarList(timeFrame: TimeFrame = TimeFrame.HOUR_1) {
        if (!rateLimiter.canMakeRequest()) {
            _state.value = TerminalScreenState.Error(
                error = Error.RateLimitError(
                    retryAfterSeconds = 60 - rateLimiter.secondsSinceFirstRequest()
                ),
                timeFrame = timeFrame
            )
            return
        }

        _state.value = TerminalScreenState.Loading
        viewModelScope.launch {
            when (val result = getBarsUseCase(timeFrame)) {
                is Result.Success -> {
                    rateLimiter.recordRequest()
                    _state.value = TerminalScreenState.Content(
                        barList = result.data,
                        timeFrame = timeFrame
                    )
                }
                is Result.Failure -> {
                    _state.value = TerminalScreenState.Error(
                        error = result.error,
                        timeFrame = timeFrame
                    )
                }
            }
        }
    }
}