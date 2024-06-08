package com.android.stickerpocket.presentation.sticker

import androidx.emoji2.emojipicker.EmojiViewItem
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.android.stickerpocket.domain.model.Favourites
import com.android.stickerpocket.domain.model.Category
import com.android.stickerpocket.domain.model.RecentSearch
import com.android.stickerpocket.dtos.getCategories
import com.android.stickerpocket.presentation.Sticker
import com.android.stickerpocket.utils.Event
import com.android.stickerpocket.utils.StickerExt.toFavorite
import com.android.stickerpocket.utils.StickerExt.toFile
import com.android.stickerpocket.utils.StickerExt.toSticker
import com.giphy.sdk.core.models.Media
import java.io.File

class StickerFragmentInteractor {

    sealed class Actions {
        object InitGiphyView: Actions()
        data class InitCategoryView(val categories: List<Category>): Actions()
        object HideGiphyGridViewAndShowRecentSearches: Actions()
        object ShowGiphyGridView: Actions()
        object clearAllRecentSearchAndHideView : Actions()
        data class ShowRecentSearches(val recentSearches: List<RecentSearch>): Actions()
        data class ShowGiphyViewForRecentSearch(val query: String) : Actions()
        data class LoadEmojisForCategory(val query: String) : Actions()
        data class ShowCategoryOptionDialog(val category: Category, val pos: Int, val previous: Int) : Actions()
        data class ShowStickerDialog(val sticker: Sticker) : Actions()
        data class ShareSticker(val gifFile: File) : Actions()
        data class NavigateToStickerInfo(val sticker: Sticker) : Actions()
        data class ShowFavoritesSticker(val favoriteStickers: List<Favourites>): Actions()
        data class ReloadCategories(val categories: List<Category>) : Actions()
        data class ShowMessage(val message: String): Actions()
    }
    private val _liveData = MutableLiveData<Event<Actions>>()
    val liveData = _liveData
    private lateinit var viewModel: StickerViewModel
    fun initObserver(viewLifecycleOwner: LifecycleOwner, viewModel: StickerViewModel) {
        this.viewModel = viewModel
        viewModel.liveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is StickerViewModel.Result.CategoryCreated -> {
                    _liveData.value = Event(Actions.ReloadCategories(viewModel.getEmojiCategories()))
                }
                is StickerViewModel.Result.CreateCatFailure -> {
                    _liveData.value = Event(Actions.ShowMessage("Emoji not found, Can not create category"))
                }
                else -> {}
            }
        })
    }

    fun onViewCreated() {
        _liveData.value = (Event(Actions.InitCategoryView(viewModel.getEmojiCategories().ifEmpty { getCategories() })))
    }

    fun onSearchClick() {
        _liveData.value = Event(Actions.HideGiphyGridViewAndShowRecentSearches)
        _liveData.postValue(Event(Actions.ShowRecentSearches(viewModel.getRecentSearches())))
    }

    fun onRecentSearchItemClick(position: Int) {
        _liveData.value = Event(Actions.ShowGiphyViewForRecentSearch(viewModel.getRecentSearches()[position].query))
        viewModel.updateRecentSearch(position)
    }

    fun onQuerySearch(query: String) {
        viewModel.saveRecentSearch(query)
        _liveData.value = Event(Actions.ShowGiphyViewForRecentSearch(query))
    }

    fun onRecentSearchRemove(position: Int) {
        viewModel.removeRecentSearch(position)
        _liveData.value = Event(Actions.ShowRecentSearches(viewModel.getRecentSearches()))
    }

    fun onClearAllRecentSearch() {
        viewModel.clearAllRecentSearch()
        _liveData.value = Event(Actions.clearAllRecentSearchAndHideView)
    }

    fun onQueryBlank() {
        _liveData.postValue(Event(Actions.ShowRecentSearches(viewModel.getRecentSearches())))
    }

    fun onCategoryItemClick(category: Category, previous: Int) {
        viewModel.categorySelected(category, previous)
        _liveData.value = Event(Actions.LoadEmojisForCategory(category.name))
    }

    fun onCategoryItemLongClick(category: Category, pos: Int, previous: Int) {
        _liveData.value = Event(Actions.ShowCategoryOptionDialog(category, pos, previous))
    }

    fun onMediaClick(media: Media) {
        val sticker = media.toSticker()
        _liveData.value = Event(Actions.ShowStickerDialog(sticker))
        viewModel.downloadSticker(sticker)
    }

    fun onStickerInfoClick(sticker: Sticker) {
        _liveData.value = Event(Actions.NavigateToStickerInfo(sticker))
    }

    fun onStickerShare(sticker: Sticker) {
        _liveData.value = Event(Actions.ShareSticker(sticker.toFile()))
    }

    fun onAddStickerToFavoritesClick(sticker: Sticker) {
        viewModel.downloadSticker(sticker)
        viewModel.addToFavorites(sticker.toFavorite())
    }
    fun onFavClick() {
        _liveData.value = Event(Actions.ShowFavoritesSticker(viewModel.getFavourites()))
    }
    fun onAddNewCategory(emojiItem: EmojiViewItem, category: Category, pos: Int, previous: Int) {
        viewModel.getEmojiCategories().get(previous).isHighlighted = false
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
}