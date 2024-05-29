package com.android.stickerpocket.utils

import com.android.stickerpocket.network.response.Emoji
import kotlin.random.Random
import com.android.stickerpocket.domain.model.Emoji as ModelEmoji

fun Emoji.toEmoji(): ModelEmoji {
    val em = ModelEmoji(
        id = Random.nextInt(),
        category = this.category,
        name = this.name,
        unicode = this.unicode.lowercase(),
        shortname = this.shortname,
        html = this.html,
        emoji = this.emoji,
        order = this.order
    )
    return em
}