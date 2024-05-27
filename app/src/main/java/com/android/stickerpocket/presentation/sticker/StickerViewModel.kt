package com.android.stickerpocket.presentation.sticker

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.stickerpocket.StickerApplication
import com.android.stickerpocket.domain.model.Favourites
import com.android.stickerpocket.domain.model.RecentSearch
import com.android.stickerpocket.domain.usecase.AddToFavoritesUseCase
import com.android.stickerpocket.domain.usecase.CreateOrUpdatedRecentSearchUseCase
import com.android.stickerpocket.domain.usecase.DeleteRecentSearchUseCase
import com.android.stickerpocket.domain.usecase.FetchAllFavoritesUseCase
import com.android.stickerpocket.domain.usecase.GetRecentSearchUseCase
import com.android.stickerpocket.presentation.Sticker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.Date

class StickerViewModel : ViewModel() {

    sealed class Result {
        data class RecentSearches(val recentSearches: List<RecentSearch>) : Result()
    }

    private val _liveData = MutableLiveData<Result>()
    val liveData: MutableLiveData<Result> = _liveData

    private var recentSearchUseCase: GetRecentSearchUseCase
    private var createOrUpdatedRecentSearchUseCase: CreateOrUpdatedRecentSearchUseCase
    private var deleteRecentSearchUseCase: DeleteRecentSearchUseCase
    private var addToFavoritesUseCase: AddToFavoritesUseCase
    private var fetchAllFavoritesUseCase: FetchAllFavoritesUseCase

    private var recentSearches: MutableList<RecentSearch> = mutableListOf()
    private var favourites: MutableList<Favourites> = mutableListOf()

    init {
        deleteRecentSearchUseCase =
            DeleteRecentSearchUseCase(StickerApplication.instance.recentSearchRepository)
        recentSearchUseCase =
            GetRecentSearchUseCase(StickerApplication.instance.recentSearchRepository)
        createOrUpdatedRecentSearchUseCase =
            CreateOrUpdatedRecentSearchUseCase(StickerApplication.instance.recentSearchRepository)
        addToFavoritesUseCase =
            AddToFavoritesUseCase(StickerApplication.instance.stickerRepository)
        fetchAllFavoritesUseCase =
            FetchAllFavoritesUseCase(StickerApplication.instance.stickerRepository)
        fetchRecentSearches()
        fetchAllFavorites()
    }

    private fun fetchRecentSearches() {
        CoroutineScope(Dispatchers.Default).launch {
            when (val result = recentSearchUseCase.execute()) {
                is GetRecentSearchUseCase.Result.Success -> {
                    recentSearches = result.items.toMutableList()
                }

                is GetRecentSearchUseCase.Result.Failure -> {

                }
            }
        }
    }

    fun getRecentSearches() = recentSearches
    fun updateRecentSearch(position: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val recentSearch = recentSearches[position].apply {
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
        val recentSearchToDelete = recentSearches[position]
        CoroutineScope(Dispatchers.IO).launch {
            recentSearches.removeAt(position)
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

    fun addToFavorites(favourites: Favourites){
        CoroutineScope(Dispatchers.IO).launch {
            addToFavoritesUseCase.execute(favourites)
        }
    }

    fun fetchAllFavorites() {
        viewModelScope.launch {
            fetchAllFavoritesUseCase.execute()
                .collectLatest {
                    when (it) {
                        is FetchAllFavoritesUseCase.Result.Success -> {
                            favourites = it.data.toMutableList()
                        }
                        else -> {}
                    }
                }
        }
    }

    fun getFavourites() = favourites
}