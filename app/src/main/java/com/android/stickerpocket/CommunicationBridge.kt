package com.android.stickerpocket

import androidx.lifecycle.MutableLiveData
import com.android.stickerpocket.domain.model.Sticker

object CommunicationBridge {

    var isOrganizationMode= MutableLiveData(false)
    var isSelectionMode= MutableLiveData(false)
    var selectedStickes= MutableLiveData(ArrayList<Sticker>())
    var selectedCatPosition= MutableLiveData(-1)
}