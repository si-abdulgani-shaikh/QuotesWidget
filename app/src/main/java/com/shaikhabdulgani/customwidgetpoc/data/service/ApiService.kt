package com.shaikhabdulgani.customwidgetpoc.data.service

import com.shaikhabdulgani.customwidgetpoc.data.dto.QuoteDto
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("/api/random")
    suspend fun getRandomQuote(): Response<List<QuoteDto>>
}