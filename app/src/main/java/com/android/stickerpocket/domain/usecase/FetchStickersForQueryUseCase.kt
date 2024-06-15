package com.android.stickerpocket.domain.usecase

import com.android.stickerpocket.domain.model.Sticker
import com.android.stickerpocket.domain.repository.StickerRepository

class FetchStickersForQueryUseCase(private val stickerRepository: StickerRepository) {
    suspend fun execute(query: String) = stickerRepository.fetchStickerForQuery(query)
}