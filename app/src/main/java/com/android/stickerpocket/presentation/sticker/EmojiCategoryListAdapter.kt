package com.android.stickerpocket.presentation.sticker

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.stickerpocket.databinding.CvStickerItemBinding
import com.android.stickerpocket.domain.model.Category

class EmojiCategoryListAdapter :
    RecyclerView.Adapter<EmojiCategoryListAdapter.StepperViewHolder>(){

    private var selected = -1
    private var stickerClickAction: ((category: Category, position: Int) -> Unit)? = null
    private var stickerLongClickAction: ((category: Category, position: Int) -> Unit)? = null
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

    override fun onBindViewHolder(holder: StepperViewHolder, position: Int) {
        differ.currentList.forEach {
            if (it.position == position)
                holder.bind(it, position)
        }
    }

    fun updateList(list: List<Category>) {
        differ.submitList(list)
        notifyDataSetChanged()
    }

    inner class StepperViewHolder(private val binding: CvStickerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(category: Category, position: Int) {
            binding.apply {
                //ivStickerThumbnail.load(selectedSticker.thumbnail)
                ivStickerThumbnail.text = String(Character.toChars(category.unicode.toInt(16)))
                if (category.isHighlighted) {
                    cvSticker.strokeColor = Color.GREEN
                    cvSticker.strokeWidth = 6
                } else {
                    cvSticker.strokeColor = Color.TRANSPARENT
                }
//                if (selected == position) {
//                    cvSticker.strokeColor = Color.GREEN
//                    cvSticker.strokeWidth = 6
//                } else {
//                    cvSticker.strokeColor = Color.TRANSPARENT
//                }
//
//                stickerClickAction?.let { category ->
//                    cvSticker.setOnClickListener {
//                        category(category, position)
//                        setSelectedItem(adapterPosition)
//                    }
//                }

                stickerLongClickAction?.let { c ->
                    cvSticker.setOnLongClickListener {
                        c.invoke(category, position)
                        true
                    }
                }
                stickerClickAction?.let { c ->
                    cvSticker.setOnClickListener {
                        c.invoke(category, adapterPosition)
                        setSelectedItem(position)
                    }
                }
            }
        }
    }

    fun stickerActionClick(action: (category: Category, position: Int) -> Unit) {
        this.stickerClickAction = action
    }

    fun stickerActionLongClick(action: (category: Category, position: Int) -> Unit) {
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
        val diffUtilEmoji = object : DiffUtil.ItemCallback<Category>() {
            override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
                return oldItem == newItem
            }
        }
    }
}