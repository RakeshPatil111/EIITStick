package com.android.stickerpocket.presentation.sticker

import android.content.ClipData
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.load
import com.android.stickerpocket.utils.CommunicationBridge
import com.android.stickerpocket.databinding.CvGifItemBinding
import com.android.stickerpocket.domain.model.Sticker
import com.android.stickerpocket.utils.DragListener
import com.android.stickerpocket.utils.OnStickerDropOnCategoryListener
import com.android.stickerpocket.utils.OnItemDoubleClickListener
import com.android.stickerpocket.utils.StickerExt.toLoadableImage
import com.android.stickerpocket.utils.ViewExt.shakeMe


class StickerViewHolder(
    private val binding: CvGifItemBinding,
    private val imageLoader: ImageLoader,
    private val itemClickListener: ((fav: Sticker, position: Int) -> Unit)?,
    private val itemLongClickListener: ((fav: Sticker, position: Int) -> Unit)?,
    private val itemDeleteClickListener: ((fav: Sticker, position: Int) -> Unit)?,
    private val doubleClickListener: OnItemDoubleClickListener?,
    private val itemStickerDropListener: ((sourceStickerPosition: Int, targetCategoryPosition: Int) -> Unit)? = null
) : RecyclerView.ViewHolder(binding.root) {

    private var lastClickTime: Long = 0
    private var handler: Handler = Handler(Looper.getMainLooper())
    private val doubleClickDelay: Long = 300  // Milliseconds

    fun bind(sticker: Sticker) {
        binding.root.tag = position
        binding.sivGifImage.tag = position
        binding.apply {
            sivGifImage.load(sticker.toLoadableImage(), imageLoader) {
                target(
                    onStart = {
                        loader.visibility = View.VISIBLE
                    },
                    onSuccess = {
                        loader.visibility = View.GONE
                        sivGifImage.load(sticker.toLoadableImage(), imageLoader)
                    }
                )
            }

            itemClickListener?.let { gif ->
                if (CommunicationBridge.isOrganizationMode.value == false)
                    sivGifImage.setOnClickListener {
                        val currentTime = System.currentTimeMillis()

                        if (currentTime - lastClickTime < doubleClickDelay) {
                            handler.removeCallbacksAndMessages(null)
                            doubleClickListener?.onItemDoubleClick(sticker, adapterPosition)
                        } else {
                            handler.postDelayed({
                                gif(sticker, adapterPosition)
                            }, doubleClickDelay)
                        }

                        lastClickTime = currentTime
                    }
            }


            itemLongClickListener?.let { gif ->
                    sivGifImage.setOnLongClickListener {
                        // gif(sticker, adapterPosition)
                        if (CommunicationBridge.isOrganizationMode.value == false) {
                            val dbs = View.DragShadowBuilder(it)
                            var clipData = ClipData.newPlainText("", "")
                            it.startDragAndDrop(clipData, dbs, it, 0)
                            val dragListener = DragListener(dbs)
                            binding.root.setOnDragListener(dragListener)
                            dragListener.setDropListener(object : OnStickerDropOnCategoryListener {
                                override fun onDrop(
                                    sourceStickerPosition: Int,
                                    targetCategoryPosition: Int
                                ) {
                                    itemStickerDropListener?.invoke(
                                        sourceStickerPosition,
                                        targetCategoryPosition
                                    )
                                }

                            })
                        }
                        true
                    }
            }

            itemDeleteClickListener?.let { gif ->
                ivRemove.setOnClickListener {
                    gif(sticker, adapterPosition)
                }
            }

            cbSelect.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked){
                    CommunicationBridge.selectedStickes.value?.add(sticker)
                } else {
                    CommunicationBridge.selectedStickes.value?.remove(sticker)
                }
            }

            if (sticker.isFavourite) {
                favImg.visibility = View.VISIBLE
            } else {
                favImg.visibility = View.GONE
            }

            if (CommunicationBridge.isOrganizationMode.value == true) {
                ivRemove.visibility = View.VISIBLE
                sivGifImage.shakeMe()
            } else {
                ivRemove.visibility = View.GONE
            }

            if (CommunicationBridge.isSelectionMode.value == true) {
                cbSelect.visibility = View.VISIBLE
                sivGifImage.shakeMe()
            } else {
                cbSelect.visibility = View.GONE
            }
        }
    }

    companion object {
        private const val DOUBLE_CLICK_TIME_DELTA: Long = 300 // Milliseconds
    }
}