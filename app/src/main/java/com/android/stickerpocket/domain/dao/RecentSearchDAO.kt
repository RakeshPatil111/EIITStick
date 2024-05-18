package com.android.stickerpocket.domain.dao

import androidx.room.Dao
import androidx.room.Query
import com.android.stickerpocket.domain.model.RecentSearch

@Dao
interface RecentSearchDAO {
    @Query("SELECT * FROM RecentSearch ORDER BY time DESC")
    fun getRecentSearches(): List<RecentSearch>
}