package com.android.stickerpocket.presentation.sticker

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.stickerpocket.R
import com.android.stickerpocket.StickerApplication
import com.android.stickerpocket.domain.model.Category
import com.android.stickerpocket.domain.model.Emoji
import com.android.stickerpocket.domain.model.RecentSearch
import com.android.stickerpocket.domain.model.RecentStickers
import com.android.stickerpocket.domain.model.Sticker
import com.android.stickerpocket.domain.usecase.AddEmojiIfNotExistUseCase
import com.android.stickerpocket.domain.usecase.AddToFavoritesUseCase
import com.android.stickerpocket.domain.usecase.ClearAllRecentSearchUseCase
import com.android.stickerpocket.domain.usecase.CreateOrUpdatedRecentSearchUseCase
import com.android.stickerpocket.domain.usecase.DeleteRecentSearchUseCase
import com.android.stickerpocket.domain.usecase.FetchAllDownloadedUseCase
import com.android.stickerpocket.domain.usecase.FetchAllFavoritesUseCase
import com.android.stickerpocket.domain.usecase.FetchCategoriesUseCase
import com.android.stickerpocket.domain.usecase.FetchEmojiByEmojiIcon
import com.android.stickerpocket.domain.usecase.FetchRecentStickersUseCase
import com.android.stickerpocket.domain.usecase.FetchStickerCountInCategoryUseCase
import com.android.stickerpocket.domain.usecase.FetchStickerUseCase
import com.android.stickerpocket.domain.usecase.FetchStickersForCategoryUseCase
import com.android.stickerpocket.domain.usecase.FetchStickersForQueryUseCase
import com.android.stickerpocket.domain.usecase.GetRecentSearchUseCase
import com.android.stickerpocket.domain.usecase.InsertOrReplaceCategoriesUseCase
import com.android.stickerpocket.domain.usecase.InsertRecentStickerUseCase
import com.android.stickerpocket.domain.usecase.InsertSingleStickersUseCase
import com.android.stickerpocket.domain.usecase.InsertStickersUseCase
import com.android.stickerpocket.domain.usecase.UpdateStickerUseCase
import com.android.stickerpocket.dtos.getCategories
import com.android.stickerpocket.network.response.Emojis
import com.android.stickerpocket.presentation.StickerDTO
import com.android.stickerpocket.utils.StickerExt.sticker
import com.android.stickerpocket.utils.StickerExt.toFile
import com.android.stickerpocket.utils.StickerExt.toStickerDTO
import com.android.stickerpocket.utils.toEmoji
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.net.URL
import java.util.Date

class StickerViewModel : ViewModel() {

