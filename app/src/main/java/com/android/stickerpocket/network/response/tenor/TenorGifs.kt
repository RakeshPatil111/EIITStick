package com.android.stickerpocket.network.response.tenor

data class TenorGifs(
    val locale: String,
    val next: String,
    val results: List<Result>
)