package com.android.stickerpocket.presentation.sticker

import androidx.emoji2.emojipicker.EmojiViewItem
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.android.stickerpocket.domain.model.Category
import com.android.stickerpocket.domain.model.RecentSearch
import com.android.stickerpocket.dtos.CommonAdapterDTO
import com.android.stickerpocket.dtos.getCategories
import com.android.stickerpocket.presentation.StickerDTO
import com.android.stickerpocket.utils.Event
import com.android.stickerpocket.utils.StickerExt.stickerDTO
import com.android.stickerpocket.utils.StickerExt.toStickerDTO
import com.android.stickerpocket.utils.toCategory
import com.giphy.sdk.core.models.Media

class StickerFragmentInteractor {

    sealed class Actions {
        data class InitCategoryView(val categories: List<CommonAdapterDTO>) : Actions()
        object HideGiphyGridViewAndShowRecentSearches : Actions()
        data class clearAllRecentSearchAndHideView(val list: List<RecentSearch>) : Actions()
        data class ShowRecentSearches(val recentSearches: List<RecentSearch>) : Actions()
        data class LoadEmojisForCategory(val query: String) : Actions()
        data class ShowCategoryOptionDialog(
            val category: Category,
            val pos: Int,
            val previous: Int
        ) : Actions()

        data class ShowStickerDialog(
            val sticker: com.android.stickerpocket.domain.model.Sticker,
            val position: Int,
            val isFavourite: Boolean
        ) : Actions()

        data class ShareSticker(
            val sticker: com.android.stickerpocket.domain.model.Sticker,
            val position: Int,
            val isFavourite: Boolean
        ) : Actions()

        data class NavigateToStickerInfo(val stickerDTO: StickerDTO) : Actions()
        data class ShowFavoritesSticker(val favoriteStickers: List<com.android.stickerpocket.domain.model.Sticker>) :
            Actions()

        data class ReloadCategories(val categories: List<CommonAdapterDTO>) : Actions()
        data class ShowMessage(val message: String) : Actions()
        data class ShowStickers(val stickers: List<CommonAdapterDTO>) :
            Actions()

        data class ShowDownloadedStickers(val stickers: List<CommonAdapterDTO>) :
            Actions()

        data class ShowStickerForRecentSearch(
            val query: String,
            val recentSearchStickers: List<CommonAdapterDTO>,
            val stickersWithNoTags: List<CommonAdapterDTO>
        ) : Actions()

        data class ShowRecentStickers(val stickers: List<CommonAdapterDTO>) :
            Actions()
    }

