package com.android.stickerpocket.domain.repository

import com.android.stickerpocket.domain.dao.StickerDAO
import com.android.stickerpocket.domain.model.Sticker

class StickerRepository(private val dao: StickerDAO) {
    suspend fun getDownloaded() = dao.getDownloadedStickers()

    suspend fun fetchFavourites() = dao.fetchAllFavouritesSticker()

    suspend fun updateSticker(sticker: Sticker) = dao.update(sticker)

    suspend fun insertAll(stickers: List<Sticker>) = dao.insertAll(stickers)

    suspend fun fetchAll() = dao.fetchAll()

    suspend fun fetchStickersForCategory(id: Int) = dao.fetchForCategory(id)

    suspend fun fetchStickerForQuery(query: String) = dao.fetchStickersForQuery(query)

    suspend fun insertSticker(sticker: Sticker) = dao.insertSticker(sticker)

    suspend fun checkDuplicate(mediaId: String) = dao.checkDuplicate(mediaId)

    suspend fun fetch(id: Int) = dao.fetch(id)

    fun fetchStickerCountForCategory(id: Int) = dao.stickerCountInCategory(id)
}