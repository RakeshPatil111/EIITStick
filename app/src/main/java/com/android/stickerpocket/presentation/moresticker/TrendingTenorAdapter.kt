package com.android.stickerpocket.presentation.moresticker

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.load
import com.android.stickerpocket.databinding.CvGifItemBinding
import com.android.stickerpocket.presentation.StickerDTO
import com.android.stickerpocket.presentation.sticker.StickerActivity


class TrendingTenorAdapter: RecyclerView.Adapter<TrendingTenorAdapter.ViewHolder>() {

    var items: List<StickerDTO> = listOf()
    private lateinit var imageLoader: ImageLoader
    var displayMetrics = DisplayMetrics()
    private var screenWidth = 0
    private var loading = false
    private lateinit var listenr: OnTrendingGifListener
    private var currentPage = 0

    inner class ViewHolder(val binding: CvGifItemBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        (parent.context as StickerActivity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        screenWidth = displayMetrics.widthPixels

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
        return ViewHolder(
            CvGifItemBinding.inflate(LayoutInflater.from(parent.context),
                parent, false)
        )
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        Log.w("ITEM", "${item.id}, $position")
        val itemPadding = 12

        //here you may change the divide amount from 2.5 to whatever you need
        val itemWidth = (screenWidth - itemPadding).div(3.25)
        val layoutParams = holder.itemView.layoutParams as RecyclerView.LayoutParams
        layoutParams.height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 95F,
            holder.itemView.context.resources.displayMetrics).toInt()
        layoutParams.width = itemWidth.toInt()
        layoutParams.topMargin = 12
        holder.itemView.layoutParams = layoutParams
        holder.binding.apply {
            sivGifImage.strokeColor = ColorStateList.valueOf(Color.TRANSPARENT)
            ivRemove.visibility = View.GONE
            cbSelect.visibility = View.GONE
            favImg.visibility = View.GONE
            sivGifImage.load(item.thumbnail, imageLoader) {
                target(
                    onSuccess = {
                        Log.w("ITEM", "${item.thumbnail}, $position")
                        holder.binding.sivGifImage.load(item.thumbnail, imageLoader)
                    }
                )
            }
        }
        if (!loading && position == items.size - 2) {
            loading = true
            listenr.loadMore()
        }
        holder.binding.root.setOnClickListener { listenr.onGifItemClick(item) }
    }

    fun updateList(newList: List<StickerDTO>, page: Int = 1) {
        items = newList
        notifyDataSetChanged()
    }

    fun setListener(itemListener: OnTrendingGifListener) {
        listenr = itemListener
    }

    interface OnTrendingGifListener {
        fun onGifItemClick(item: StickerDTO)
        fun loadMore()
    }
}