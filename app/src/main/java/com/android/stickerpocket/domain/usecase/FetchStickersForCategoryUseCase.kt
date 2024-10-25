package com.android.stickerpocket.domain.usecase

import com.android.stickerpocket.domain.model.Sticker
import com.android.stickerpocket.domain.repository.StickerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class FetchStickersForCategoryUseCase (private val repository: StickerRepository) {
    sealed class Result {
        data class Success(val stickers: List<Sticker>) : Result()
        object Failure: Result()
    }
    suspend fun execute(categoryId: Int): Flow<Result> {
        return repository.fetchStickersForCategory(categoryId)
            .map {
                Result.Success(it)
            }
            .flowOn(Dispatchers.Main)
    }
}