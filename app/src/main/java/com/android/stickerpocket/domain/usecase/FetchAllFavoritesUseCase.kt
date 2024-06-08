package com.android.stickerpocket.domain.usecase

import com.android.stickerpocket.domain.model.Favourites
import com.android.stickerpocket.domain.repository.StickerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class FetchAllFavoritesUseCase(private val stickerRepository: StickerRepository) {

    sealed class Result {
        data class Success(val data: List<Favourites>) : Result()
        data class Error(val message: String) : Result()
    }
    private var list: List<Favourites>? = null
    suspend fun execute(): Flow<Result> =
        stickerRepository.fetchAllFavorites()
            .map {
                list = it
                Result.Success(list!!)
            }
            .flowOn(Dispatchers.IO)
}