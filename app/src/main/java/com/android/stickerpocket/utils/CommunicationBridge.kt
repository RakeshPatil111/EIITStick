package com.android.stickerpocket.utils

import androidx.lifecycle.MutableLiveData
import com.android.stickerpocket.domain.model.Sticker

object CommunicationBridge {

    var isOrganizationMode= MutableLiveData(false)
    var isSelectionMode= MutableLiveData(false)
    var selectedStickes= MutableLiveData(ArrayList<Sticker>())
    var selectedCatPosition= MutableLiveData(-1)

    var gifyEnabled=MutableLiveData(true)
    var tenorEnabled=MutableLiveData(true)
}