package com.android.stickerpocket.presentation.sticker

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.stickerpocket.R
import com.android.stickerpocket.StickerApplication
import com.android.stickerpocket.domain.usecase.FetchTenorGifsUseCase
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
import com.android.stickerpocket.domain.usecase.FetchDeletedStickersUseCase
import com.android.stickerpocket.domain.usecase.FetchEmojiByEmojiIcon
import com.android.stickerpocket.domain.usecase.FetchRecentStickersUseCase
import com.android.stickerpocket.domain.usecase.FetchStickerCountInCategoryUseCase
import com.android.stickerpocket.domain.usecase.FetchStickerUseCase
import com.android.stickerpocket.domain.usecase.FetchStickersForCategoryUseCase
import com.android.stickerpocket.domain.usecase.FetchStickersForQueryUseCase
import com.android.stickerpocket.domain.usecase.FetchStickersWithNoTagsUseCase
import com.android.stickerpocket.domain.usecase.FetchTrendingGifUseCase
import com.android.stickerpocket.domain.usecase.GetRecentSearchUseCase
import com.android.stickerpocket.domain.usecase.InsertOrReplaceCategoriesUseCase
import com.android.stickerpocket.domain.usecase.InsertRecentStickerUseCase
import com.android.stickerpocket.domain.usecase.InsertSingleStickersUseCase
import com.android.stickerpocket.domain.usecase.InsertStickersUseCase
import com.android.stickerpocket.domain.usecase.UpdateStickerUseCase
import com.android.stickerpocket.dtos.CommonAdapterDTO
import com.android.stickerpocket.dtos.getCategories
import com.android.stickerpocket.network.response.Emojis
import com.android.stickerpocket.network.response.GifResponse
import com.android.stickerpocket.network.response.tenor.TenorGifs
import com.android.stickerpocket.presentation.StickerDTO
import com.android.stickerpocket.utils.CommunicationBridge
import com.android.stickerpocket.utils.StickerExt.sticker
import com.android.stickerpocket.utils.StickerExt.toFile
import com.android.stickerpocket.utils.StickerExt.toLoadableImage
import com.android.stickerpocket.utils.StickerExt.toStickerDTO
import com.android.stickerpocket.utils.toCommonDTO
import com.android.stickerpocket.utils.toEmoji
import com.android.stickerpocket.utils.toStickerDTO
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.net.URL
import java.util.Date
import java.util.UUID


class StickerViewModel : ViewModel() {

    sealed class Result {
        data class RecentSearches(val recentSearches: List<RecentSearch>) : Result()
        object CategoryCreated : Result()
        object CreateCatFailure : Result()
        data class StickersReceivedForCategory(val stickers: List<CommonAdapterDTO>) : Result()
        data class FetchedDownloadedStickers(val stickers: List<CommonAdapterDTO>) : Result()
        object FavouritesStickerUpdated : Result()
        data class StickersWithQuery(val query: String, val stickers: List<CommonAdapterDTO>) : Result()
        data class ShowProgress(val showProgress: Boolean) : Result()
        data class StickerDownloaded(val gifFile: File) : Result()
        data class ShareSticker(val gifFile: File) : Result()
        data class RecentSearchCleared(val searches: List<RecentSearch>) : Result()
        class FetchedRecentStickers(val list: List<CommonAdapterDTO>) : Result()
        object StickerDTOUpdated : Result()
        data class StickerUpdated(val updatedSticker: Sticker) : Result()
        data class TrendingGiphyStickers(val giphyGifs: List<StickerDTO>, val tenorGifs: List<StickerDTO>) : Result()
        data class StickersWithQueryForBoth(val query: String, val giphyGifs: List<StickerDTO>, val tenorGifs: List<StickerDTO>) : Result()
        data class DeletedStickers(val stickers: List<Sticker>) : Result()
        object StickerDeleted: Result()
        data class Failure(val message: String): Result()
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
    private val fetchStickersWithNoTagsUseCase: FetchStickersWithNoTagsUseCase
    private var stickersWithNoTags = listOf<Sticker>()
    private var trendingGifResponse: GifResponse? = null
    private var trendingTenorGifsResponse: TenorGifs? = null
    private var trendingGIPHYGifs = mutableListOf<StickerDTO>()
    private var trendingTenorGifs = mutableListOf<StickerDTO>()
    private val fetchTrendingGifUseCase = FetchTrendingGifUseCase()
    private val fetchTenorGifsUseCase = FetchTenorGifsUseCase()
    private var queryGifResponse: GifResponse? = null
    private var queryTenorGifsResponse: TenorGifs? = null
    private var queryGIPHYGifs = mutableListOf<StickerDTO>()
    private var queryTenorGifs = mutableListOf<StickerDTO>()
    private val randomId = UUID.randomUUID().toString()
    private lateinit var fetchAPIFromServeJob: Job
    private var query: String? = null
    private var fetchDeletedStickersUseCase: FetchDeletedStickersUseCase

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
        fetchStickersWithNoTagsUseCase = FetchStickersWithNoTagsUseCase(StickerApplication.instance.stickerRepository)
        insertRecentStickersUseCase = InsertRecentStickerUseCase(StickerApplication.instance.recentStickerRepository)
        fetchRecentStickersUseCase = FetchRecentStickersUseCase(StickerApplication.instance.recentStickerRepository)
        fetchStickerCountInCategoryUseCase = FetchStickerCountInCategoryUseCase(StickerApplication.instance.stickerRepository)
        loadAndSaveEmoji(R.raw.emojis)
        fetchDeletedStickersUseCase = FetchDeletedStickersUseCase(StickerApplication.instance.stickerRepository)
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
                                _liveData.postValue(Result.StickersReceivedForCategory(stickersToCommonDTO(stickers)))
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

