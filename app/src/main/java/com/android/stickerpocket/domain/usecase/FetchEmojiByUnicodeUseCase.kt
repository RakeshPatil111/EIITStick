package com.android.stickerpocket.domain.usecase

import com.android.stickerpocket.domain.model.Emoji
import com.android.stickerpocket.domain.repository.EmojiRepository

class FetchEmojiByUnicodeUseCase(private val emojiRepository: EmojiRepository) {
    suspend fun execute(unicode: String): Emoji? {
        return emojiRepository.fetchEmojiByUnicode(unicode)
    }
}