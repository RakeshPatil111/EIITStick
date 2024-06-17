package com.android.stickerpocket.domain.model

import com.android.stickerpocket.domain.dao.RecentStickerDAO

class RecentStickerRepository(val dao: RecentStickerDAO) {
    suspend fun fetchRecentStickers() = dao.fetchRecentStickers()
    suspend fun insert(recentStickers: RecentStickers) = dao.insert(recentStickers)
}