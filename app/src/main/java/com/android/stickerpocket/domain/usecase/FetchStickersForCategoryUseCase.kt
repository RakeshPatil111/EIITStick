package com.android.stickerpocket.domain.usecase

import com.android.stickerpocket.domain.model.Sticker
import com.android.stickerpocket.domain.repository.StickerRepository
import kotlinx.coroutines.flow.Flow

class FetchStickersForCategoryUseCase (private val repository: StickerRepository) {
    suspend fun execute(categoryId: Int): Flow<List<Sticker>> {
        return repository.fetchStickersForCategory(categoryId)
    }
}