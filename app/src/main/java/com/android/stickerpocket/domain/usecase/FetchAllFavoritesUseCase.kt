package com.android.stickerpocket.domain.usecase

import com.android.stickerpocket.domain.model.Favourites
import com.android.stickerpocket.domain.repository.StickerRepository

class FetchAllFavoritesUseCase(private val stickerRepository: StickerRepository) {

    suspend fun execute(): List<Favourites>{
        return stickerRepository.fetchAllFavorites()
    }
}