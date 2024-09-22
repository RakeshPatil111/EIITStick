package com.android.stickerpocket.utils

import androidx.room.PrimaryKey
import com.android.stickerpocket.domain.model.Category
import com.android.stickerpocket.domain.model.Sticker
import com.android.stickerpocket.dtos.CommonAdapterDTO

fun CommonAdapterDTO.toSticker(): Sticker {
    return Sticker(
        name = this.name,
        categoryId = this.categoryId,
        id = this.id,
        isDeleted = this.isDeleted,
        url = this.url,
        isDownloaded = this.isDownloaded,
        isFavourite = this.isFavourite,
        localPath = this.localPath,
        tags = this.tags,
        creator = this.creator,
        source = this.source,
        position = this.position,
        mediaId = this.mediaId
    )
}

fun Sticker.toCommonDTO(): CommonAdapterDTO {
    return CommonAdapterDTO(
        isSticker = true,
        name = this.name,
        categoryId = this.categoryId,
        id = this.id,
        isDeleted = this.isDeleted,
        url = this.url,
        isDownloaded = this.isDownloaded,
        isFavourite = this.isFavourite,
        localPath = this.localPath,
        tags = this.tags,
        creator = this.creator,
        source = this.source,
        position = this.position,
        mediaId = this.mediaId
    )
}

fun Category.toCommonDTO(): CommonAdapterDTO {
    return CommonAdapterDTO(
        isSticker = false,
        id = this.id,
        name = this.name,
        unicode = this.unicode,
        position = this.position,
        isHighlighted = this.isHighlighted,
        isDeleted = this.isDeleted,
        html = this.html
    )
}

fun CommonAdapterDTO.toCategory(): Category {
    return Category(
        id = this.id,
        name = this.name,
        unicode = this.unicode,
        position = this.position,
        isHighlighted = this.isHighlighted,
        isDeleted = this.isDeleted,
        html = this.html
    )
}