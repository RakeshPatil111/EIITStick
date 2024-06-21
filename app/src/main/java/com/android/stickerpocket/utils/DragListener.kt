package com.android.stickerpocket.utils

import android.util.Log
import android.view.DragEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.android.stickerpocket.R
import com.android.stickerpocket.presentation.CommonStickerAdapter
import com.android.stickerpocket.presentation.sticker.EmojiCategoryListAdapter

class DragListener() : View.OnDragListener {

    private var isDropped = false

    override fun onDrag(v: View, event: DragEvent): Boolean {
        when (event.action) {
            // DragEvent.ACTION_DROP,
            // DragEvent.ACTION_DRAG_EXITED
            DragEvent.ACTION_DROP -> {
                Log.e("MyLog", "ACTION_DROP")
                isDropped = true
                var positionTarget: Int

                val viewSource = event.localState as? View
                val viewId = v.id
                //val flItem = R.id.frame_layout_item
                val tvEmptyListTop = R.id.siv_gif_image
                val tvEmptyListBottom = R.id.cv_sticker
                val rvTop = R.id.rv_stickers
                val rvBottom = R.id.rv_category
                Log.d("MyLog", "viewId $viewId")
                when (viewId) {
                    tvEmptyListTop, tvEmptyListBottom, rvTop, rvBottom -> {
                        Log.d("MyLog", "flItem, tvEmptyListTop, tvEmptyListBottom, rvTop, rvBottom")
                        val target: RecyclerView = when (viewId) {
                            tvEmptyListTop, rvTop -> v.rootView.findViewById(rvTop)
                            tvEmptyListBottom, rvBottom -> v.rootView.findViewById(rvBottom)
                            else -> v.parent as RecyclerView
                        }

                        positionTarget = v.tag as Int
                        Log.d("MyLog", "positionTarget = $positionTarget")

                        Log.d("MyLog", "viewSource $viewSource")
                        if (viewSource != null) {
                            val source = viewSource.parent as RecyclerView
                            val adapterSource = source.adapter as CommonStickerAdapter
                            val positionSource = viewSource.tag as Int
                            Log.d("MyLog", "positionSource $positionSource")
                            val list = adapterSource.getList()[positionSource]
                            val listSource = adapterSource.getList().toMutableList()

                            //listSource.removeAt(positionSource)
                            //adapterSource.updateList(listSource)
                            //adapterSource.notifyDataSetChanged()

                            val adapterTarget = target.adapter as EmojiCategoryListAdapter
                            Log.d("MyLog", "target $target")
                            val customListTarget = adapterTarget.getList().toMutableList()
                            Log.d("MyLog", "customListTarget $customListTarget")
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
}