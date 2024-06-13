package com.android.stickerpocket.domain.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.android.stickerpocket.domain.model.Favourites
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouritesDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addToFavorites(favourites: Favourites)

    @Query("SELECT * FROM Favourites ORDER BY date DESC")
    fun fetchAllFavorites(): Flow<List<Favourites>>
}