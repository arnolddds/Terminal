package com.example.terminal.presentation.terminal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.terminal.domain.model.Result
import com.example.terminal.domain.model.TimeFrame
import com.example.terminal.domain.usecase.GetBarsUseCase
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

    init {
        loadBarList()
    }

    fun loadBarList(timeFrame: TimeFrame = TimeFrame.HOUR_1) {
        _state.value = TerminalScreenState.Loading
        viewModelScope.launch {
            when (val result = getBarsUseCase(timeFrame)) {
                is Result.Success -> {
                    _state.value = TerminalScreenState.Content(
                        barList = result.data,
                        timeFrame = timeFrame
                    )
                }
                is Result.Failure -> {
                    _state.value = TerminalScreenState.Error(result.error)
                }
            }
        }
    }
}