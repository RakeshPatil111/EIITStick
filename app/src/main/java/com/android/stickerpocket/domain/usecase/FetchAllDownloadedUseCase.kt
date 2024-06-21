package com.android.stickerpocket.domain.usecase

import com.android.stickerpocket.domain.model.Sticker
import com.android.stickerpocket.domain.repository.StickerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class FetchAllDownloadedUseCase(private val repository: StickerRepository) {
    sealed class Result {
        data class Success(val list: List<Sticker>): Result()
    }

    private var list: List<Sticker> = listOf()

    suspend fun execute(): Flow<Result> =
        repository.getDownloaded()
            .map {
                list = it
                Result.Success(list)
            }
            .flowOn(Dispatchers.IO)
}