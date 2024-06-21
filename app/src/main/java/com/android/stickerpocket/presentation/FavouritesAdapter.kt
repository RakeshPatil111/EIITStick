package com.android.stickerpocket.presentation

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.android.stickerpocket.databinding.CvGifItemBinding
import com.android.stickerpocket.domain.model.Sticker
import com.android.stickerpocket.presentation.sticker.StickerViewHolder

class FavouritesAdapter: RecyclerView.Adapter<StickerViewHolder>() {
    private val differ = AsyncListDiffer(this, stickerDiffUtil)
    private var actionItemClick: ((sticker: Sticker, position: Int) -> Unit)? = null
    private var actionItemLongClick: ((sticker: Sticker, position: Int) -> Unit)? = null
    private var actionItemDelete: ((sticker: Sticker, position: Int) -> Unit)? = null
    private lateinit var imageLoader: ImageLoader
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StickerViewHolder {
        imageLoader = ImageLoader
            .Builder(parent.context)
            .components {
                if (Build.VERSION.SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
        return StickerViewHolder(
            CvGifItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), imageLoader = imageLoader,
            itemClickListener = actionItemClick,
            itemLongClickListener=actionItemLongClick,
            itemDeleteClickListener = actionItemDelete,
            didOpenForCategory = false,
            didOpenForReorganize = false
        )
    }

    override fun getItemCount() = differ.currentList.size

    override fun onBindViewHolder(holder: StickerViewHolder, position: Int) = holder.bind(differ.currentList[position])

    fun onItemClick(action: (sticker: Sticker, position: Int) -> Unit){
        this.actionItemClick = action
    }
    fun onItemLongClick(action: (sticker: Sticker, position: Int) -> Unit){
        this.actionItemLongClick = action
    }
    fun updateList(favoriteStickers: List<Sticker>) {
        differ.submitList(favoriteStickers)
    }

    companion object{
        val stickerDiffUtil = object: DiffUtil.ItemCallback<Sticker>(){
            override fun areItemsTheSame(oldItem: Sticker, newItem: Sticker): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Sticker, newItem: Sticker): Boolean {
                return oldItem == newItem
            }

        }
    }
}