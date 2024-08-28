package com.android.stickerpocket.presentation.settings.delete

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.android.stickerpocket.domain.model.Sticker
import com.android.stickerpocket.presentation.sticker.StickerViewModel
import com.android.stickerpocket.utils.Event

class DeleteFragmentInterActor {

    private val _liveData = MutableLiveData<Event<Actions>>()
    val liveData = _liveData
    private lateinit var viewModel: StickerViewModel

    sealed class Actions {
        data class ShowStickers(val stickers: List<Sticker>): Actions()
        object ShowProgress: Actions()
        object HideProgress: Actions()
        object HideRecyclerView: Actions()
    }

    fun initObserver(viewLifecycleOwner: LifecycleOwner, viewModel: StickerViewModel) {
        this.viewModel = viewModel
        viewModel.liveData.observe(viewLifecycleOwner, Observer {
            when (it) {
                is StickerViewModel.Result.DeletedStickers -> {
                    if (it.stickers.isNotEmpty()) {
                        _liveData.postValue(Event(Actions.ShowStickers(it.stickers)))
                    } else {
                        _liveData.postValue(Event(Actions.HideRecyclerView))
                    }
                }

                is StickerViewModel.Result.StickerDeleted -> {
                    _liveData.postValue(Event(Actions.HideProgress))
                    viewModel.getDeletedStickers()
                }

                else -> {}
            }
        })
    }

    fun onViewCreated() {
        viewModel.getDeletedStickers()
    }

    fun onDeleteAll() {
        _liveData.value = Event(Actions.ShowProgress)
        viewModel.deleteAllStickers()
    }

    fun onDelete(sticker: Sticker) {
        _liveData.value = Event(Actions.ShowProgress)
        viewModel.hardDeleteSticker(listOf(sticker))
    }

    fun onRestoreStickers(selectedStickers: List<Sticker>) {
        viewModel.restoreStickers(selectedStickers)
    }
}