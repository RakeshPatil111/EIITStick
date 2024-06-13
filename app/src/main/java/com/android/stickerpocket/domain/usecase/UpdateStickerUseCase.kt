package com.android.stickerpocket.domain.usecase

import com.android.stickerpocket.domain.model.Sticker
import com.android.stickerpocket.domain.repository.StickerRepository

class UpdateStickerUseCase(private val stickerRepository: StickerRepository) {
    suspend fun execute(sticker: Sticker) {
        stickerRepository.updateSticker(sticker)
    }
}