package com.android.stickerpocket.domain.usecase

import com.android.stickerpocket.domain.model.Emoji
import com.android.stickerpocket.domain.repository.EmojiRepository

class GetLocalEmojiUseCase(private val emojiRepository: EmojiRepository) {

    suspend fun execute(): MutableList<Emoji>{
        return emojiRepository.getAllEmoji()
    }
}