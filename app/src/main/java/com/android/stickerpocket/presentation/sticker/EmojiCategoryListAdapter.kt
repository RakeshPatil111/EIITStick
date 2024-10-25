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
import com.android.stickerpocket.dtos.CommonAdapterDTO
import com.android.stickerpocket.utils.DragListener
import com.android.stickerpocket.utils.OnStickerDropOnCategoryListener
import com.android.stickerpocket.utils.ViewExt.removeBorder
import com.android.stickerpocket.utils.ViewExt.setBlueBorder
import com.android.stickerpocket.utils.ViewExt.setBorder
import com.android.stickerpocket.utils.ViewExt.shakeMe
import com.android.stickerpocket.utils.ViewExt.zoomIn
import com.android.stickerpocket.utils.toCategory

class EmojiCategoryListAdapter :
    RecyclerView.Adapter<EmojiCategoryListAdapter.StepperViewHolder>(){

    private var selected = -1
    private var stickerClickAction: ((category: Category, position: Int, previouslySelected: Int) -> Unit)? = null
    private var stickerLongClickAction: ((category: Category, position: Int, previouslySelected: Int) -> Unit)? = null
    private val differ = AsyncListDiffer(this, diffUtilEmoji)
    private var hoverItem = -1
    private var dropListener: OnStickerDropOnCategory? = null

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
        val category = differ.currentList[position].toCategory()
        holder.bind(category, position)
        if (position == hoverItem) {
            holder.binding.root.zoomIn()
            hoverItem = -1
        }
    }

    fun updateList(list: List<CommonAdapterDTO>) {
        differ.submitList(list)
        notifyDataSetChanged()
    }

    inner class StepperViewHolder(val binding: CvStickerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(category: Category, position: Int) {
            val dragListener = DragListener()
            binding.root.setOnDragListener(dragListener)
            binding.cvCategoryItem.tag = position
            binding.ivCategoryThumbnail.tag = position
            binding.root.tag = position
            binding.apply {
                binding.cvCategoryItem.tag = position
                ivCategoryThumbnail.text = Html.fromHtml(category.html)
                if (category.isHighlighted) {
                    cvCategoryItem.setBorder()
                } else {
                    cvCategoryItem.removeBorder()
                }
                stickerLongClickAction?.let { c ->
                    cvCategoryItem.setOnLongClickListener {
                        binding.root.shakeMe()
                        c.invoke(category, position, selected)
                        true
                    }
                }
                stickerClickAction?.let { c ->
                    cvCategoryItem.setOnClickListener {

                        if (CommunicationBridge.isSelectionMode.value==true){
                            CommunicationBridge.selectedCatPosition.value=category.position
                            cvCategoryItem.setBlueBorder()
                        }else {
                            c.invoke(category, adapterPosition, selected)
                            setSelectedItem(position)
                            cvCategoryItem.removeBorder()
                        }
                    }
                }
            }
            dragListener.setDropListener(object : OnStickerDropOnCategoryListener {
                override fun onDrop(
                    sourceStickerPosition: Int,
                    targetCategoryPosition: Int
                ) {
                    binding.root.shakeMe()
                    dropListener?.onDrop(sourceStickerPosition, targetCategoryPosition)
                }

            })
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
        val diffUtilEmoji = object : DiffUtil.ItemCallback<CommonAdapterDTO>() {
            override fun areItemsTheSame(oldItem: CommonAdapterDTO, newItem: CommonAdapterDTO): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: CommonAdapterDTO, newItem: CommonAdapterDTO): Boolean {
                return oldItem == newItem
            }
        }
    }

    fun setDropListener(listener: OnStickerDropOnCategory) {
        this.dropListener = listener
    }

    interface OnStickerDropOnCategory {
        fun onDrop(stickerPosition: Int, categoryPosition: Int)
    }
}