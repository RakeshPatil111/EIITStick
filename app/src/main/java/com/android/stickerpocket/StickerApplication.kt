package com.android.stickerpocket

import android.app.Application
import com.android.stickerpocket.domain.db.StickerDB
import com.android.stickerpocket.domain.repository.EmojiRepository
import com.android.stickerpocket.domain.repository.RecentSearchRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class StickerApplication: Application() {
    companion object {
        lateinit var instance: StickerApplication
            private set
    }
    override fun onCreate() {
        super.onCreate()
        instance = this
    }
    val applicationScope = CoroutineScope(SupervisorJob())
    val database by lazy { StickerDB.getDatabase(this, applicationScope) }
    val recentSearchRepository by lazy { RecentSearchRepository(database.recentSearchDAO()) }
    val emojisRepository by lazy { EmojiRepository(database.emojiDAO()) }
}