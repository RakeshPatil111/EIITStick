package com.android.stickerpocket

import android.app.Application
import com.android.stickerpocket.domain.db.StickerDB
import com.android.stickerpocket.domain.model.RecentStickerRepository
import com.android.stickerpocket.domain.repository.CategoryRepository
import com.android.stickerpocket.domain.repository.EmojiRepository
import com.android.stickerpocket.domain.repository.RecentSearchRepository
import com.android.stickerpocket.domain.repository.FavouritesRepository
import com.android.stickerpocket.domain.repository.StickerRepository
import com.android.stickerpocket.domain.usecase.InsertCategoriesUseCase
import com.android.stickerpocket.domain.usecase.InsertStickersUseCase
import com.android.stickerpocket.dtos.getCategories
import com.android.stickerpocket.dtos.getMappedStickersToCategory
import com.appodeal.ads.Appodeal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class StickerApplication : Application() {
    companion object {
        lateinit var instance: StickerApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        // load data, this will be done only once
        loadDataIntoDB()

        // Initialize Appodeal with your App Key and required ad types
        Appodeal.initialize(
            this,
            "d908f77a97ae0993514bc8edba7e776a36593c77e5f44994",
            Appodeal.BANNER
        )
    }

    private fun loadDataIntoDB() {
        // Create Categories
        CoroutineScope(Dispatchers.IO).launch {
            InsertCategoriesUseCase(categoryRepository).execute(getCategories())
        }
        CoroutineScope(Dispatchers.IO).launch {
            InsertStickersUseCase(stickerRepository).execute(getMappedStickersToCategory())
        }
    }

    val applicationScope = CoroutineScope(SupervisorJob())
    val database by lazy { StickerDB.getDatabase(this, applicationScope) }
    val recentSearchRepository by lazy { RecentSearchRepository(database.recentSearchDAO()) }
    val emojisRepository by lazy { EmojiRepository(database.emojiDAO()) }
    val favouritesRepository by lazy { FavouritesRepository(database.favouritesDAO()) }
    val categoryRepository by lazy { CategoryRepository(database.categoryDAO()) }
    val stickerRepository by lazy { StickerRepository(database.stickerDAO()) }
    val recentStickerRepository by lazy { RecentStickerRepository(database.recentStickerDAO()) }
}