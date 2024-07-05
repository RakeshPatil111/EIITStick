package com.android.stickerpocket.presentation.sticker

import android.os.Build.VERSION.SDK_INT
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.load
import com.android.stickerpocket.CommunicationBridge
import com.android.stickerpocket.databinding.CvGifItemBinding
import com.android.stickerpocket.domain.model.Sticker
import com.android.stickerpocket.utils.StickerExt.toLoadableImage
import com.android.stickerpocket.utils.ViewExt.shakeMe

class NoTagStickerAdapter: RecyclerView.Adapter<NoTagStickerAdapter.ViewHolder>() {
    private var stickers: List<Sticker> = listOf()
    private var listener: OnStickerClickListener? = null
    private lateinit var imageLoader: ImageLoader

    inner class ViewHolder(val view: CvGifItemBinding) : RecyclerView.ViewHolder(view.root)

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
        return ViewHolder(CvGifItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun getItemCount() = stickers.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val sticker = stickers[position]
        holder.view.apply {
            ivRemove.visibility = View.GONE
            cbSelect.visibility = View.GONE
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
            sivGifImage.setOnClickListener {
                listener?.onStickerClick(position, sticker)
            }
        }
    }

    fun updateList(stickers: List<Sticker>) {
        this.stickers = stickers
        notifyDataSetChanged()
    }

    fun setListener(listener: OnStickerClickListener) {
        this.listener = listener
    }

    interface OnStickerClickListener {
        fun onStickerClick(position: Int, sticker: Sticker)
    }
}