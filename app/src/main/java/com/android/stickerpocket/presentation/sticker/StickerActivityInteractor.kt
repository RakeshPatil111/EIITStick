package com.android.stickerpocket.presentation.sticker

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.android.stickerpocket.presentation.StickerDTO
import java.io.File

class StickerActivityInteractor {
    private val _liveData = MutableLiveData<Actions>()
    val liveData = _liveData
    private lateinit var viewModel: StickerViewModel
    sealed class Actions {
        data class ShareSticker(val file: File?): Actions()
        object ShowLoading: Actions()
    }

    fun initObserver(viewLifecycleOwner: LifecycleOwner, viewModel: StickerViewModel) {
        this.viewModel = viewModel
        this.viewModel.liveData.observe(viewLifecycleOwner) {
            when (it) {
                is StickerViewModel.Result.StickerDownloaded -> {
                    _liveData.value = Actions.ShareSticker(it.gifFile)
                }
                is StickerViewModel.Result.ShareSticker -> {
                    _liveData.value = Actions.ShareSticker(it.gifFile)
                }
                else -> {}
            }
        }
    }
}