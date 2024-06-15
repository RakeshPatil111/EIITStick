package com.android.stickerpocket.domain.repository

import com.android.stickerpocket.domain.dao.FavouritesDAO
import com.android.stickerpocket.domain.model.Favourites
import kotlinx.coroutines.flow.Flow

class FavouritesRepository(private val stickerDAO: FavouritesDAO) {

    suspend fun addStickerToFavorites(favourites: Favourites) = stickerDAO.addToFavorites(favourites)

    suspend fun fetchAllFavorites(): Flow<List<Favourites>> = stickerDAO.fetchAllFavorites()
}