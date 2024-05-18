package com.android.stickerpocket.domain.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.android.stickerpocket.domain.dao.EmojiDAO
import com.android.stickerpocket.domain.dao.RecentSearchDAO
import com.android.stickerpocket.domain.model.Category
import com.android.stickerpocket.domain.model.Emoji
import com.android.stickerpocket.domain.model.Favourites
import com.android.stickerpocket.domain.model.RecentSearch

@Database(entities = [RecentSearch::class, Emoji::class, Category::class, Favourites::class], version = 1, exportSchema = false)
public abstract class StickerDB : RoomDatabase() {

    abstract fun recentSearchDAO(): RecentSearchDAO
    abstract fun emojiDAO(): EmojiDAO

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: StickerDB? = null

        fun getDatabase(context: Context): StickerDB {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StickerDB::class.java,
                    "sticker_db"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}