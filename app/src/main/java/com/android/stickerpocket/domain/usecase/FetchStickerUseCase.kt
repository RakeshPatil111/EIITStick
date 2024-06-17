package com.android.stickerpocket.domain.usecase

import com.android.stickerpocket.domain.repository.StickerRepository

class FetchStickerUseCase (private val stickerRepository: StickerRepository) {
    suspend fun execute(id: Int) = stickerRepository.fetch(id)
}