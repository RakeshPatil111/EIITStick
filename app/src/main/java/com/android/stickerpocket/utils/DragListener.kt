package com.android.stickerpocket.utils

import android.util.Log
import android.view.DragEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.stickerpocket.R
import com.android.stickerpocket.utils.ViewExt.findViewAt
import com.android.stickerpocket.utils.ViewExt.getViewByCoordinates

class DragListener() : View.OnDragListener {

    private var isDropped = false
    var sourcePosition: Int? = null
    var targetPosition: Int? = null
    var listener: OnStickerDropOnCategoryListener? = null
    override fun onDrag(v: View, event: DragEvent): Boolean {
        when (event.action) {
             //DragEvent.ACTION_DROP,
             //DragEvent.ACTION_DRAG_EXITED,
            DragEvent.ACTION_DRAG_ENDED -> {
                Log.e("MyLog", "ACTION_DROP")
                isDropped = true
                var positionTarget: Int

                val viewSource = event.localState as? View
                val viewId = v.id

                //val flItem = R.id.frame_layout_item
                val tvEmptyListTop = R.id.cl_parent
                val tvEmptyListBottom = R.id.cv_sticker
                val rvTop = R.id.rv_stickers
                val rvBottom = R.id.rv_category
                Log.d("MyLog", "viewId $viewId")

                when (viewId) {
                    tvEmptyListTop, tvEmptyListBottom, rvTop, rvBottom -> {
                        Log.d("MyLog", "flItem, tvEmptyListTop, tvEmptyListBottom, rvTop, rvBottom")
                        val target: RecyclerView = when (viewId) {
                            tvEmptyListTop, rvTop -> v.rootView.findViewById(rvBottom)
                            tvEmptyListBottom, rvBottom -> v.rootView.findViewById(rvTop)
                            else -> v.parent as RecyclerView
                        }
                        val cvFavY = v.rootView.findViewById<View>(R.id.cv_fav_sticker).y
                        positionTarget = v.tag as Int

                        val parentView =(target.parent as ViewGroup).getViewByCoordinates(event.x, event.y)
                        if (parentView?.id != R.id.rv_category) {
                            return false
                        }

                        val droppedCategoryTag = target.findViewAt(target, event.x.toInt(), event.y.toInt())?.tag as Int

                        if (viewSource != null) {
                            val positionSource = viewSource.tag as Int
                            sourcePosition = positionSource
                            targetPosition = droppedCategoryTag
                        }

                        if (sourcePosition != null && targetPosition != null) {
                            listener?.onDrop(sourcePosition!!, targetPosition!!)
                            sourcePosition = null
                            targetPosition = null
                        }
                    }
                }
            }

            DragEvent.ACTION_DRAG_EXITED -> {
                Log.d("MyLog", "Enter ACTION_DRAG_EXITED")
            }
        }

        if (!isDropped && event.localState != null) {
            (event.localState as? View)?.visibility = View.VISIBLE
        }
        return true
    }

    fun setDropListener(listener: OnStickerDropOnCategoryListener) {
        this.listener = listener
    }
}
interface OnStickerDropOnCategoryListener {
    fun onDrop(sourceStickerPosition: Int, targetCategoryPosition: Int)
}