    fun getEmojiCategories() = categoryToCommonDTO(categories)

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
            _liveData.postValue(Result.StickersWithQuery(query, stickersToCommonDTO(list)))
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
                          _liveData.postValue(Result.FetchedDownloadedStickers(stickersToCommonDTO(stickers)))
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
                _liveData.postValue(Result.FetchedRecentStickers(it.map { it.sticker.toCommonDTO() }))
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

    fun addTagToSticker(stickerId: Int, tag: String) {
        CoroutineScope(Dispatchers.Default).launch {
            fetchStickerUseCase.execute(stickerId)?.let {
                if (it.tags == null) {
                    it.tags = tag
                } else {
                    it.tags = it.tags+","+tag
                }
                updateStickerUseCase.execute(it)
                _liveData.postValue(Result.StickerUpdated(it))
            }
        }
    }

    fun getStickersWithNullTags(): List<CommonAdapterDTO> {
        fetchStickersNoTags()
        return stickersToCommonDTO(stickersWithNoTags)
    }

    private fun fetchStickersNoTags() {
        CoroutineScope(Dispatchers.Default).launch {
            stickersWithNoTags = fetchStickersWithNoTagsUseCase.execute()
        }
    }

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

        CoroutineScope(Dispatchers.Main).launch {
            fetchStickersForCategoryUseCase.execute(categories.filter { it.isHighlighted }.first().id!!)
        }
    }

    fun moveMultipleStickersToCategory( targetCategoryPosition: Int) {
        // Fetch Sticker
        // Fetch Existing Category
        // Fetch Selected category
        // Remove sticker from existing category
        // Add sticker to new category

        CoroutineScope(Dispatchers.Default).launch {
            //val selectedSticker = fetchStickerUseCase.execute(stickers[sourceStickerPosition].id!!)

            val selectedStickers = CommunicationBridge.selectedStickes.value
            val targetedId =categories[targetCategoryPosition].id
            val stickerCount = fetchStickerCountInCategoryUseCase.execute(categories[targetCategoryPosition].id!!)
            for(index in CommunicationBridge.selectedStickes.value!!.indices) {
                var selectedSticker=selectedStickers?.get(index)
                selectedSticker?.categoryId = targetedId
                selectedSticker?.position = stickerCount + (index+1)
                if (selectedSticker != null) {
                    updateStickerUseCase.execute( selectedSticker)
                }
            }

            categories.filter { it.isHighlighted == true }.first().id?.let {
                fetchStickersForCategoryUseCase.execute(
                    it
                )
            }
            CommunicationBridge.selectedStickes.value?.clear()
            CommunicationBridge.selectedCatPosition.postValue(-1)
        }
    }

    fun getTrendingGifs() {
        if (trendingGifResponse != null && trendingTenorGifs != null) {
            return
        }
        // GIPHY API called twice to fetch 50 GIFS, max limit in one Req is 25
        fetchAPIFromServeJob = CoroutineScope(Dispatchers.IO).launch {
            launch {
                for (i in 0..1) {
                    trendingGifResponse = fetchTrendingGifUseCase.execute(randomId = randomId, page = i)
                    handleGiphyTrendingResponse(trendingGifResponse)
                }
            }
            launch {
                trendingTenorGifsResponse = fetchTenorGifsUseCase.execute()
                handleTenorTrendingResponse(trendingTenorGifsResponse)
            }
        }
    }

    fun getQueryGifs(searchQuery: String?) {
        searchQuery?.let {
            query = it
            queryGIPHYGifs.clear()
            queryTenorGifs.clear()
            fetchAPIFromServeJob.cancel()
            fetchAPIFromServeJob = CoroutineScope(Dispatchers.IO).launch {
                launch {
                    for (i in 0..1) {
                        queryGifResponse = fetchTrendingGifUseCase.execute(
                            randomId = randomId,
                            page = 1,
                            query = query!!
                        )
                        handleGiphyTrendingResponse(queryGifResponse)
                    }
                }
                launch {
                    queryTenorGifsResponse = fetchTenorGifsUseCase.execute(query = query!!)
                    handleTenorTrendingResponse(queryTenorGifsResponse)
                }
            }

            fetchAPIFromServeJob.invokeOnCompletion {
                _liveData.postValue(Result.StickersWithQueryForBoth(query!!, queryGIPHYGifs, queryTenorGifs))
            }
        }
    }

