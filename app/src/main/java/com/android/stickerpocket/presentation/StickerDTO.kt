package com.android.stickerpocket.presentation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StickerDTO(
    val id: Int,
    val thumbnail: String?,
    val title: String?,
    val creator: String? = null,
    val source: String? = null,
    var tags: List<String>? = null,
    val mediaId: String? = null,
    val stickerId: Int? = null,
    val isFavourite: Boolean = false,
    val isDownloaded: Boolean = false
): Parcelable