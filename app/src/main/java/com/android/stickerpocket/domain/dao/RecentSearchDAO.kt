package com.android.stickerpocket.domain.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.android.stickerpocket.domain.model.RecentSearch
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentSearchDAO {
    @Query("SELECT * FROM RecentSearch ORDER BY time DESC")
    fun getRecentSearches(): List<RecentSearch>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createOrUpdateRecentSearch(recentSearch: RecentSearch)
}