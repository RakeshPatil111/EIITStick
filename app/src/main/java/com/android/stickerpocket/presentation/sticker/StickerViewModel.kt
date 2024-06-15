package com.android.stickerpocket.presentation.sticker

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.stickerpocket.StickerApplication
import com.android.stickerpocket.domain.model.Category
import com.android.stickerpocket.domain.model.Favourites
import com.android.stickerpocket.domain.model.RecentSearch
import com.android.stickerpocket.domain.model.Sticker
import com.android.stickerpocket.domain.usecase.AddToFavoritesUseCase
import com.android.stickerpocket.domain.usecase.ClearAllRecentSearchUseCase
import com.android.stickerpocket.domain.usecase.CreateOrUpdatedRecentSearchUseCase
import com.android.stickerpocket.domain.usecase.DeleteRecentSearchUseCase
import com.android.stickerpocket.domain.usecase.FetchAllFavoritesUseCase
import com.android.stickerpocket.domain.usecase.FetchCategoriesUseCase
import com.android.stickerpocket.domain.usecase.FetchEmojiByEmojiIcon
import com.android.stickerpocket.domain.usecase.FetchStickersForCategoryUseCase
import com.android.stickerpocket.domain.usecase.FetchStickersForQueryUseCase
import com.android.stickerpocket.domain.usecase.GetRecentSearchUseCase
import com.android.stickerpocket.domain.usecase.InsertOrReplaceCategoriesUseCase
import com.android.stickerpocket.domain.usecase.UpdateStickerUseCase
import com.android.stickerpocket.dtos.getCategories
import com.android.stickerpocket.utils.StickerExt.toFavorite
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.Date

class StickerViewModel : ViewModel() {

    sealed class Result {
        data class RecentSearches(val recentSearches: List<RecentSearch>) : Result()
        object CategoryCreated : Result()
        object CreateCatFailure : Result()
        data class StickersReceivedForCategory(val stickers: List<Sticker>) : Result()
        object FavouritesStickerUpdated : Result()
        data class StickersWithQuery(val query: String, val stickers: List<Sticker>) : Result()
    }

    private val _liveData = MutableLiveData<Result>()
    val liveData: MutableLiveData<Result> = _liveData

    private var recentSearchUseCase: GetRecentSearchUseCase
    private var createOrUpdatedRecentSearchUseCase: CreateOrUpdatedRecentSearchUseCase
    private var deleteRecentSearchUseCase: DeleteRecentSearchUseCase
    private var addToFavoritesUseCase: AddToFavoritesUseCase
    private var fetchAllFavoritesUseCase: FetchAllFavoritesUseCase
    private var fetchCategory: FetchCategoriesUseCase
    private var insertCategoriesUseCase: InsertOrReplaceCategoriesUseCase
    private var clearAllRecentSearchUseCase: ClearAllRecentSearchUseCase
    private var fetchEMojiByIcon: FetchEmojiByEmojiIcon
    private var recentSearches: MutableList<RecentSearch> = mutableListOf()
    private var favourites: MutableList<Sticker> = mutableListOf()
    private var recentSearchs: MutableList<RecentSearch> = mutableListOf()
    private var categories = mutableListOf<Category>()
    private var stickers = mutableListOf<com.android.stickerpocket.domain.model.Sticker>()
    private var fromPosition: Int? = null
    private var toPosition = 0
    private val fetchStickersForCategoryUseCase: FetchStickersForCategoryUseCase
    private val updateStickerUseCase: UpdateStickerUseCase
    private val fetchStickersForQueryUseCase: FetchStickersForQueryUseCase

    init {
        deleteRecentSearchUseCase =
            DeleteRecentSearchUseCase(StickerApplication.instance.recentSearchRepository)
        recentSearchUseCase =
            GetRecentSearchUseCase(StickerApplication.instance.recentSearchRepository)
        createOrUpdatedRecentSearchUseCase =
            CreateOrUpdatedRecentSearchUseCase(StickerApplication.instance.recentSearchRepository)
        fetchCategory = FetchCategoriesUseCase(StickerApplication.instance.categoryRepository)
        insertCategoriesUseCase =
            InsertOrReplaceCategoriesUseCase(StickerApplication.instance.categoryRepository)
        clearAllRecentSearchUseCase =
            ClearAllRecentSearchUseCase(StickerApplication.instance.recentSearchRepository)
        fetchEMojiByIcon = FetchEmojiByEmojiIcon(StickerApplication.instance.emojisRepository)
        addToFavoritesUseCase =
            AddToFavoritesUseCase(StickerApplication.instance.favouritesRepository)
        fetchAllFavoritesUseCase =
            FetchAllFavoritesUseCase(StickerApplication.instance.stickerRepository)
        fetchStickersForCategoryUseCase =
            FetchStickersForCategoryUseCase(StickerApplication.instance.stickerRepository)
        updateStickerUseCase = UpdateStickerUseCase(StickerApplication.instance.stickerRepository)
        fetchStickersForQueryUseCase =
            FetchStickersForQueryUseCase(StickerApplication.instance.stickerRepository)
        fetchRecentSearches()
        fetchCategories()
        fetchAllFavorites()
    }

