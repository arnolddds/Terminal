package com.example.terminal.data

import com.example.terminal.presentation.TimeFrame
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {


    @GET("aggs/ticker/AAPL/range/{timeframe}/2022-01-09/2024-01-09?adjusted=true&sort=desc&limit=50000&apiKey=RPxwcp8QQseffWL_OjvcYzwpfJ__o0GI")
    suspend fun loadBars(
        @Path("timeframe") timeFrame: String
    ): Result
}