package com.android.stickerpocket.domain.usecase

import com.android.stickerpocket.domain.repository.StickerRepository

class FetchStickerCountInCategoryUseCase (private val stickerRepository: StickerRepository) {
    suspend fun execute(categoryId: Int) = stickerRepository.fetchStickerCountForCategory(categoryId)
}