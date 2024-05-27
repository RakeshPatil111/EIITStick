package com.android.stickerpocket.presentation.sticker

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.android.stickerpocket.domain.model.Category
import com.android.stickerpocket.domain.model.RecentSearch
import com.android.stickerpocket.dtos.getCategories
import com.android.stickerpocket.presentation.Sticker
import com.android.stickerpocket.utils.Event
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
        data class ShowRecentSearches(val recentSearches: List<RecentSearch>): Actions()
        data class ShowGiphyViewForRecentSearch(val query: String) : Actions()
        data class LoadEmojisForCategory(val query: String) : Actions()
        data class ShowCategoryOptionDialog(val category: Category) : Actions()
        data class ShowStickerDialog(val sticker: Sticker) : Actions()
        data class ShareSticker(val gifFile: File) : Actions()
        data class NavigateToStickerInfo(val sticker: Sticker) : Actions()
    }
    private val _liveData = MutableLiveData<Event<Actions>>()
    val liveData = _liveData
    private lateinit var viewModel: StickerViewModel
    fun initObserver(viewLifecycleOwner: LifecycleOwner, viewModel: StickerViewModel) {
        this.viewModel = viewModel
        viewModel.liveData.observe(viewLifecycleOwner, Observer {

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

    fun onQueryBlank() {
        _liveData.postValue(Event(Actions.ShowRecentSearches(viewModel.getRecentSearches())))
    }

    fun onCategoryItemClick(category: Category) {
        _liveData.value = Event(Actions.LoadEmojisForCategory(category.name))
    }

    fun onCategoryItemLongClick(category: Category) {
        _liveData.value = Event(Actions.ShowCategoryOptionDialog(category))
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
}