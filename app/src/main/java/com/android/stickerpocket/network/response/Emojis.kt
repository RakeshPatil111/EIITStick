package com.android.stickerpocket.network.response
import kotlinx.serialization.Serializable
@Serializable
data class Emojis(
    val emojis: List<Emoji>
)