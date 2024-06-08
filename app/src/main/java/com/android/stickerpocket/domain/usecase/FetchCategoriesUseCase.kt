package com.android.stickerpocket.domain.usecase

import android.util.Log
import com.android.stickerpocket.domain.model.Category
import com.android.stickerpocket.domain.repository.CategoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class FetchCategoriesUseCase (private val repository: CategoryRepository) {
    private var list = listOf<Category>()
    sealed class Result {
        data class Success(val categories: List<Category>) : Result()
        data object Failure: Result()
    }
    suspend fun execute(): Flow<Result> =
        repository.fetchAll()
            .map {
                list = it
                Result.Success(list)
            }
            .flowOn(Dispatchers.IO)
}