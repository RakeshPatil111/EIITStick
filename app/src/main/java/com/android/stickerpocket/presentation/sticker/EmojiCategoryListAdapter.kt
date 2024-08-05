package com.android.stickerpocket.presentation.sticker

import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.stickerpocket.utils.CommunicationBridge
import com.android.stickerpocket.databinding.CvStickerItemBinding
import com.android.stickerpocket.domain.model.Category
import com.android.stickerpocket.utils.ViewExt.removeBorder
import com.android.stickerpocket.utils.ViewExt.setBorder
import com.android.stickerpocket.utils.ViewExt.shakeMe

class EmojiCategoryListAdapter :
    RecyclerView.Adapter<EmojiCategoryListAdapter.StepperViewHolder>(){

    private var selected = -1
    private var stickerClickAction: ((category: Category, position: Int, previouslySelected: Int) -> Unit)? = null
    private var stickerLongClickAction: ((category: Category, position: Int, previouslySelected: Int) -> Unit)? = null
    private val differ = AsyncListDiffer(this, diffUtilEmoji)
    private var hoverItem = -1

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
        if (position == hoverItem) {
            holder.binding.root.shakeMe()
            hoverItem = -1
        }
    }

    fun updateList(list: List<Category>) {
        differ.submitList(list)
        notifyDataSetChanged()
    }

    inner class StepperViewHolder(val binding: CvStickerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(category: Category, position: Int) {
            binding.cvSticker.tag = position
            binding.ivStickerThumbnail.tag = position
            binding.apply {
                binding.cvSticker.tag = position
                ivStickerThumbnail.text = Html.fromHtml(category.html)
                if (category.isHighlighted) {
                    cvSticker.setBorder()
                } else {
                    cvSticker.removeBorder()
                }
                stickerLongClickAction?.let { c ->
                    cvSticker.setOnLongClickListener {
                        c.invoke(category, position, selected)
                        true
                    }
                }
                stickerClickAction?.let { c ->
                    cvSticker.setOnClickListener {

                        if (CommunicationBridge.isSelectionMode.value==true){
                            CommunicationBridge.selectedCatPosition.value=category.position
                        }else {
                            c.invoke(category, adapterPosition, selected)
                            setSelectedItem(position)
                        }
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

    fun clearSelection() {
        differ.currentList.forEach {
            it.isHighlighted = false
        }
        selected = -1
        notifyDataSetChanged()
    }

    fun isCategorySelected() = selected != -1
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

    fun getList() = differ.currentList

    fun hoverCategory(droppedCategoryTag: Int) {
        hoverItem = droppedCategoryTag
        notifyItemChanged(droppedCategoryTag)
    }
}