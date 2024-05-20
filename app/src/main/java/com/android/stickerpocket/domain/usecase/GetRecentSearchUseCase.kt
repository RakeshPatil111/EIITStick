package com.android.stickerpocket.domain.usecase

import com.android.stickerpocket.domain.model.RecentSearch
import com.android.stickerpocket.domain.repository.RecentSearchRepository


class GetRecentSearchUseCase(private val recentSearchRepository: RecentSearchRepository) {

    sealed class Result {
        data class Success(val items: List<RecentSearch>): Result()
        object Failure: Result()
    }

    suspend fun execute(): Result {
        return try {
            val recentSearches = recentSearchRepository.fetchAll()
            Result.Success(recentSearches)
        } catch (e: Exception) {
            println(e.message)
            Result.Failure
        }
    }
}