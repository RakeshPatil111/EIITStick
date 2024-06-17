package com.android.stickerpocket.domain.usecase

import com.android.stickerpocket.domain.model.RecentStickerRepository

class FetchRecentStickersUseCase(private val recentStickersRepository: RecentStickerRepository) {
    suspend fun execute() = recentStickersRepository.fetchRecentStickers()
}