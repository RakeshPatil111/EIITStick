package com.android.stickerpocket.presentation

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.android.stickerpocket.databinding.CvStickerSectionItemBinding

class StickerSectionListAdapter :
    RecyclerView.Adapter<StickerSectionListAdapter.SectionViewHolder>() {

    private val differ = AsyncListDiffer(this, diffUtilSectionSticker)
    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)= SectionViewHolder(
        CvStickerSectionItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun getItemCount() = differ.currentList.size

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    fun updateList(stickerSectionList: ArrayList<StickerSection>, context: Context){
        this.context = context
        differ.submitList(stickerSectionList)
    }

    inner class SectionViewHolder(private val binding: CvStickerSectionItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(stickerSection: StickerSection) {
            binding.apply {
                tvTitle.text = stickerSection.title
                val gifListAdapter = GifListAdapter()
                rvGifs.adapter = gifListAdapter
                //gifListAdapter.updateList(gifs, context, "HORIZONTAL")
            }
        }
    }

    companion object{
        val diffUtilSectionSticker = object: DiffUtil.ItemCallback<StickerSection>(){
            override fun areItemsTheSame(
                oldItem: StickerSection,
                newItem: StickerSection
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: StickerSection,
                newItem: StickerSection
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}