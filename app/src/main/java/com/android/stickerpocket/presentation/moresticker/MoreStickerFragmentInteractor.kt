package com.android.stickerpocket.presentation.moresticker

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.android.stickerpocket.presentation.StickerDTO
import com.android.stickerpocket.presentation.sticker.StickerViewModel
import com.android.stickerpocket.utils.Event

class MoreStickerFragmentInteractor {

    sealed class Actions {
        data class ShowDownloadStickerDialog(val media: StickerDTO) : Actions()
        data class ShowProgress(val showProgress: Boolean) : Actions()
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

    fun onLoadMoreTrendingStickers() {
        viewModel.loadMoreGiphyStickers()
    }

    fun onDestroy() {
        //viewModel.resetResponse()
    }
}