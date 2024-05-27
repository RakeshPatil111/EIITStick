package com.android.stickerpocket.domain.usecase

import android.util.Log
import com.android.stickerpocket.domain.model.Emoji
import com.android.stickerpocket.domain.repository.EmojiRepository

class AddEmojiIfNotExistUseCase(private val emojiRepository: EmojiRepository) {
    suspend fun execute(emojis: List<Emoji>){
        try {
            if (!emojiRepository.doesEmojisExist()) {
                emojiRepository.saveEmojis(emojis)
            }
        } catch (e: Exception) {
            Log.e("AddEmojiIfNotExistUseCase", "execute: ${e.message}")
        }
    }
}