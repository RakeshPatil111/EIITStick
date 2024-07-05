package com.android.stickerpocket.utils

import com.android.stickerpocket.domain.model.Sticker

interface OnItemDoubleClickListener {
    fun onItemDoubleClick(sticker: Sticker, position: Int)
}