package com.android.stickerpocket.domain.usecase

import com.android.stickerpocket.domain.model.RecentSearch
import com.android.stickerpocket.domain.repository.RecentSearchRepository

class CreateOrUpdatedRecentSearchUseCase(private val recentSearchRepository: RecentSearchRepository) {
    suspend fun execute(recentSearch: RecentSearch) {
        recentSearchRepository.createOrUpdatedRecentSearch(recentSearch)
    }
}