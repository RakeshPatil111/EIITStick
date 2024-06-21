package com.android.stickerpocket.presentation.sticker

import android.R
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.load
import com.android.stickerpocket.databinding.CvGifItemBinding
import com.android.stickerpocket.domain.model.Sticker
import com.android.stickerpocket.utils.DragListener
import com.android.stickerpocket.utils.StickerExt.toLoadableImage
import com.android.stickerpocket.utils.ViewExt.shakeMe


class StickerViewHolder(
    private val binding: CvGifItemBinding,
    private val imageLoader: ImageLoader,
    private val itemClickListener: ((fav: Sticker, position: Int) -> Unit)?,
    private val itemLongClickListener: ((fav: Sticker, position: Int) -> Unit)?,
    private val itemDeleteClickListener: ((fav: Sticker, position: Int) -> Unit)?,
    val didOpenForCategory: Boolean,
    private val didOpenForReorganize: Boolean
) : RecyclerView.ViewHolder(binding.root) {
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
            if (!sticker.isOrganizeMode) {
                itemClickListener?.let { gif ->

                    sivGifImage.setOnClickListener {
                        gif(sticker, adapterPosition)
                    }
                }
            } else {

            }

            itemLongClickListener?.let { gif ->
                if (!sticker.isOrganizeMode) {
                    sivGifImage.setOnLongClickListener {
                        gif(sticker, adapterPosition)
                        true
                    }
                }
            }

            itemDeleteClickListener?.let { gif ->
                if (sticker.isOrganizeMode) {
                    ivRemove.setOnClickListener {
                        gif(sticker, adapterPosition)
                        true
                    }
                }
            }

            if (sticker.isFavourite) {
                favImg.visibility = View.VISIBLE
            } else {
                favImg.visibility = View.GONE
            }

            if (sticker.isOrganizeMode) {
                ivRemove.visibility = View.VISIBLE
                sivGifImage.shakeMe()
            } else {
                ivRemove.visibility = View.GONE
            }

            if (sticker.selectionon) {
                cbSelect.visibility = View.VISIBLE
                sivGifImage.shakeMe()
            } else {
                cbSelect.visibility = View.GONE
            }
        }
    }
}