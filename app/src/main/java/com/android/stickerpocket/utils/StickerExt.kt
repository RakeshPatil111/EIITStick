package com.android.stickerpocket.utils

import android.net.Uri
import com.android.stickerpocket.StickerApplication
import com.android.stickerpocket.domain.model.Sticker
import com.android.stickerpocket.presentation.StickerDTO
import com.giphy.sdk.core.models.Media
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import kotlin.random.Random


object StickerExt {
    fun Sticker.toFile(): File? {
        val cachedFile = File(StickerApplication.instance.cacheDir, this.name + ".gif")
        if (cachedFile.exists()) {
            return cachedFile
        }
        val inputStream: InputStream = StickerApplication.instance.resources.openRawResource(
            StickerApplication.instance.resources.getIdentifier(
                this.name,
                "raw",
                StickerApplication.instance.packageName
            )
        )
        val tempFile = File.createTempFile(this.name, ".gif")
        copyFile(inputStream, FileOutputStream(tempFile))
        return tempFile
    }

    @Throws(IOException::class)
    private fun copyFile(input: InputStream, out: OutputStream) {
        val buffer = ByteArray(1024)
        var read: Int
        while ((input.read(buffer).also { read = it }) != -1) {
            out.write(buffer, 0, read)
        }
    }

    fun Media.stickerDTO(): StickerDTO {
        return StickerDTO(
            id = Random.nextInt(),
            title = this.title,
            thumbnail = this.images.original?.gifUrl,
            source = this.source,
            creator = this.user?.username,
            mediaId = this.id,
            tags = this.tags
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

    fun Sticker.toLoadableImage(): Comparable<*>? {
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

    fun Sticker.toStickerDTO() = StickerDTO(
        id = Random.nextInt(),
        mediaId = this.mediaId,
        thumbnail = this.url,
        title = this.name,
        source = this.source,
        creator = this.creator,
        tags = this.tags?.let { listOf(it) },
        stickerId = this.id,
        isFavourite = this.isFavourite,
        isDownloaded = this.isDownloaded
    )

    fun StickerDTO.toLoadableImage() : Comparable<*>? {
        val cachedFile = File(StickerApplication.instance.cacheDir, this.title + ".gif")
        val url = if (cachedFile.length() > 0) cachedFile.path else this.thumbnail
        return when {
            StickerApplication.instance.resources.getIdentifier(
                this.title,
                "raw",
                StickerApplication.instance.packageName
            ) != 0 -> {
                Uri.parse("android.resource://${StickerApplication.instance.packageName}/raw/${this.title}")
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