    sealed class Result {
        data class RecentSearches(val recentSearches: List<RecentSearch>) : Result()
        object CategoryCreated : Result()
        object CreateCatFailure : Result()
        data class StickersReceivedForCategory(val stickers: List<Sticker>) : Result()
        data class FetchedDownloadedStickers(val stickers: List<Sticker>) : Result()
        object FavouritesStickerUpdated : Result()
        data class StickersWithQuery(val query: String, val stickers: List<Sticker>) : Result()
        data class ShowProgress(val showProgress: Boolean) : Result()
        data class StickerDownloaded(val gifFile: File) : Result()
        data class ShareSticker(val gifFile: File) : Result()
        data class RecentSearchCleared(val searches: List<RecentSearch>) : Result()
        class FetchedRecentStickers(val list: List<Sticker>) : Result()
        object StickerDTOUpdated : Result()
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
    private var stickers = mutableListOf<Sticker>()
    private var fromPosition: Int? = null
    private var toPosition = 0
    private val fetchStickersForCategoryUseCase: FetchStickersForCategoryUseCase
    private val updateStickerUseCase: UpdateStickerUseCase
    private val fetchStickersForQueryUseCase: FetchStickersForQueryUseCase
    private val insertSingleStickersUseCase: InsertSingleStickersUseCase
    private val fetchAllDownloadedUseCase: FetchAllDownloadedUseCase
    private val insertStickersUseCase: InsertStickersUseCase
    private var addEmojiIfNotExistUseCase =
        AddEmojiIfNotExistUseCase(StickerApplication.instance.emojisRepository)
    private var stickerDTO: StickerDTO? = null
    private var fetchStickerUseCase: FetchStickerUseCase
    private val fetchRecentStickersUseCase: FetchRecentStickersUseCase
    private val insertRecentStickersUseCase: InsertRecentStickerUseCase
    private var currentViewMode = ViewMode.Category
    private val fetchStickerCountInCategoryUseCase: FetchStickerCountInCategoryUseCase
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
        insertSingleStickersUseCase =
            InsertSingleStickersUseCase(StickerApplication.instance.stickerRepository)
        fetchAllDownloadedUseCase =
            FetchAllDownloadedUseCase(StickerApplication.instance.stickerRepository)
        insertStickersUseCase = InsertStickersUseCase(StickerApplication.instance.stickerRepository)
        fetchStickerUseCase = FetchStickerUseCase(StickerApplication.instance.stickerRepository)
        insertRecentStickersUseCase = InsertRecentStickerUseCase(StickerApplication.instance.recentStickerRepository)
        fetchRecentStickersUseCase = FetchRecentStickersUseCase(StickerApplication.instance.recentStickerRepository)
        fetchStickerCountInCategoryUseCase = FetchStickerCountInCategoryUseCase(StickerApplication.instance.stickerRepository)
        fetchCategories()
        loadAndSaveEmoji(R.raw.emojis)
        fetchRecentSearches()
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
                            _liveData.value = (Result.CategoryCreated)
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
                        when (it) {
                            is FetchStickersForCategoryUseCase.Result.Success -> {
                                stickers = it.stickers.toMutableList()
                                _liveData.postValue(Result.StickersReceivedForCategory(stickers.toList()))
                            }

                            else -> {}
                        }
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
            fetchRecentSearches()
            _liveData.postValue(Result.RecentSearchCleared(recentSearches.toList()))
        }
    }

    fun downloadSticker(stickerDTO: StickerDTO) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val url = URL(stickerDTO.thumbnail)
                val connection = url.openConnection()
                connection.connect()

                // Create a temporary file in the cache directory
                val gifFile =
                    File(StickerApplication.instance.cacheDir, stickerDTO.title!! + ".gif")

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
        CoroutineScope(Dispatchers.Default).launch {
            sticker.isFavourite = true
            updateStickerUseCase.execute(sticker)
        }
    }

    fun addToDeletedStickers(sticker: Sticker) {
        CoroutineScope(Dispatchers.Default).launch {
            sticker.isDeleted = true
            updateStickerUseCase.execute(sticker)
        }
    }

    private fun fetchAllFavorites() {
        viewModelScope.launch {
            fetchAllFavoritesUseCase.execute()
                .collectLatest {
                    when (it) {
                        is FetchAllFavoritesUseCase.Result.Success -> {
                            favourites.clear()
                            favourites = it.data.toMutableList()
                            _liveData.postValue(Result.FavouritesStickerUpdated)
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
                    isHighlighted = false,
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
        fromPosition?.let {
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
            if (c.id == category.id) {
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
        }
    }
    fun removeStickerFromDeleted(sticker: Sticker) {
        CoroutineScope(Dispatchers.IO).launch {
            fetchStickerUseCase.execute(sticker.id!!)?.let {
                it.isDeleted = false
                updateStickerUseCase.execute(it)
            }

        }
    }

    fun getStickersForQuery(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val list = fetchStickersForQueryUseCase.execute(query)
            _liveData.postValue(Result.StickersWithQuery(query, list))
        }
    }

    fun saveSingleSticker(stickerDTO: StickerDTO) {
        val cachedFile = File(StickerApplication.instance.cacheDir, stickerDTO.title + ".gif")
        if (cachedFile.exists()) {
            return
        }
        downloadSticker(stickerDTO)
        CoroutineScope(Dispatchers.IO).launch {
            val entitySticker = stickerDTO.sticker()
            insertSingleStickersUseCase.execute(entitySticker)
        }
    }

    fun fetchAllDownloaded() {
        viewModelScope.launch {
          fetchAllDownloadedUseCase.execute().collect {
              withContext(Dispatchers.Main) {
                  when (it) {
                      is FetchAllDownloadedUseCase.Result.Success -> {
                          stickers = it.list.toMutableList()
                          _liveData.postValue(Result.FetchedDownloadedStickers(stickers.toList()))
                      }
                  }
              }
            }
        }
    }

    fun reArrangeStickers() {
        if (fromPosition != toPosition && fromPosition != null) {
            val sticker = stickers[fromPosition!!]
            stickers.removeAt(fromPosition!!)
            stickers.add(toPosition, sticker)
            stickers.forEachIndexed { index, sticker ->
                sticker.position = index
            }
            fromPosition = null
            CoroutineScope(Dispatchers.Default).launch {
                insertStickersUseCase.forceInsertAll(
                    stickers
                )
            }
        }
    }

    private fun insertCategories() {
        CoroutineScope(Dispatchers.IO).launch {
            insertCategoriesUseCase.execute(getCategories())
        }
    }

    fun loadAndSaveEmoji(resourceId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val jsonString = loadJSONFromResource(resourceId)
            jsonString?.let {
                try {
                    val gson = Gson()
                    val emojis = gson.fromJson(jsonString, Emojis::class.java)
                    val emojiList = arrayListOf<Emoji>()
                    for (emoji in emojis.emojis) {
                        emojiList.add(emoji.toEmoji())
                    }
                    addEmojiIfNotExistUseCase.execute(emojiList)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun loadJSONFromResource(resourceId: Int): String? {
        return try {
            val inputStream = StickerApplication.instance.resources.openRawResource(resourceId)
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }
            bufferedReader.close()
            stringBuilder.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun setStickerDto(dto: StickerDTO?) {
        dto?.let { stickerDTO = it }
    }

    fun getStickerDto() = stickerDTO
    fun shareSticker(sticker: Sticker) {
        _liveData.value = Result.ShareSticker(sticker.toFile()!!)
    }

    fun addToFavorites(id: Int?) {
        CoroutineScope(Dispatchers.Default).launch {
            var sticker = fetchStickerUseCase.execute(id!!)
            sticker?.isFavourite = true
            updateStickerUseCase.execute(sticker!!)
            stickerDTO = sticker.toStickerDTO()
            _liveData.postValue(Result.StickerDTOUpdated)
        }
    }

    fun addRecentSticker(stickerId: Int) {
        val recentSticker = RecentStickers(stickerId = stickerId)
        CoroutineScope(Dispatchers.Default).launch {
            insertRecentStickersUseCase.execute(recentSticker)
        }
    }

    fun fetchRecentStickers() {
        CoroutineScope(Dispatchers.IO).launch {
            val list = fetchRecentStickersUseCase.execute()
            list.let {
                _liveData.postValue(Result.FetchedRecentStickers(it.map { it.sticker }))
            }
        }
    }

    fun removeStickerFromFav(id: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val sticker = fetchStickerUseCase.execute(id)
            sticker?.isFavourite = false
            updateStickerUseCase.execute(sticker!!)
            stickerDTO = sticker.toStickerDTO()
            _liveData.postValue(Result.StickerDTOUpdated)
        }
    }

    fun updateViewMode(newViewMode: ViewMode) {
        currentViewMode = newViewMode
    }


    fun deleteSticker(sticker: Sticker) {
        CoroutineScope(Dispatchers.Default).launch {
            fetchStickerUseCase.execute(sticker.id!!)?.let {
                it.isDeleted = true
                updateStickerUseCase.execute(it)
            }
        }
    }

    fun getViewMode() = currentViewMode

    fun moveStickerToCategory(sourceStickerPosition: Int, targetCategoryPosition: Int) {
        // Fetch Sticker
        // Fetch Existing Category
        // Fetch Selected category
        // Remove sticker from existing category
        // Add sticker to new category

        CoroutineScope(Dispatchers.Default).launch {
            val selectedSticker = fetchStickerUseCase.execute(stickers[sourceStickerPosition].id!!)
            val stickerCount = fetchStickerCountInCategoryUseCase.execute(categories[targetCategoryPosition].id!!)
            selectedSticker?.categoryId = categories[targetCategoryPosition].id
            selectedSticker?.position = stickerCount+1
            updateStickerUseCase.execute(selectedSticker!!)
        }
    }

    enum class ViewMode {
        Recent,
        Downloaded,
        Favourites,
        Category,
        RecentSearch
    }
}