    private fun fetchCategories() {
        viewModelScope.launch {
            fetchCategory.execute()
                .collectLatest {
                    when (it) {
                        is FetchCategoriesUseCase.Result.Success -> {
                            categories = it.categories.toMutableList()
                                .ifEmpty { getCategories().toMutableList() }
                            _liveData.postValue(Result.CategoryCreated)
                            val selectedCategory =
                                categories.filter { it.isHighlighted == true }.first().id
                            fetchStickersForCategory(selectedCategory)
                        }

                        is FetchCategoriesUseCase.Result.Failure -> {
                            categories = getCategories().toMutableList()
                        }
                    }
                }

        }
    }

    private fun fetchStickersForCategory(selectedCategory: Int?) {
        selectedCategory?.let {
            CoroutineScope(Dispatchers.IO).launch {
                fetchStickersForCategoryUseCase.execute(it)
                    .collectLatest {
                        stickers = it.toMutableList()
                        _liveData.postValue(Result.StickersReceivedForCategory(stickers.toList()))
                    }
            }
        }
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

    fun clearAllRecentSearch() {
        CoroutineScope(Dispatchers.IO).launch {
            recentSearchs.clear()
            clearAllRecentSearchUseCase.execute()
        }
    }

    fun downloadSticker(sticker: com.android.stickerpocket.presentation.Sticker) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL(sticker.thumbnail)
                val connection = url.openConnection()
                connection.connect()

                // Create a temporary file in the cache directory
                val gifFile =
                    File(StickerApplication.instance.cacheDir, sticker.title!! + ".gif")

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

    fun addToFavorites(sticker: Sticker) {
        CoroutineScope(Dispatchers.IO).launch {
            //addToFavoritesUseCase.execute(sticker.toFavorite())
            sticker.isFavourite = true
            updateStickerUseCase.execute(sticker)
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

    fun getEmojiCategories() = categories

    fun createCategory(unicode: String, pos: Int) {
        CoroutineScope(Dispatchers.Default).launch {
            fetchEMojiByIcon.execute(unicode.lowercase())?.let {
                val newCategory = Category(
                    unicode = unicode,
                    position = pos,
                    isHighlighted = true,
                    isDeleted = false,
                    name = it.name,
                    html = it.html
                )
                categories.add(pos, newCategory)
                // change position
                categories.forEachIndexed { index, category ->
                    category.position = index
                }
                updateCategories()
            } ?: _liveData.postValue(Result.CreateCatFailure)
        }
    }

    fun itemMoved(fromPosition: Int, toPosition: Int) {
        if (this.fromPosition == null) {
            this.fromPosition = fromPosition
        }
        this.toPosition = toPosition
    }

    fun reArrangeCategory() {
        if (fromPosition != toPosition) {
            val category = categories[fromPosition!!]
            categories.removeAt(fromPosition!!)
            categories.add(toPosition, category)
            categories.forEachIndexed { index, category ->
                category.position = index
            }
            updateCategories()
            fromPosition = null
        }
    }

    fun deleteCategory(category: Category, pos: Int) {
        categories[pos].apply {
            this.isDeleted = true
            this.isHighlighted = false
        }
        updateCategories()
    }

    fun categorySelected(c: Category, previous: Int) {
        fetchStickersForCategory(c.id)
        categories.forEachIndexed { index, category ->
            if (c.unicode == category.unicode) {
                category.isHighlighted = true
            } else {
                category.isHighlighted = false
            }
        }
        updateCategories()
    }

    private fun updateCategories() {
        CoroutineScope(Dispatchers.Default).launch {
            insertCategoriesUseCase.execute(categories)
        }
    }

    fun removeStickerFromFav(sticker: Sticker) {
        CoroutineScope(Dispatchers.IO).launch {
            sticker.isFavourite = false
            updateStickerUseCase.execute(sticker)
            _liveData.postValue(Result.FavouritesStickerUpdated)
        }
    }

    fun getStickersForQuery(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val list = fetchStickersForQueryUseCase.execute(query)
            _liveData.postValue(Result.StickersWithQuery(query, list))
        }
    }
}