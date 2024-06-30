package com.android.stickerpocket.presentation

import android.os.Build.VERSION.SDK_INT
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.android.stickerpocket.databinding.CvGifItemBinding
import com.android.stickerpocket.domain.model.Sticker
import com.android.stickerpocket.presentation.sticker.StickerViewHolder
import com.android.stickerpocket.utils.ItemClickSupport
import java.util.Collections

class CommonStickerAdapter() : RecyclerView.Adapter<StickerViewHolder>() {
    var stickers = mutableListOf<Sticker>()
    private var actionItemClick: ((sticker: Sticker, position: Int) -> Unit)? = null
    private var actionItemLongClick: ((sticker: Sticker, position: Int) -> Unit)? = null
    private var actionItemDelete: ((sticker: Sticker, position: Int) -> Unit)? = null
    private var actionStickerDrop: ((sourceStickerPosition: Int, targetCategoryPosition: Int) -> Unit)? = null
    private lateinit var imageLoader: ImageLoader
    var didOpenForCategory: Boolean = true
    var didOpenForReorganize: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StickerViewHolder {
        imageLoader = ImageLoader
            .Builder(parent.context)
            .components {
                if (SDK_INT >= 28) {
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
            itemLongClickListener = actionItemLongClick,
            itemDeleteClickListener = actionItemDelete,
            itemStickerDropListener = actionStickerDrop,
        )
    }

    override fun getItemCount() = stickers.size

    override fun onBindViewHolder(holder: StickerViewHolder, position: Int) {
        holder.bind(stickers[position])
    }

    fun updateList(list: List<Sticker>) {
        stickers = list.toMutableList()
        notifyDataSetChanged()
    }

    fun onItemClick(action: (sticker: Sticker, position: Int) -> Unit){
        this.actionItemClick = action
    }

    fun getList() = stickers

    fun onItemLongClick(action: (sticker: Sticker, position: Int) -> Unit){
        this.actionItemLongClick = action
    }

    fun isOpenedForCategory(value: Boolean) {
        didOpenForCategory = value
    }

    fun onItemDelete(action: (sticker: Sticker, position: Int) -> Unit){
        this.actionItemDelete = action
    }

    fun onRowMoved(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(stickers, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(stickers, i, i - 1)
            }
        }

        notifyItemMoved(fromPosition, toPosition)
    }

    fun onStickerDrop(action: (sourceStickerPosition: Int, targetCategoryPosition: Int) -> Unit) {
        this.actionStickerDrop = action
    }
}