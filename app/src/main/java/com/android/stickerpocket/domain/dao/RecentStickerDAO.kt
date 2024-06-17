package com.android.stickerpocket.domain.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.android.stickerpocket.domain.model.RecentStickerAndStickers
import com.android.stickerpocket.domain.model.RecentStickers

@Dao
interface RecentStickerDAO {
    @Query("SELECT * FROM recentstickers")
    fun fetchRecentStickers(): List<RecentStickerAndStickers>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(recentSticker: RecentStickers)
}