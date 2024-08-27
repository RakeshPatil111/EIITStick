package com.android.stickerpocket.presentation.settings.delete

import android.content.ClipData
import android.os.Build.VERSION.SDK_INT
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.load
import com.android.stickerpocket.databinding.CvGifItemBinding
import com.android.stickerpocket.domain.model.Sticker
import com.android.stickerpocket.presentation.sticker.StickerViewHolder
import com.android.stickerpocket.utils.CommunicationBridge
import com.android.stickerpocket.utils.DragListener
import com.android.stickerpocket.utils.OnItemDoubleClickListener
import com.android.stickerpocket.utils.OnStickerDropOnCategoryListener
import com.android.stickerpocket.utils.StickerExt.toLoadableImage
import com.android.stickerpocket.utils.ViewExt.shakeMe
import java.util.Collections

class DeletedStickerAdapter  : RecyclerView.Adapter<DeletedStickerAdapter.ViewHolder>() {
    var stickers = mutableListOf<Sticker>()
    private lateinit var imageLoader: ImageLoader
    private var onClickListener: OnDeleteStickerClick? = null
    val setOfSelectedStickers = mutableSetOf<Sticker>()

    init {
        setOfSelectedStickers.clear()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
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
        return ViewHolder(
            CvGifItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = stickers.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val sticker = stickers[position]
        holder.binding.root.tag = position
        holder.binding.sivGifImage.tag = position
        holder.binding.apply {
            ivRemove.visibility = View.VISIBLE
            cbSelect.visibility = View.VISIBLE
            favImg.visibility = View.GONE
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

            cbSelect.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                   setOfSelectedStickers.add(sticker)
                } else {
                    setOfSelectedStickers.remove(sticker)
                }
                onClickListener?.onStickerCheck()
            }


            ivRemove.setOnClickListener {
                onClickListener?.onClick(sticker)
            }
        }
    }

    fun updateList(list: List<Sticker>) {
        stickers = list.toMutableList()
        notifyDataSetChanged()
    }

    fun setOnClickListener(onClickListener: OnDeleteStickerClick) {
        this.onClickListener = onClickListener
    }

    fun getSelectedStickers() = setOfSelectedStickers.toList()

    interface OnDeleteStickerClick {
        fun onClick(sticker: Sticker)
        fun onStickerCheck()
    }

    inner class ViewHolder(val binding: CvGifItemBinding) : RecyclerView.ViewHolder(binding.root)
}