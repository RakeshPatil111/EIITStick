package com.android.stickerpocket.network.response

import kotlinx.serialization.Serializable


@Serializable
data class Emoji(
    val category: String,
    val emoji: String,
    val html: String,
    val name: String,
    val order: String,
    val shortname: String,
    val unicode: String
)