package com.android.stickerpocket.presentation.sticker

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.android.stickerpocket.presentation.StickerViewModel

class StickerFragmentInteractor {

    sealed class Actions {
        object InitGiphyView: Actions()
        object InitCategoryView: Actions()
    }
    private val _liveData = MutableLiveData<Actions>()
    val liveData = _liveData

    val viewModel by lazy {
        StickerViewModel()
    }
    fun initObserver(viewLifecycleOwner: LifecycleOwner) {
        viewModel.liveData.observe(viewLifecycleOwner, Observer {

        })
    }

    fun onViewCreated() {
        _liveData.value = Actions.InitCategoryView
        _liveData.postValue(Actions.InitGiphyView)
    }
}