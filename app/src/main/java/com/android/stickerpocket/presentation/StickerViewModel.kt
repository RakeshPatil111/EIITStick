package com.android.stickerpocket.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StickerViewModel: ViewModel() {
    sealed class Result {

    }

    private val _liveData = MutableLiveData<Result>()
    val liveData: MutableLiveData<Result> = _liveData

    init {

    }
}