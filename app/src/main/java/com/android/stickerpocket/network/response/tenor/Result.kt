package com.android.stickerpocket.network.response.tenor

data class Result(
    val bg_color: String,
    val composite: Any,
    val content_description: String,
    val content_rating: String,
    val created: Double,
    val flags: List<Any>,
    val h1_title: String,
    val hasaudio: Boolean,
    val hascaption: Boolean,
    val id: String,
    val itemurl: String,
    val media: List<Media>,
    val shares: Int,
    val source_id: String,
    val tags: List<Any>,
    val title: String,
    val url: String
)