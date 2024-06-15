package com.android.stickerpocket.domain.usecase

import com.android.stickerpocket.domain.model.Sticker
import com.android.stickerpocket.domain.repository.StickerRepository

class InsertStickersUseCase (private val stickerRepository: StickerRepository) {
    suspend fun execute(stickers: List<Sticker>) {
        if (stickerRepository.fetchAll().isEmpty()) {
            stickerRepository.insertAll(stickers)
        }
    }
    suspend fun forceInsertAll(stickers: List<Sticker>) {
        stickerRepository.insertAll(stickers)
    }
}