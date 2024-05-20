package com.android.stickerpocket.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.android.stickerpocket.presentation.sticker.StickerViewModel
import com.android.stickerpocket.presentation.sticker.StickerActivityViewModel

class StickerViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            StickerActivityViewModel::class.java -> StickerActivityViewModel() as T
            StickerViewModel::class.java -> StickerViewModel() as T
            else -> throw Throwable("Unsupported view model")
        }
    }
}