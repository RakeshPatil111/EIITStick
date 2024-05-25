package com.android.stickerpocket.domain.usecase

import com.android.stickerpocket.domain.model.Emoji
import com.android.stickerpocket.domain.repository.EmojiRepository

class SaveEmojiUseCase(private val emojiRepository: EmojiRepository) {
    suspend fun execute(emojis: List<Emoji>){
        emojiRepository.saveEmojis(emojis)
    }
}