package com.android.stickerpocket.utils

import com.android.stickerpocket.network.response.Data
import com.android.stickerpocket.presentation.StickerDTO
import kotlin.random.Random

fun Data.toStickerDTO(): StickerDTO {
    return StickerDTO (
        id = Random.nextInt(0, 999999),
        thumbnail = this.images?.original?.url,
        title = this.title,
        creator = this.user?.username,
        source = this.source_tld,
        mediaId = this.id )
}