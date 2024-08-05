package com.android.stickerpocket.presentation.moresticker

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.android.stickerpocket.presentation.sticker.StickerViewModel
import com.android.stickerpocket.utils.Event
import com.giphy.sdk.core.models.Media

class MoreStickerFragmentInteractor {

    sealed class Actions {
        data class ShowDownloadStickerDialog(val media: Media) : Actions()
        data class ShowProgress(val showProgress: Boolean) : Actions()
    }
    private val _liveData = MutableLiveData<Event<Actions>>()
    val liveData = _liveData
    private lateinit var viewModel: StickerViewModel

    fun initObserver(viewLifecycleOwner: LifecycleOwner, viewModel: StickerViewModel) {
        this.viewModel = viewModel
        viewModel.liveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is StickerViewModel.Result.ShowProgress -> {
                    _liveData.value = Event(Actions.ShowProgress(it.showProgress))
                }
                else -> {}
            }
        })
    }

    fun onStickerClick(media: Media) {
        _liveData.value = Event(Actions.ShowDownloadStickerDialog(media))
    }
}