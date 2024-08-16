package com.android.stickerpocket.utils

import com.android.stickerpocket.network.response.tenor.Result
import com.android.stickerpocket.presentation.StickerDTO
import kotlin.random.Random

fun Result.toStickerDTO(): StickerDTO {
    return StickerDTO (
        id = Random.nextInt(0, 999999),
        thumbnail = this.media[0].gif.url,
        title = if (this.title.isEmpty()) this.content_description else this.title,
        mediaId = this.id)
}