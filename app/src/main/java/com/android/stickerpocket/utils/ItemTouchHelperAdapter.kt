package com.android.stickerpocket.utils

interface ItemTouchHelperAdapter {
    fun onItemMove(fromPosition: Int, toPosition: Int)
    fun onDragComplete()
}