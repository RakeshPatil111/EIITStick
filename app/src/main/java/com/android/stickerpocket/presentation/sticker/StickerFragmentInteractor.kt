package com.android.stickerpocket.presentation.sticker

import androidx.emoji2.emojipicker.EmojiViewItem
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.android.stickerpocket.domain.model.Category
import com.android.stickerpocket.domain.model.RecentSearch
import com.android.stickerpocket.dtos.getCategories
import com.android.stickerpocket.presentation.StickerDTO
import com.android.stickerpocket.utils.Event
import com.android.stickerpocket.utils.StickerExt.stickerDTO
import com.android.stickerpocket.utils.StickerExt.toFile
import com.android.stickerpocket.utils.StickerExt.toStickerDTO
import com.giphy.sdk.core.models.Media
import java.io.File

class StickerFragmentInteractor {

    sealed class Actions {
        object InitGiphyView : Actions()
        data class InitCategoryView(val categories: List<Category>) : Actions()
        object HideGiphyGridViewAndShowRecentSearches : Actions()
        object ShowGiphyGridView : Actions()
        data class clearAllRecentSearchAndHideView(val list: List<RecentSearch>) : Actions()
        data class ShowRecentSearches(val recentSearches: List<RecentSearch>) : Actions()
        data class ShowGiphyViewForRecentSearch(val query: String) : Actions()
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

        data class ReloadCategories(val categories: List<Category>) : Actions()
        data class ShowMessage(val message: String) : Actions()
        data class ShowStickers(val stickers: List<com.android.stickerpocket.domain.model.Sticker>) :
            Actions()

        data class ShowDownloadedStickers(val stickers: List<com.android.stickerpocket.domain.model.Sticker>) :
            Actions()

        data class ShowStickerForRecentSearch(
            val query: String,
            val stickers: List<com.android.stickerpocket.domain.model.Sticker>
        ) : Actions()

        data class ShowRecentStickers(val stickers: List<com.android.stickerpocket.domain.model.Sticker>) :
            Actions()
    }

    private val _liveData = MutableLiveData<Event<Actions>>()
    val liveData = _liveData
    private lateinit var viewModel: StickerViewModel
    fun initObserver(viewLifecycleOwner: LifecycleOwner, viewModel: StickerViewModel) {
        this.viewModel = viewModel
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
                }

                is StickerViewModel.Result.FavouritesStickerUpdated -> {
                    if (viewModel.getViewMode() == StickerViewModel.ViewMode.Favourites) {
                        _liveData.value =
                            Event(Actions.ShowFavoritesSticker(viewModel.getFavourites()))
                    }
                }

                is StickerViewModel.Result.StickersWithQuery -> {
                    _liveData.postValue(
                        Event(
                            Actions.ShowStickerForRecentSearch(
                                it.query,
                                it.stickers
                            )
                        )
                    )
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
    }

    fun onViewCreated() {
        _liveData.value = (Event(
            Actions.InitCategoryView(
                viewModel.getEmojiCategories().ifEmpty { getCategories() })
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
                _liveData.postValue( Event(Actions.ShowFavoritesSticker(viewModel.getFavourites())))
            }
            StickerViewModel.ViewMode.Category -> {
                if (viewModel.getEmojiCategories().isNotEmpty())
                    viewModel.categorySelected(viewModel.getEmojiCategories()[0], 0)
            }
        }
    }

    fun onSearchClick() {
        _liveData.value = Event(Actions.HideGiphyGridViewAndShowRecentSearches)
        _liveData.postValue(Event(Actions.ShowRecentSearches(viewModel.getRecentSearches())))
    }

    fun onRecentSearchItemClick(position: Int) {
        viewModel.getStickersForQuery(
            viewModel.getRecentSearches()[position].query
        )
        viewModel.updateRecentSearch(position)
    }

    fun onQuerySearch(query: String) {
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
        sticker.isOrganizeMode=false
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
    fun onStickerLongClick(sticker: com.android.stickerpocket.domain.model.Sticker, position: Int) {
        _liveData.value = Event(Actions.ShowStickerDialog(sticker, position, sticker.isFavourite))
    }

    fun onFavStickerClick(sticker: com.android.stickerpocket.domain.model.Sticker, position: Int) {
        _liveData.value = Event(Actions.ShareSticker(sticker, position, isFavourite = true))
    }
    fun onFavStickerLongClick(sticker: com.android.stickerpocket.domain.model.Sticker, position: Int) {
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
}