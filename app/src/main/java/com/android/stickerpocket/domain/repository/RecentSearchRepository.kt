package com.android.stickerpocket.domain.repository

import com.android.stickerpocket.domain.dao.RecentSearchDAO
import com.android.stickerpocket.domain.model.RecentSearch

class RecentSearchRepository(private val dao: RecentSearchDAO) {
    suspend fun fetchAll(): List<RecentSearch> {
        return dao.getRecentSearches()
    }
}