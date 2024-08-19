package com.android.stickerpocket.network.response

data class GifResponse(
    val data: List<Data>,
    val meta: Meta,
    val pagination: Pagination
)