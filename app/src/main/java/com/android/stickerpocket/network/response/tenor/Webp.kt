package com.android.stickerpocket.network.response.tenor

data class Webp(
    val dims: List<Int>,
    val duration: Double,
    val preview: String,
    val size: Int,
    val url: String
)