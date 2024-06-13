package com.android.stickerpocket.presentation

import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.load
import com.android.stickerpocket.R
import com.android.stickerpocket.StickerApplication
import com.android.stickerpocket.databinding.CvGifItemBinding
import com.android.stickerpocket.domain.model.Favourites
import com.android.stickerpocket.domain.model.Sticker
import com.android.stickerpocket.utils.StickerExt.toLoadableImage
import java.io.File

class CommonStickerAdapter : RecyclerView.Adapter<CommonStickerAdapter.GifListViewHolder>() {

    private val differ = AsyncListDiffer(this, diffUtilGifs)
    private lateinit var context: Context
    private var gifClickAction: ((fav: Sticker, position: Int) -> Unit)? = null
    private lateinit var imageLoader: ImageLoader
    private lateinit var scrollType: String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GifListViewHolder {
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
        return GifListViewHolder(
            CvGifItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = differ.currentList.size

    override fun onBindViewHolder(holder: GifListViewHolder, position: Int) = holder.bind(differ.currentList[position])

    fun updateList(list: List<Sticker>) {
        differ.submitList(list)
        notifyDataSetChanged()
    }
    inner class GifListViewHolder(private val binding: CvGifItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(sticker: Sticker) {
            val res = StickerApplication.instance.resources.getResourceName(R.raw.mad1)
            binding.apply {
                sivGifImage.load(sticker.toLoadableImage(), imageLoader){
                    target(
                        onStart = {
                            loader.visibility = VISIBLE
                        },
                        onSuccess = {
                            loader.visibility = GONE
                            sivGifImage.load(sticker.toLoadableImage(), imageLoader)
                        }
                    )
                }
                gifClickAction?.let { gif ->
                    sivGifImage.setOnClickListener {
                        gif(sticker, adapterPosition)
                    }
                }
            }
        }
    }

    fun gifActionClick(action: (gif: Sticker, position: Int) -> Unit){
        this.gifClickAction = action
    }

    companion object{
        val diffUtilGifs = object: DiffUtil.ItemCallback<Sticker>(){
            override fun areItemsTheSame(oldItem: Sticker, newItem: Sticker): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Sticker, newItem: Sticker): Boolean {
                return oldItem == newItem
            }

        }
    }
}