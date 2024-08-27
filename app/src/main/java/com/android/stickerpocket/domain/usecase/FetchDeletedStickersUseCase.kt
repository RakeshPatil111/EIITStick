package com.android.stickerpocket.domain.usecase

import com.android.stickerpocket.domain.repository.StickerRepository

class FetchDeletedStickersUseCase (private val stickerRepository: StickerRepository) {
    suspend fun execute() = stickerRepository.fetchDeletedStickers()
}