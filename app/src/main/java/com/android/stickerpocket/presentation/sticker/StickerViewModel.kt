package com.android.stickerpocket.presentation.sticker

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.stickerpocket.StickerApplication
import com.android.stickerpocket.domain.model.RecentSearch
import com.android.stickerpocket.domain.usecase.CreateOrUpdatedRecentSearchUseCase
import com.android.stickerpocket.domain.usecase.DeleteRecentSearchUseCase
import com.android.stickerpocket.domain.usecase.GetRecentSearchUseCase
import com.android.stickerpocket.presentation.Sticker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.Date

class StickerViewModel: ViewModel() {

    sealed class Result {
        data class RecentSearches(val recentSearches: List<RecentSearch>): Result()
    }

    private val _liveData = MutableLiveData<Result>()
    val liveData: MutableLiveData<Result> = _liveData

    private var recentSearchUseCase: GetRecentSearchUseCase
    private var createOrUpdatedRecentSearchUseCase: CreateOrUpdatedRecentSearchUseCase
    private var deleteRecentSearchUseCase: DeleteRecentSearchUseCase


    private var recentSearchs: MutableList<RecentSearch> = mutableListOf()
    init {
        deleteRecentSearchUseCase = DeleteRecentSearchUseCase(StickerApplication.instance.recentSearchRepository)
        recentSearchUseCase = GetRecentSearchUseCase(StickerApplication.instance.recentSearchRepository)
        createOrUpdatedRecentSearchUseCase = CreateOrUpdatedRecentSearchUseCase(StickerApplication.instance.recentSearchRepository)
        fetchRecentSearches()
    }

    private fun fetchRecentSearches() {
        CoroutineScope(Dispatchers.Default).launch {
            when (val result = recentSearchUseCase.execute()) {
                is GetRecentSearchUseCase.Result.Success -> {
                    recentSearchs = result.items.toMutableList()
                }
                is GetRecentSearchUseCase.Result.Failure -> {

                }
            }
        }
    }
    fun getRecentSearches() = recentSearchs
    fun updateRecentSearch(position: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val recentSearch = recentSearchs[position].apply {
                this.time = Date().time
            }
            createOrUpdatedRecentSearchUseCase.execute(recentSearch)
        }
    }

    fun saveRecentSearch(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val recentSearch = RecentSearch(query, Date().time)
            createOrUpdatedRecentSearchUseCase.execute(recentSearch)
            fetchRecentSearches()
        }
    }

    fun removeRecentSearch(position: Int) {
        val recentSearchToDelete = recentSearchs[position]
        CoroutineScope(Dispatchers.IO).launch {
            recentSearchs.removeAt(position)
            deleteRecentSearchUseCase.execute(DeleteRecentSearchUseCase.Params(recentSearch = recentSearchToDelete))
            fetchRecentSearches()
        }
    }
    fun downloadSticker(sticker: Sticker) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL(sticker.thumbnail)
                val connection = url.openConnection()
                connection.connect()

                // Create a temporary file in the cache directory
                val gifFile = File(StickerApplication.instance.cacheDir, sticker.title!! + ".gif")

                val inputStream = connection.getInputStream()
                val outputStream = FileOutputStream(gifFile)

                val buffer = ByteArray(1024)
                var bytesRead: Int
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }

                outputStream.close()
                inputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}