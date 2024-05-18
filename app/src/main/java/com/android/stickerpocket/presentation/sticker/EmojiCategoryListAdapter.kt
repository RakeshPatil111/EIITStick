package com.android.stickerpocket.presentation.sticker

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.android.stickerpocket.databinding.CvStickerItemBinding
import com.android.stickerpocket.presentation.Sticker

class EmojiCategoryListAdapter :
    RecyclerView.Adapter<EmojiCategoryListAdapter.StepperViewHolder>(){

    private var selected = -1
    private var stickerClickAction: ((sticker: Sticker, position: Int) -> Unit)? = null
    private var stickerLongClickAction: ((sticker: Sticker, position: Int) -> Unit)? = null
    private val differ = AsyncListDiffer(this, diffUtilEmoji)
    private var bindings = mutableListOf<CvStickerItemBinding>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        StepperViewHolder(
            CvStickerItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount() = differ.currentList.size

    override fun onBindViewHolder(holder: StepperViewHolder, position: Int) =
        holder.bind(differ.currentList[position])

    fun updateList(stickers: ArrayList<Sticker>) {
        differ.submitList(stickers)
        notifyDataSetChanged()
    }

    inner class StepperViewHolder(private val binding: CvStickerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(selectedSticker: Sticker) {
            binding.apply {
                ivStickerThumbnail.load(selectedSticker.thumbnail)

                if (selected == adapterPosition) {
                    cvSticker.strokeColor = Color.GREEN
                    cvSticker.strokeWidth = 6
                } else {
                    cvSticker.strokeColor = Color.TRANSPARENT
                }

                stickerClickAction?.let { sticker ->
                    cvSticker.setOnClickListener {
                        sticker(selectedSticker, adapterPosition)
                        setSelectedItem(adapterPosition)
                    }
                }

                stickerLongClickAction?.let { sticker ->
                    cvSticker.setOnLongClickListener {
                        sticker(selectedSticker, adapterPosition)
                        true
                    }
                }

            }
        }
    }

    fun stickerActionClick(action: (sticker: Sticker, position: Int) -> Unit) {
        this.stickerClickAction = action
    }

    fun stickerActionLongClick(action: (sticker: Sticker, position: Int) -> Unit) {
        this.stickerLongClickAction = action
    }

    private fun setSelectedItem(position: Int) {
        if (selected != position) {
            val previousSelectedPosition = selected
            selected = position
            notifyItemChanged(previousSelectedPosition)
            notifyItemChanged(selected)
        }
    }

    companion object {
        val diffUtilEmoji = object : DiffUtil.ItemCallback<Sticker>() {
            override fun areItemsTheSame(oldItem: Sticker, newItem: Sticker): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Sticker, newItem: Sticker): Boolean {
                return oldItem == newItem
            }
        }
    }
}