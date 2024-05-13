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
import com.android.stickerpocket.databinding.CvGifItemBinding
import java.util.*

class GifListAdapter : RecyclerView.Adapter<GifListAdapter.GifListViewHolder>() {

    private val differ = AsyncListDiffer(this, diffUtilGifs)
    private lateinit var context: Context
    private var gifClickAction: ((gifs: Gifs, position: Int) -> Unit)? = null
    private lateinit var imageLoader: ImageLoader
    private lateinit var scrollType: String

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        GifListViewHolder(
            CvGifItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount() = differ.currentList.size

    override fun onBindViewHolder(holder: GifListViewHolder, position: Int) = holder.bind(differ.currentList[position])

    fun updateList(gifs: ArrayList<Gifs>, context: Context, scrollType: String) {
        this.context = context
        this.scrollType = scrollType
        differ.submitList(gifs)
        imageLoader = ImageLoader
        .Builder(context)
            .components {
                if (SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }

    inner class GifListViewHolder(private val binding: CvGifItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(gifs: Gifs) {
            binding.apply {

                if (scrollType == "HORIZONTAL"){
                    val layoutParams = ConstraintLayout.LayoutParams(170,170)
                    layoutParams.marginStart = 8
                    layoutParams.marginEnd = 4
                    layoutParams.topMargin = 4
                    layoutParams.bottomMargin = 8
                    root.layoutParams = layoutParams
                }

                sivGifImage.load(gifs.thumbnail, imageLoader){
                    target(
                        onStart = {
                            loader.visibility = VISIBLE
                        },
                        onSuccess = {
                            loader.visibility = GONE
                            sivGifImage.load(gifs.thumbnail, imageLoader)
                        }
                    )
                }
                gifClickAction?.let { gif ->
                    sivGifImage.setOnClickListener {
                        gif(gifs, adapterPosition)
                    }
                }
            }
        }
    }

    fun gifActionClick(action: (gif: Gifs, position: Int) -> Unit){
        this.gifClickAction = action
    }

    companion object{
        val diffUtilGifs = object: DiffUtil.ItemCallback<Gifs>(){
            override fun areItemsTheSame(oldItem: Gifs, newItem: Gifs): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Gifs, newItem: Gifs): Boolean {
                return oldItem == newItem
            }
        }
    }
}