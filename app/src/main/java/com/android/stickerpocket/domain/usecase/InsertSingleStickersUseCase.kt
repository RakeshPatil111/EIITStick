package com.android.stickerpocket.domain.usecase

import com.android.stickerpocket.domain.model.Sticker
import com.android.stickerpocket.domain.repository.StickerRepository

class InsertSingleStickersUseCase (private val stickerRepository: StickerRepository) {
    suspend fun execute(stickers:Sticker) {
        if (stickerRepository.checkDuplicate(stickers.mediaId).isEmpty())
            stickerRepository.insertSticker(stickers)
    }
}