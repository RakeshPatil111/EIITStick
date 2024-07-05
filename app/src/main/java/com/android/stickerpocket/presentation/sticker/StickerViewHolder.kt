package com.android.stickerpocket.presentation.sticker

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.load
import com.android.stickerpocket.CommunicationBridge
import com.android.stickerpocket.databinding.CvGifItemBinding
import com.android.stickerpocket.domain.model.Sticker
import com.android.stickerpocket.utils.DragListener
import com.android.stickerpocket.utils.OnItemDoubleClickListener
import com.android.stickerpocket.utils.StickerExt.toLoadableImage
import com.android.stickerpocket.utils.ViewExt.shakeMe


class StickerViewHolder(
    private val binding: CvGifItemBinding,
    private val imageLoader: ImageLoader,
    private val itemClickListener: ((fav: Sticker, position: Int) -> Unit)?,
    private val itemLongClickListener: ((fav: Sticker, position: Int) -> Unit)?,
    private val itemDeleteClickListener: ((fav: Sticker, position: Int) -> Unit)?,
    val didOpenForCategory: Boolean,
    private val didOpenForReorganize: Boolean,
    private val doubleClickListener: OnItemDoubleClickListener?
) : RecyclerView.ViewHolder(binding.root) {

    private var lastClickTime: Long = 0

    fun bind(sticker: Sticker) {
        if (didOpenForCategory) binding.root.setOnDragListener(DragListener())
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
                        gif(sticker, adapterPosition)
                    }
            }

            sivGifImage.setOnClickListener {
                val clickTime = System.currentTimeMillis()
                if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                    doubleClickListener?.onItemDoubleClick(sticker, adapterPosition)
                }
                lastClickTime = clickTime
            }

            itemLongClickListener?.let { gif ->
                sivGifImage.setOnLongClickListener {
                    gif(sticker, adapterPosition)
                    true
                }
            }

            itemDeleteClickListener?.let { gif ->
                ivRemove.setOnClickListener {
                    gif(sticker, adapterPosition)
                    true
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