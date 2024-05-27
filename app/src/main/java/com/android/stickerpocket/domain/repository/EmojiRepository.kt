package com.android.stickerpocket.domain.repository

import com.android.stickerpocket.domain.dao.EmojiDAO
import com.android.stickerpocket.domain.model.Emoji

class EmojiRepository(private val dao: EmojiDAO) {
    suspend fun getEmojiByUnicode(unicode: String) = dao.getEmojiName(unicode)

    suspend fun saveEmojis(emojis: List<Emoji>) = dao.saveEmojis(emojis)

    suspend fun doesEmojisExist() = dao.doesEmojisExist()
}