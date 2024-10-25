package com.android.stickerpocket.presentation.sticker

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.android.stickerpocket.presentation.StickerDTO
import com.android.stickerpocket.utils.Event
import com.android.stickerpocket.utils.StickerExt.sticker

class StickerDetailFragmentInteractor {
    private lateinit var viewModel: StickerViewModel
    private val _livedata = MutableLiveData<Event<Actions>>()
    val liveData = _livedata
    sealed class Actions {
        data class ShowStickerDetails(val stickerDTO: StickerDTO) : Actions()
        object ShowFilledFavIcon : Actions()
        object ShowOutlinedFavIcon : Actions()
        object ShowAddTagBottomSheet : Actions()
    }
    fun initObserver(viewLifecycleOwner: LifecycleOwner, viewModel: StickerViewModel) {
        this.viewModel = viewModel
        viewModel.liveData.observe(viewLifecycleOwner) {
            when (it) {
                is StickerViewModel.Result.StickerDTOUpdated -> {
                    _livedata.value = Event(Actions.ShowStickerDetails(viewModel.getStickerDto()!!))
                    changeStateOfFav(viewModel.getStickerDto()!!.isFavourite)
                }

                is StickerViewModel.Result.StickerUpdated -> {
                    viewModel.getStickerDto()?.apply {
                        this.tags = listOf(it.updatedSticker.tags!!)
                    }
                    _livedata.value = Event(Actions.ShowStickerDetails(viewModel.getStickerDto()!!))
                }
                else -> {

                }
            }
        }
    }

    fun onCreate(stickerDTO: StickerDTO?) {
        viewModel.setStickerDto(stickerDTO)
       // viewModel.addRecentSticker(stickerDTO?.stickerId!!)
    }

    fun onViewCreated() {
       viewModel.getStickerDto()?.let {
           _livedata.value = Event(Actions.ShowStickerDetails(it))
           changeStateOfFav(it.isFavourite)
       }
    }

    private fun changeStateOfFav(favourite: Boolean) {
        if (favourite) {
            _livedata.postValue(Event(Actions.ShowFilledFavIcon))
        } else {
            _livedata.postValue(Event(Actions.ShowOutlinedFavIcon))
        }
    }

    fun onAddToFavClick() {
        viewModel.getStickerDto()?.let {
           if (it.isFavourite) {
               viewModel.removeStickerFromFav(it.stickerId!!)
           } else {
               viewModel.addToFavorites(it.stickerId)
           }
        }
    }

    fun onShareClick() {
        viewModel.getStickerDto()?.let {
            viewModel.shareSticker(it.sticker())
        }
    }

    fun onAddTagClick() {
        _livedata.value = Event(Actions.ShowAddTagBottomSheet)
    }

    fun onAddTag(tag: String, stickerId: Int?) {
        viewModel.addTagToSticker(stickerId!!, tag)
    }
}