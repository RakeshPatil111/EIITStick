package com.android.stickerpocket.domain.usecase

import com.android.stickerpocket.domain.model.Favourites
import com.android.stickerpocket.domain.repository.StickerRepository

class AddToFavoritesUseCase(private val stickerRepository: StickerRepository) {

    suspend fun execute(favourites: Favourites){
        stickerRepository.addStickerToFavorites(favourites)
    }
}