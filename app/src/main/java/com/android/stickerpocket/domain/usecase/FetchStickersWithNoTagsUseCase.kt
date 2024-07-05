package com.android.stickerpocket.domain.usecase

import com.android.stickerpocket.domain.repository.StickerRepository

class FetchStickersWithNoTagsUseCase(private val repository: StickerRepository) {
    suspend fun execute() = repository.fetchStickersWithNoTags()
}