package com.android.stickerpocket.presentation.moresticker

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.android.stickerpocket.domain.model.RecentSearch
import com.android.stickerpocket.presentation.StickerDTO
import com.android.stickerpocket.presentation.sticker.StickerFragmentInteractor.Actions
import com.android.stickerpocket.presentation.sticker.StickerViewModel
import com.android.stickerpocket.utils.Event

class MoreStickerFragmentInteractor {

    sealed class Actions {
        data class ShowDownloadStickerDialog(val media: StickerDTO) : Actions()
        data class ShowProgress(val showProgress: Boolean) : Actions()
        object HideGiphyTenorGridViewAndShowRecentSearches : Actions()
        data class clearAllRecentSearchAndHideView(val list: List<RecentSearch>) : Actions()
        data class ShowRecentSearches(val recentSearches: List<RecentSearch>) : Actions()
        data class ShowTrendingGiphyStickers(val giphyGifs: List<StickerDTO>, val tenorGifs: List<StickerDTO>) : Actions()
    }
    private val _liveData = MutableLiveData<Event<Actions>>()
    val liveData = _liveData
    private lateinit var viewModel: StickerViewModel

    fun initObserver(viewLifecycleOwner: LifecycleOwner, viewModel: StickerViewModel) {
        this.viewModel = viewModel
        viewModel.getTrendingGifs()
        viewModel.liveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is StickerViewModel.Result.ShowProgress -> {
                    _liveData.value = Event(Actions.ShowProgress(it.showProgress))
                }
                is StickerViewModel.Result.TrendingGiphyStickers -> {
                    _liveData.value = Event(Actions.ShowTrendingGiphyStickers(it.giphyGifs, it.tenorGifs))
                }
                is StickerViewModel.Result.RecentSearchCleared -> {
                    _liveData.value = Event(Actions.clearAllRecentSearchAndHideView(it.searches))
                }
                else -> {}
            }
        })
    }

    fun onStickerClick(media: StickerDTO) {
        _liveData.value = Event(Actions.ShowDownloadStickerDialog(media))
    }

    fun onViewCreated() {
        viewModel.loadTrendingGifs()
    }

    fun onQuerySearch(query: String) {
        viewModel.updateViewMode(StickerViewModel.ViewMode.RecentSearch)
        viewModel.saveRecentSearch(query)
        viewModel.getQueryGifs(query)
    }

    fun onEditTextClear() {
        viewModel.updateViewMode(StickerViewModel.ViewMode.Category)
        //loadStickers()
    }

    fun onRecentSearchItemClick(position: Int) {
        viewModel.updateViewMode(StickerViewModel.ViewMode.RecentSearch)
        viewModel.getQueryGifs(
            viewModel.getRecentSearches()[position].query
        )
        viewModel.updateRecentSearch(position)
    }

    fun onRecentSearchRemove(position: Int) {
        viewModel.removeRecentSearch(position)
        _liveData.value = Event(Actions.ShowRecentSearches(viewModel.getRecentSearches()))
    }

    fun onClearAllRecentSearch() {
        viewModel.clearAllRecentSearch()
    }

    fun onSearchClick() {
        _liveData.value = Event(Actions.HideGiphyTenorGridViewAndShowRecentSearches)
        _liveData.postValue(Event(Actions.ShowRecentSearches(viewModel.getRecentSearches())))
    }

    fun onLoadMoreTrendingStickers() {
        viewModel.loadMoreGiphyStickers()
    }

    fun onDestroy() {
        //viewModel.resetResponse()
    }
}