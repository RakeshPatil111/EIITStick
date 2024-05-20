package com.android.stickerpocket.domain.usecase

import com.android.stickerpocket.domain.model.RecentSearch
import com.android.stickerpocket.domain.repository.RecentSearchRepository

class DeleteRecentSearchUseCase(private val recentSearchRepository: RecentSearchRepository) {
    sealed class Result {
        object Success : Result()
        object Failure : Result()
    }
    suspend fun execute(params: Params): Result {
        return try {
            recentSearchRepository.deleteRecentSearch(params.recentSearch)
            Result.Success
        } catch (e: Exception) {
            Result.Failure
        }
    }
    data class Params(val recentSearch: RecentSearch)
}