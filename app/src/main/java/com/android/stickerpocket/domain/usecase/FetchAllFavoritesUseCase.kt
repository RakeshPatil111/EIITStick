package com.android.stickerpocket.domain.usecase

import com.android.stickerpocket.domain.model.Sticker
import com.android.stickerpocket.domain.repository.StickerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class FetchAllFavoritesUseCase(private val repository: StickerRepository) {

    sealed class Result {
        data class Success(val data: List<Sticker>) : Result()
        data class Error(val message: String) : Result()
    }
    private var list: List<Sticker>? = null
    suspend fun execute(): Flow<Result> =
        repository.fetchFavourites()
            .map {
                list = it
                Result.Success(list!!)
            }
            .flowOn(Dispatchers.IO)
}