    fun loadMoreGiphyStickers() {
        CoroutineScope(Dispatchers.IO).launch {
            val page = trendingGifResponse?.pagination?.offset?.plus(1) ?: 0
            trendingGifResponse = fetchTrendingGifUseCase.execute(randomId = randomId, page = page)
            handleGiphyTrendingResponse(trendingGifResponse)
        }
    }

    fun resetResponse() {
        trendingGifResponse = null
    }

    private fun handleGiphyTrendingResponse(gifResponse: GifResponse?) {
        gifResponse?.let {
            Log.w("ViewModle", "${it.data.size}")
            it.data.forEachIndexed { index, data ->
                val item = data.toStickerDTO()
                Log.w("ViewModle", "${trendingGIPHYGifs.contains(item)}")
                if(query != null) queryGIPHYGifs.add(item) else trendingGIPHYGifs.add(item)
            }
        }
    }

    private fun handleTenorTrendingResponse(gifResponse: TenorGifs?) {
        gifResponse?.let {
            it.results.forEach {
                val item = it.toStickerDTO()
                if(query != null) queryTenorGifs.add(item) else trendingTenorGifs.add(item)
            }
        }
    }

    fun loadTrendingGifs() {
        if (trendingGifResponse == null) {
            fetchAPIFromServeJob.cancel()
            getTrendingGifs()
            fetchAPIFromServeJob.invokeOnCompletion { _liveData.postValue(Result.TrendingGiphyStickers(trendingGIPHYGifs, trendingTenorGifs)) }
        } else {
            _liveData.postValue(Result.TrendingGiphyStickers(trendingGIPHYGifs, trendingTenorGifs))
        }
    }

    fun getDeletedStickers() {
        CoroutineScope(Dispatchers.Default).launch {
            val list = fetchDeletedStickersUseCase.execute()
            _liveData.postValue(Result.DeletedStickers(list))
        }
    }

    fun loadData() {
        //observeStickers()
        fetchCategories()
        fetchRecentSearches()
        fetchAllFavorites()
        fetchStickersNoTags()
    }

    fun hardDeleteSticker(stickers: List<Sticker>) {
        try {
            val job = CoroutineScope(Dispatchers.IO).launch {
                stickers.forEach {
                    val file = File(StickerApplication.instance.cacheDir, it.name + ".gif")
                    if (file.exists()) {
                        file.delete()
                    }
                    val stickerRepository = StickerApplication.instance.stickerRepository
                    stickerRepository.deleteSticker(it)
                }
            }
            job.invokeOnCompletion {
                _liveData.postValue(Result.StickerDeleted)
            }
        } catch (e: Exception) {
            _liveData.value = Result.Failure(e.localizedMessage)
        }
    }

    fun deleteAllStickers() {
        CoroutineScope(Dispatchers.Default).launch {
            val stickers = fetchDeletedStickersUseCase.execute()
            hardDeleteSticker(stickers)
        }
    }

    fun restoreStickers(selectedStickers: List<Sticker>) {
        CoroutineScope(Dispatchers.IO).launch {
            selectedStickers.forEach {
                it.isDeleted = false
                updateStickerUseCase.execute(it)
            }
            _liveData.postValue(Result.StickerDeleted)
        }
    }

    private fun observeStickers() {
        CoroutineScope(Dispatchers.IO).launch {
            StickerApplication.instance.stickerRepository.fetchAllFlow()
                .collectLatest {
                    if (categories.isNotEmpty()) {
                        categories.filter { it.isHighlighted == true }.first().id?.let {
                            fetchStickersForCategoryUseCase.execute(
                                it
                            )
                        }
                    }
                }
        }
    }

    fun stickersToCommonDTO(stickers: List<Sticker>): List<CommonAdapterDTO> {
        val commonDTOs = mutableListOf<CommonAdapterDTO>()
        stickers.forEach {
            commonDTOs.add(it.toCommonDTO())
        }
        return commonDTOs
    }

    fun categoryToCommonDTO(stickers: List<Category>): List<CommonAdapterDTO> {
        val commonDTOs = mutableListOf<CommonAdapterDTO>()
        stickers.forEach {
            commonDTOs.add(it.toCommonDTO())
        }
        return commonDTOs
    }

    enum class ViewMode {
        Recent,
        Downloaded,
        Favourites,
        Category,
        RecentSearch
    }
}