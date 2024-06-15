package com.android.stickerpocket.presentation.sticker

import android.view.View
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
    private val itemClickListener: ((fav: Sticker, position: Int) -> Unit)?
): RecyclerView.ViewHolder(binding.root) {
    fun bind(sticker: Sticker) {
        binding.root.setOnDragListener(DragListener())
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
            if (sticker.isFavourite){
                favImg.visibility= View.VISIBLE
            }else{
                favImg.visibility= View.GONE
            }
        }
    }
}