    private val _liveData = MutableLiveData<Event<Actions>>()
    val liveData = _liveData
    private lateinit var viewModel: StickerViewModel
    fun initObserver(viewLifecycleOwner: LifecycleOwner, viewModel: StickerViewModel) {
        this.viewModel = viewModel
        viewModel.updateViewMode(StickerViewModel.ViewMode.Category)
        viewModel.liveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is StickerViewModel.Result.CategoryCreated -> {
                    _liveData.value =
                        Event(Actions.ReloadCategories(viewModel.getEmojiCategories()))
                }

                is StickerViewModel.Result.CreateCatFailure -> {
                    _liveData.value =
                        Event(Actions.ShowMessage("Emoji not found, Can not create category"))
                }

                is StickerViewModel.Result.StickersReceivedForCategory -> {
                    if (viewModel.getViewMode() == StickerViewModel.ViewMode.Category) {
                        _liveData.postValue(Event(Actions.ShowStickers(it.stickers)))
                    }
//                    if (viewModel.getViewMode() == StickerViewModel.ViewMode.Downloaded) {
//                        _liveData.postValue(Event(Actions.ShowDownloadedStickers(it.stickers)))
//                    }
                }

                is StickerViewModel.Result.FavouritesStickerUpdated -> {
                    if (viewModel.getViewMode() == StickerViewModel.ViewMode.Favourites) {
                        _liveData.value =
                            Event(Actions.ShowFavoritesSticker(viewModel.getFavourites()))
                    }
                }

                is StickerViewModel.Result.StickersWithQuery -> {
                    if (viewModel.getViewMode() == StickerViewModel.ViewMode.RecentSearch) {
                        _liveData.postValue(
                            Event(
                                Actions.ShowStickerForRecentSearch(
                                    it.query,
                                    it.stickers,
                                    viewModel.getStickersWithNullTags()
                                )
                            )
                        )
                    }
                }

                is StickerViewModel.Result.FetchedDownloadedStickers -> {
                    if (viewModel.getViewMode() == StickerViewModel.ViewMode.Downloaded) {
                        _liveData.postValue(Event(Actions.ShowDownloadedStickers(it.stickers)))
                    }
                }

                is StickerViewModel.Result.RecentSearchCleared -> {
                    _liveData.value = Event(Actions.clearAllRecentSearchAndHideView(it.searches))
                }

                is StickerViewModel.Result.FetchedRecentStickers -> {
                    if (viewModel.getViewMode() == StickerViewModel.ViewMode.Recent) {
                        _liveData.value = Event(Actions.ShowRecentStickers(it.list))
                    }
                }

                else -> {}
            }
        })

        // Load Categories and all other data
        viewModel.loadData()
    }

    fun onViewCreated() {
        _liveData.value = (Event(
            Actions.InitCategoryView(
                viewModel.getEmojiCategories().ifEmpty { viewModel.categoryToCommonDTO(getCategories()) })
        ))
        loadStickers()
    }

    private fun loadStickers() {
        when (viewModel.getViewMode()) {
            StickerViewModel.ViewMode.Recent -> {
                viewModel.fetchRecentStickers()
            }

            StickerViewModel.ViewMode.Downloaded -> {
                viewModel.fetchAllDownloaded()
            }

            StickerViewModel.ViewMode.Favourites -> {
                _liveData.postValue(Event(Actions.ShowFavoritesSticker(viewModel.getFavourites())))
            }

            StickerViewModel.ViewMode.Category -> {
                if (viewModel.getEmojiCategories().isNotEmpty())
                    viewModel.categorySelected(
                        viewModel.getEmojiCategories().filter { it.isHighlighted }.first().toCategory(), 0
                    )
            }

            else -> {}
        }
    }

    fun onSearchClick() {
        _liveData.value = Event(Actions.HideGiphyGridViewAndShowRecentSearches)
        _liveData.postValue(Event(Actions.ShowRecentSearches(viewModel.getRecentSearches())))
    }

    fun onRecentSearchItemClick(position: Int) {
        viewModel.updateViewMode(StickerViewModel.ViewMode.RecentSearch)
        viewModel.getStickersForQuery(
            viewModel.getRecentSearches()[position].query
        )
        viewModel.updateRecentSearch(position)
    }

    fun onQuerySearch(query: String) {
        viewModel.updateViewMode(StickerViewModel.ViewMode.RecentSearch)
        viewModel.saveRecentSearch(query)
        viewModel.getStickersForQuery(query)
    }

    fun onRecentSearchRemove(position: Int) {
        viewModel.removeRecentSearch(position)
        _liveData.value = Event(Actions.ShowRecentSearches(viewModel.getRecentSearches()))
    }

    fun onClearAllRecentSearch() {
        viewModel.clearAllRecentSearch()
    }

    fun onQueryBlank() {
        _liveData.postValue(Event(Actions.ShowRecentSearches(viewModel.getRecentSearches())))
    }

    fun onCategoryItemClick(category: Category, previous: Int) {
        viewModel.categorySelected(category, previous)
        viewModel.updateViewMode(StickerViewModel.ViewMode.Category)
    }

    fun onCategoryItemLongClick(category: Category, pos: Int, previous: Int) {
        _liveData.value = Event(Actions.ShowCategoryOptionDialog(category, pos, previous))
    }

    fun onMediaClick(media: Media) {
        val sticker = media.stickerDTO()
        viewModel.downloadSticker(sticker)
    }

    fun onStickerInfoClick(sticker: com.android.stickerpocket.domain.model.Sticker) {
        _liveData.value = Event(Actions.NavigateToStickerInfo(sticker.toStickerDTO()))
    }

    fun onStickerShare(sticker: com.android.stickerpocket.domain.model.Sticker) {
        viewModel.shareSticker(sticker)
    }

    fun onAddStickerToFavoritesClick(
        sticker: com.android.stickerpocket.domain.model.Sticker,
        didOpenForFav: Boolean
    ) {
        if (didOpenForFav) viewModel.removeStickerFromFav(sticker) else viewModel.addToFavorites(
            sticker
        )
    }

    fun onAddStickerToDeletedClick(
        sticker: com.android.stickerpocket.domain.model.Sticker,
        didOpenForDelete: Boolean
    ) {
        if (!didOpenForDelete) viewModel.removeStickerFromDeleted(sticker) else viewModel.addToDeletedStickers(
            sticker
        )
    }

    fun onFavClick() {
        viewModel.updateViewMode(StickerViewModel.ViewMode.Favourites)
        _liveData.value = Event(Actions.ShowFavoritesSticker(viewModel.getFavourites()))
    }

    fun onAddNewCategory(emojiItem: EmojiViewItem, category: Category, pos: Int, previous: Int) {
        viewModel.createCategory(emojiItem.emoji, pos)
    }

    fun onItemMove(fromPosition: Int, toPosition: Int) {
        viewModel.itemMoved(fromPosition, toPosition)
    }

    fun onDragComplete() {
        viewModel.reArrangeCategory()
    }

    fun onDeleteCategory(category: Category, pos: Int) {
        viewModel.deleteCategory(category, pos)
    }

    fun onStickerClick(sticker: com.android.stickerpocket.domain.model.Sticker, position: Int) {
        _liveData.value = Event(Actions.ShareSticker(sticker, position, sticker.isFavourite))
    }

    fun onStickerDoubleClick(
        sticker: com.android.stickerpocket.domain.model.Sticker,
        position: Int
    ) {
        _liveData.value = Event(Actions.ShowStickerDialog(sticker, position, sticker.isFavourite))
    }

    fun onStickerLongClick(sticker: com.android.stickerpocket.domain.model.Sticker, position: Int) {
        _liveData.value = Event(Actions.ShowStickerDialog(sticker, position, sticker.isFavourite))
    }

    fun onFavStickerClick(sticker: com.android.stickerpocket.domain.model.Sticker, position: Int) {
        _liveData.value = Event(Actions.ShareSticker(sticker, position, isFavourite = true))
    }

    fun onFavStickerDoubleClick(
        sticker: com.android.stickerpocket.domain.model.Sticker,
        position: Int
    ) {
        _liveData.value = Event(Actions.ShowStickerDialog(sticker, position, isFavourite = true))
    }

    fun onFavStickerLongClick(
        sticker: com.android.stickerpocket.domain.model.Sticker,
        position: Int
    ) {
        _liveData.value = Event(Actions.ShowStickerDialog(sticker, position, isFavourite = true))
    }

    fun onStickerMoved(from: Int, to: Int) {
        viewModel.itemMoved(from, to)
    }

    fun onStickerDragComplete() {
        viewModel.reArrangeStickers()
    }

    fun onDownloadClick() {
        viewModel.updateViewMode(StickerViewModel.ViewMode.Downloaded)
        viewModel.fetchAllDownloaded()
    }

    fun onRecentStickerClick() {
        viewModel.updateViewMode(StickerViewModel.ViewMode.Recent)
        viewModel.fetchRecentStickers()
    }

    fun onEditTextClear() {
        viewModel.updateViewMode(StickerViewModel.ViewMode.Category)
        loadStickers()
    }

    fun onStickerDroppedOnCategory(sourceStickerPosition: Int, targetCategoryPosition: Int) {
        viewModel.moveStickerToCategory(sourceStickerPosition, targetCategoryPosition)
    }

    fun onStickerMovedToCategory(targetCategoryPosition: Int) {
        viewModel.moveMultipleStickersToCategory(targetCategoryPosition)
    }
}