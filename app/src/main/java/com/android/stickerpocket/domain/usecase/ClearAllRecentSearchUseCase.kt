package com.android.stickerpocket.domain.usecase

import com.android.stickerpocket.domain.repository.RecentSearchRepository

class ClearAllRecentSearchUseCase(private val recentSearchRepository: RecentSearchRepository) {

    suspend fun execute(){
        recentSearchRepository.deleteAllRecentSearch()
    }
}