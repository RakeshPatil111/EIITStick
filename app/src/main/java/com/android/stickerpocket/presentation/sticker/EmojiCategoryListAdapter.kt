package com.android.stickerpocket.presentation.sticker

import android.graphics.Color
import android.text.Html
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
    private var stickerClickAction: ((category: Category, position: Int, previouslySelected: Int) -> Unit)? = null
    private var stickerLongClickAction: ((category: Category, position: Int, previouslySelected: Int) -> Unit)? = null
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
        val category = differ.currentList[position]
        holder.bind(category, position)
    }

    fun updateList(list: List<Category>) {
        differ.submitList(list)
        notifyDataSetChanged()
    }

    inner class StepperViewHolder(private val binding: CvStickerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(category: Category, position: Int) {
            binding.apply {
                ivStickerThumbnail.text = Html.fromHtml(category.html)
                if (category.isHighlighted) {
                    selected = position
                    cvSticker.strokeColor = Color.GREEN
                    cvSticker.strokeWidth = 6
                } else {
                    cvSticker.strokeColor = Color.TRANSPARENT
                }

                stickerLongClickAction?.let { c ->
                    cvSticker.setOnLongClickListener {
                        c.invoke(category, position, selected)
                        true
                    }
                }
                stickerClickAction?.let { c ->
                    cvSticker.setOnClickListener {
                        c.invoke(category, adapterPosition, selected)
                        setSelectedItem(position)
                    }
                }
            }
        }
    }

    fun stickerActionClick(action: (category: Category, position: Int, previouslySelected: Int) -> Unit) {
        this.stickerClickAction = action
    }

    fun stickerActionLongClick(action: (category: Category, position: Int, previouslySelected: Int) -> Unit) {
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