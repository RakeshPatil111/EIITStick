package com.android.stickerpocket.domain.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.android.stickerpocket.domain.model.Emoji

@Dao
interface EmojiDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveEmojis(emojis: List<Emoji>)

    @Query("SELECT name FROM emoji WHERE unicode = :unicode")
    fun getEmojiName(unicode: String): String?

    @Query("SELECT EXISTS (SELECT * FROM emoji)")
    fun doesEmojisExist(): Boolean
}