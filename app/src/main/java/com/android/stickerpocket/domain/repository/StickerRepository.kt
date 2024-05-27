package com.android.stickerpocket.domain.repository

import com.android.stickerpocket.domain.dao.StickerDAO
import com.android.stickerpocket.domain.model.Favourites
import kotlinx.coroutines.flow.Flow

class StickerRepository(private val stickerDAO: StickerDAO) {

    suspend fun addStickerToFavorites(favourites: Favourites) = stickerDAO.addToFavorites(favourites)

    suspend fun fetchAllFavorites(): Flow<List<Favourites>> = stickerDAO.fetchAllFavorites()
}