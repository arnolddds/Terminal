package com.example.terminal.data

import retrofit2.http.GET

interface ApiService {


    @GET("aggs/ticker/AAPL/range/1/day/2023-01-09/2023-02-10?adjusted=true&sort=asc&limit=120&apiKey=RPxwcp8QQseffWL_OjvcYzwpfJ__o0GI")
    suspend fun loadBars(): Result
}