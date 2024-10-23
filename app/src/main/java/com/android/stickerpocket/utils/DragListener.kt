package com.android.stickerpocket.utils

import android.util.Log
import android.view.DragEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.stickerpocket.R
import com.google.android.material.imageview.ShapeableImageView

class DragListener(val dbs: View.DragShadowBuilder? = null) : View.OnDragListener {

    private var isDropped = false
    var sourcePosition: Int? = null
    var targetPosition: Int? = null
    var listener: OnStickerDropOnCategoryListener? = null
    override fun onDrag(v: View, event: DragEvent): Boolean {
        when (event.action) {

            DragEvent.ACTION_DROP -> {
                Log.e("MyLog", "ACTION_DROP")
                isDropped = true

                val viewSource = event.localState as? View
                val draggedViewId = v.id

                if (viewSource == null) {
                    return true
                }
                //val flItem = R.id.frame_layout_item
                val stickerParentView = R.id.cl_parent
                val catParentView = R.id.cv_category_item
                val rvStickers = R.id.rv_stickers
                val rvCategory = R.id.rv_category
                Log.d("MyLog", "viewId $draggedViewId")
                when ((viewSource.parent as ViewGroup).id) {

                    stickerParentView, catParentView, rvStickers, rvCategory -> {
                        Log.d("MyLog", "flItem, tvEmptyListTop, tvEmptyListBottom, rvTop, rvBottom")
                        val target: RecyclerView = when (draggedViewId) {
                            stickerParentView, rvStickers -> v.rootView.findViewById(rvStickers)
                            catParentView, rvCategory -> v.rootView.findViewById(rvCategory)
                            else -> v.parent as RecyclerView
                        }

                        if (target.id != R.id.rv_category) {
                            //v.alpha = 1f
                            return false
                        }

                        // Check if viewSource is Child of Category ie Shapeable Image View
                        // Get TAG of viewSource
                        if (viewSource !is ShapeableImageView) {
                            return false
                        }
                        v.tag?.let {
                            if (viewSource != null) {
                                val positionSource = viewSource.tag as Int
                                sourcePosition = positionSource
                                targetPosition = it as Int
                            }

                            if (sourcePosition != null && targetPosition != null) {
                                listener?.onDrop(sourcePosition!!, targetPosition!!)
                                sourcePosition = null
                                targetPosition = null
                            }
                        }
                    }
                }
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