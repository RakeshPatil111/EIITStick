package com.android.stickerpocket.presentation

import com.android.stickerpocket.dtos.Gifs
import com.android.stickerpocket.dtos.gifs

data class StickerSection(
    val title: String,
    val gifs: ArrayList<Gifs> = arrayListOf()
)

val firstSection = StickerSection(
    "Powered by Giphy",
    gifs
)

val secondSection = StickerSection(
    "Powered by Tenor",
    gifs
)

val thirdSection = StickerSection(
    "Powered by Tenor",
    gifs
)

val fourthSection = StickerSection(
    "Powered by Tenor",
    gifs
)

val fifthSection = StickerSection(
    "Powered by Tenor",
    gifs
)

val sectionStickers = arrayListOf(firstSection, secondSection, thirdSection, fourthSection, fifthSection)