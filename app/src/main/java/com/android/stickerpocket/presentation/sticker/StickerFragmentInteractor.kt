package com.android.stickerpocket.presentation.sticker

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.android.stickerpocket.domain.model.RecentSearch
import com.android.stickerpocket.presentation.Sticker
import com.android.stickerpocket.utils.StickerViewModelFactory

class StickerFragmentInteractor {

    sealed class Actions {
        object InitGiphyView: Actions()
        object InitCategoryView: Actions()
        object HideGiphyGridViewAndShowRecentSearches: Actions()
        object ShowGiphyGridView: Actions()
        data class ShowRecentSearches(val recentSearches: List<RecentSearch>): Actions()
        data class ShowGiphyViewForRecentSearch(val query: String) : Actions()
        data class LoadEmojisForCategory(val query: String) : Actions()
        object ShowCategoryOptionDialog : Actions()
    }
    private val _liveData = MutableLiveData<Actions>()
    val liveData = _liveData
    private lateinit var viewModel: StickerViewModel
    fun initObserver(viewLifecycleOwner: LifecycleOwner, viewModel: StickerViewModel) {
        this.viewModel = viewModel
        viewModel.liveData.observe(viewLifecycleOwner, Observer {

        })
    }

    fun onViewCreated() {
        _liveData.value = Actions.InitCategoryView
        _liveData.postValue(Actions.InitGiphyView)
    }

    fun onSearchClick() {
        _liveData.value = Actions.HideGiphyGridViewAndShowRecentSearches
        _liveData.postValue(Actions.ShowRecentSearches(viewModel.getRecentSearches()))
    }

    fun onRecentSearchItemClick(position: Int) {
        _liveData.value = Actions.ShowGiphyViewForRecentSearch(viewModel.getRecentSearches()[position].query)
        viewModel.updateRecentSearch(position)
    }

    fun onQuerySearch(query: String) {
        viewModel.saveRecentSearch(query)
        _liveData.value = Actions.ShowGiphyViewForRecentSearch(query)
    }

    fun onRecentSearchRemove(position: Int) {
        viewModel.removeRecentSearch(position)
        _liveData.value = Actions.ShowRecentSearches(viewModel.getRecentSearches())
    }

    fun onQueryBlank() {
        _liveData.postValue(Actions.ShowRecentSearches(viewModel.getRecentSearches()))
    }

    fun onCategoryItemClick(sticker: Sticker) {
        _liveData.value = Actions.LoadEmojisForCategory(sticker.title!!)
    }

    fun onCategoryItemLongClick() {
        _liveData.value = Actions.ShowCategoryOptionDialog
    }
}