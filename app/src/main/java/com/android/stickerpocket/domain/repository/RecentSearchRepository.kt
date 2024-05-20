package com.android.stickerpocket.domain.repository

import androidx.annotation.WorkerThread
import com.android.stickerpocket.domain.dao.RecentSearchDAO
import com.android.stickerpocket.domain.model.RecentSearch
import kotlinx.coroutines.flow.Flow

class RecentSearchRepository(private val dao: RecentSearchDAO) {
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun fetchAll(): List<RecentSearch> {
        return dao.getRecentSearches()
    }

    suspend fun createOrUpdatedRecentSearch(recentSearch: RecentSearch) {
        dao.createOrUpdateRecentSearch(recentSearch)
    }

    suspend fun deleteRecentSearch(recentSearch: RecentSearch) {
        dao.deleteRecentSearch(recentSearch)
    }

}