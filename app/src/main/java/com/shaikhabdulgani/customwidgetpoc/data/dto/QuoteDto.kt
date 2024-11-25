package com.shaikhabdulgani.customwidgetpoc.data.dto

import com.google.gson.annotations.SerializedName

data class QuoteDto(
    @SerializedName("q")
    val quote: String?,
    @SerializedName("a")
    val artist: String?
)
