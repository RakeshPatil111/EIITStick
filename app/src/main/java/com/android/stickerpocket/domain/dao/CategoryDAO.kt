package com.android.stickerpocket.domain.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.android.stickerpocket.domain.model.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDAO {
    @Query("SELECT * FROM Category")
    fun fetchAll(): Flow< List<Category>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(categories: List<Category>)
}