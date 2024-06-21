package com.android.stickerpocket.domain.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.android.stickerpocket.domain.model.Sticker
import kotlinx.coroutines.flow.Flow

@Dao
interface StickerDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(stickers: List<Sticker>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSticker(sticker: Sticker)

    @Update
    suspend fun update(sticker: Sticker)

    @Query("SELECT * FROM sticker WHERE isFavourite = 1 AND isDeleted = 0 ORDER BY position ASC")
    fun fetchAllFavouritesSticker(): Flow<List<Sticker>>

    @Query("SELECT * FROM sticker WHERE isDownloaded = 1 AND isDeleted = 0")
    fun getDownloadedStickers(): Flow<List<Sticker>>

    @Query("SELECT * FROM sticker WHERE isDeleted = 0 ORDER BY position ASC")
    suspend fun fetchAll(): List<Sticker>

    @Query("SELECT * FROM sticker WHERE categoryId = :id AND isDeleted = 0 ORDER BY position ASC")
    fun fetchForCategory(id: Int): Flow<List<Sticker>>

    @Query("SELECT * FROM sticker WHERE tags AND isDeleted = 0 LIKE '%' || :query || '%'")
    fun fetchStickersForQuery(query: String): List<Sticker>

    @Query("SELECT * FROM sticker WHERE mediaId= :mediaId")
    fun checkDuplicate(mediaId: String): List<Sticker>
    @Query("SELECT * FROM sticker WHERE id= :id")
    fun fetch(id: Int): Sticker?
}