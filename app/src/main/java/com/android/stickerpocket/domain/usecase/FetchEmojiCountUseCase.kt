package com.android.stickerpocket.domain.usecase

import com.android.stickerpocket.domain.repository.EmojiRepository

class FetchEmojiCountUseCase(private val emojiRepository: EmojiRepository) {

    suspend fun execute(): Int{
        return emojiRepository.fetchEmojiCount()
    }
}