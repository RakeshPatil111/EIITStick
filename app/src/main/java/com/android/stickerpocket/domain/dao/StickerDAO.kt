package com.android.stickerpocket.domain.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.android.stickerpocket.domain.model.Favourites

@Dao
interface StickerDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addToFavorites(favourites: Favourites)

    @Query("SELECT * FROM Favourites")
    fun fetchAllFavorites(): List<Favourites>
}