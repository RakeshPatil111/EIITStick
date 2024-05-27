package com.android.stickerpocket.presentation

import android.content.Context
import android.os.Build.VERSION.SDK_INT
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.load
import com.android.stickerpocket.StickerApplication
import com.android.stickerpocket.databinding.CvGifItemBinding
import com.android.stickerpocket.domain.model.Favourites
import java.io.File
import java.util.*

class GifListAdapter : RecyclerView.Adapter<GifListAdapter.GifListViewHolder>() {

    private val differ = AsyncListDiffer(this, diffUtilGifs)
    private lateinit var context: Context
    private var gifClickAction: ((fav: Favourites, position: Int) -> Unit)? = null
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

    fun updateList(list: List<Favourites>) {
        differ.submitList(list)
        notifyDataSetChanged()
    }
    inner class GifListViewHolder(private val binding: CvGifItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(fav: Favourites) {
            binding.apply {
                val cachedFile = File(StickerApplication.instance.cacheDir, fav.name + ".gif")
                val url = if (cachedFile.length() > 0) cachedFile else fav.url
                sivGifImage.load(url, imageLoader){
                    target(
                        onStart = {
                            loader.visibility = VISIBLE
                        },
                        onSuccess = {
                            loader.visibility = GONE
                            sivGifImage.load(url, imageLoader)
                        }
                    )
                }
                gifClickAction?.let { gif ->
                    sivGifImage.setOnClickListener {
                        gif(fav, adapterPosition)
                    }
                }
            }
        }
    }

    fun gifActionClick(action: (gif: Favourites, position: Int) -> Unit){
        this.gifClickAction = action
    }

    companion object{
        val diffUtilGifs = object: DiffUtil.ItemCallback<Favourites>(){
            override fun areItemsTheSame(oldItem: Favourites, newItem: Favourites): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Favourites, newItem: Favourites): Boolean {
                return oldItem == newItem
            }

        }
    }
}