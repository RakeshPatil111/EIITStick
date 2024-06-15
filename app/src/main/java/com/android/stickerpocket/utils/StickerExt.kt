package com.android.stickerpocket.utils

import android.net.Uri
import androidx.room.PrimaryKey
import com.android.stickerpocket.StickerApplication
import com.android.stickerpocket.domain.model.Favourites
import com.android.stickerpocket.domain.model.Sticker
import com.android.stickerpocket.presentation.StickerDTO
import com.giphy.sdk.core.models.Media
import java.io.File
import kotlin.random.Random

object StickerExt {
    fun StickerDTO.toFile() = File(StickerApplication.instance.cacheDir, this.title!! + ".gif")

    fun Media.stickerDTO(): StickerDTO {
        return StickerDTO(
            id = Random.nextInt(),
            title = this.title,
            thumbnail = this.images.original?.gifUrl,
            source = this.source,
            creator = this.user?.username,
            mediaId = this.id
        )
    }

    fun StickerDTO.sticker(): Sticker {
        return Sticker(
            mediaId = this.mediaId ?: "",
            name = this.title!!,
            isDownloaded = true,
            url= this.thumbnail ?: "",
            tags = this.tags?.joinToString { "," },
            creator= this.creator,
            source = this.source,
            position = 0
        )
    }


    fun com.android.stickerpocket.domain.model.Sticker.toFavorite(): Favourites {
        return Favourites(
            id = Random.nextInt(),
            mediaId = this.mediaId,
            url = this.url,
            position = 0,
            name = this.name,
            stickerId = this.id!!.toString()
        )
    }

    fun com.android.stickerpocket.domain.model.Sticker.toLoadableImage(): Comparable<*>? {
        val cachedFile = File(StickerApplication.instance.cacheDir, this.name + ".gif")
        val url = if (cachedFile.length() > 0) cachedFile.path else this.url
        return when {
            StickerApplication.instance.resources.getIdentifier(
                this.name,
                "raw",
                StickerApplication.instance.packageName
            ) != 0 -> {
                Uri.parse("android.resource://${StickerApplication.instance.packageName}/raw/${this.name}")
            }

            url != null -> {
                return url
            }

            else -> {
                return null
            }
        }
    }
}