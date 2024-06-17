package com.android.stickerpocket.domain.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.android.stickerpocket.domain.dao.CategoryDAO
import com.android.stickerpocket.domain.dao.EmojiDAO
import com.android.stickerpocket.domain.dao.RecentSearchDAO
import com.android.stickerpocket.domain.dao.FavouritesDAO
import com.android.stickerpocket.domain.dao.RecentStickerDAO
import com.android.stickerpocket.domain.dao.StickerDAO
import com.android.stickerpocket.domain.model.Category
import com.android.stickerpocket.domain.model.Emoji
import com.android.stickerpocket.domain.model.Favourites
import com.android.stickerpocket.domain.model.RecentSearch
import com.android.stickerpocket.domain.model.RecentStickers
import com.android.stickerpocket.domain.model.Sticker
import kotlinx.coroutines.CoroutineScope

@Database(entities = [RecentSearch::class, Emoji::class, Category::class,
    Favourites::class, Sticker::class, RecentStickers::class],
    version = 1,
    exportSchema = false)
abstract class StickerDB : RoomDatabase() {

    abstract fun recentSearchDAO(): RecentSearchDAO
    abstract fun emojiDAO(): EmojiDAO
    abstract fun favouritesDAO(): FavouritesDAO
    abstract fun categoryDAO(): CategoryDAO
    abstract fun stickerDAO(): StickerDAO
    abstract fun recentStickerDAO(): RecentStickerDAO

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: StickerDB? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): StickerDB {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StickerDB::class.java,
                    "sticker_db"
                )
                    //.addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
//        val MIGRATION_1_2 = object : Migration(1, 2) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL("CREATE TABLE `Favourites` (`id` INTEGER, `mediaId` TEXT, `url` TEXT, " +
//                        "`position` INTEGER, `name` TEXT, `date` Long " +
//                        "PRIMARY KEY(`id`))")
//            }
//        }
//    }

//    val MIGRATION_2_3 = object : Migration(2, 3) {
//        override fun migrate(database: SupportSQLiteDatabase) {
//            database.execSQL("ALTER TABLE Book ADD COLUMN pub_year INTEGER")
//        }
//    }
    }
}