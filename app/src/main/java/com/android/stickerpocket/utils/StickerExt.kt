package com.android.stickerpocket.utils

import com.android.stickerpocket.StickerApplication
import com.android.stickerpocket.domain.model.Favourites
import com.android.stickerpocket.presentation.Sticker
import com.giphy.sdk.core.models.Media
import java.io.File
import kotlin.random.Random

object StickerExt {
    fun Sticker.toFile() = File(StickerApplication.instance.cacheDir, this.title!! + ".gif")

    fun Media.toSticker(): Sticker {
        return Sticker(
            id = Random.nextInt(),
            title = this.title,
            thumbnail = this.images.original?.gifUrl,
            source = this.source,
            creator = this.user?.username,
            mediaId = this.id
        )
    }

    fun Sticker.toFavorite(): Favourites {
        return Favourites(
            id = Random.nextInt(),
            mediaId = this.mediaId,
            url = this.thumbnail,
            position = 0,
            name = this.title!!
        )
    }
}