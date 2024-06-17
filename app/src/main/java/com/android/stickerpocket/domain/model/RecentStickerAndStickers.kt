package com.android.stickerpocket.domain.model

import androidx.room.Embedded
import androidx.room.Relation

// This class represents relationship between sticker and RecentSticker
data class RecentStickerAndStickers(
    @Embedded val recentStickers: RecentStickers,
    @Relation(
        parentColumn = "stickerId",
        entityColumn = "id",
        entity = Sticker::class
    )
    val sticker: Sticker
)
