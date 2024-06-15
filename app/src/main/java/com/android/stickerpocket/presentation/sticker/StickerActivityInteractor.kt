package com.android.stickerpocket.presentation.sticker

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.android.stickerpocket.presentation.StickerDTO
import java.io.File

class StickerActivityInteractor {
    private val _liveData = MutableLiveData<Actions>()
    val liveData = _liveData
    private lateinit var viewModel: StickerActivityViewModel
    sealed class Actions {
        data class ShareSticker(val file: File?): Actions()
        object ShowLoading: Actions()
    }

    fun initObserver(viewLifecycleOwner: LifecycleOwner, viewModel: StickerActivityViewModel) {
        this.viewModel = viewModel
        this.viewModel.liveData.observe(viewLifecycleOwner) {
            when (it) {
                is StickerActivityViewModel.Result.StickerDownloaded -> {
                    _liveData.value = Actions.ShareSticker(it.gifFile)
                }
                else -> {}
            }
        }
    }

    fun onShareSticker(stickerDTO: StickerDTO) {
        _liveData.value = Actions.ShowLoading
        viewModel.downloadSticker(stickerDTO)
    }
}