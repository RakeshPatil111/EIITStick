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
    val tags: List<String>? = null,
    val mediaId: String? = null
): Parcelable

val smiley = StickerDTO(
    1,
    "https://i.ibb.co/44H70gw/smile.png",
    "smiley"
)

val clap = StickerDTO(
    2,
    "https://i.ibb.co/tQv1P0F/clap.png",
    "clap"
)

val laugh = StickerDTO(
    3,
    "https://i.ibb.co/3Rr0Kf7/laugh.png",
    "face with tears of joy"
)

val love = StickerDTO(
    4,
    "https://i.ibb.co/6JFV20f/love.png",
    "love"
)

val unwell = StickerDTO(
    5,
    "https://i.ibb.co/Ycb6NxN/unwell.png",
    "Unwell"
)

val sleepy = StickerDTO(
    6,
    "https://i.ibb.co/KmyDKsc/sleepy.png",
    "Sleepy"
)