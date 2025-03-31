package com.example.terminal.data

import retrofit2.http.GET

interface ApiService {


    @GET("aggs/ticker/AAPL/range/1/hour/2022-01-09/2024-01-09?adjusted=true&sort=asc&limit=50000&apiKey=RPxwcp8QQseffWL_OjvcYzwpfJ__o0GI")
    suspend fun loadBars(): Result
}