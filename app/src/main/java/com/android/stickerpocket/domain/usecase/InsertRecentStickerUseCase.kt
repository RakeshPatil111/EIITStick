package com.android.stickerpocket.domain.usecase

import com.android.stickerpocket.domain.model.RecentStickerRepository
import com.android.stickerpocket.domain.model.RecentStickers

class InsertRecentStickerUseCase(private val recentStickerRepository: RecentStickerRepository) {
    suspend fun execute(recentStickers: RecentStickers) {
        recentStickerRepository.insert(recentStickers)
    }
}