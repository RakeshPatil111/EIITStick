package com.android.stickerpocket.presentation.sticker

import android.view.View
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.load
import com.android.stickerpocket.databinding.CvGifItemBinding
import com.android.stickerpocket.domain.model.Sticker
import com.android.stickerpocket.utils.DragListener
import com.android.stickerpocket.utils.StickerExt.toLoadableImage

class StickerViewHolder(
    private val binding: CvGifItemBinding,
    private val imageLoader: ImageLoader,
    private val itemClickListener: ((fav: Sticker, position: Int) -> Unit)?,
    private val itemLongClickListener: ((fav: Sticker, position: Int) -> Unit)?,
    val didOpenForCategory: Boolean
): RecyclerView.ViewHolder(binding.root) {
    fun bind(sticker: Sticker) {
        if (didOpenForCategory) binding.root.setOnDragListener(DragListener())
        binding.apply {
            sivGifImage.load(sticker.toLoadableImage(), imageLoader){
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
                sivGifImage.setOnClickListener {
                    gif(sticker, adapterPosition)
                }
            }

            itemLongClickListener?.let { gif ->
                sivGifImage.setOnLongClickListener {
                    gif(sticker, adapterPosition)
                    true
                }
            }
            if (sticker.isFavourite){
                favImg.visibility= View.VISIBLE
            }else{
                favImg.visibility= View.GONE
            }
        }
    }
}