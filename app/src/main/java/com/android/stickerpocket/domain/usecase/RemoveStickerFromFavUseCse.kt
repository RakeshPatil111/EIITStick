package com.android.stickerpocket.domain.usecase

import com.android.stickerpocket.domain.model.Favourites
import com.android.stickerpocket.domain.repository.FavouritesRepository

class RemoveStickerFromFavUseCse(private val favouritesRepository: FavouritesRepository) {

    suspend fun execute(favourites: Favourites){
        favouritesRepository.addStickerToFavorites(favourites)
    }
}