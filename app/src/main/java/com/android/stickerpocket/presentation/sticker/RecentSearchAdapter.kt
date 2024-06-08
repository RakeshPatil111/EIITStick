package com.android.stickerpocket.presentation.sticker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.stickerpocket.databinding.ItemClearRecentSearchBinding
import com.android.stickerpocket.databinding.ItemRecentSearchBinding
import com.android.stickerpocket.domain.model.RecentSearch

class RecentSearchAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var recentSearchList: List<RecentSearch> = emptyList()
    private lateinit var onRecentSearchClickListener: OnRecentSearchClickListener

    override fun getItemViewType(position: Int): Int =
        if (position == 0 && recentSearchList.isNotEmpty()) TYPE_CLEAR_RECENT else TYPE_RECENT_SEARCH_ITEM

    inner class RecentSearchViewHolder(val binding: ItemRecentSearchBinding) : RecyclerView.ViewHolder(binding.root)

    inner class ClearRecentSearchViewHolder(val binding: ItemClearRecentSearchBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_CLEAR_RECENT) {
            ClearRecentSearchViewHolder(
                ItemClearRecentSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        } else {
            RecentSearchViewHolder(
                ItemRecentSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }
    }

    override fun getItemCount(): Int =
        if (recentSearchList.isEmpty()) 0 else recentSearchList.size + 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ClearRecentSearchViewHolder) {
            holder.binding.btnClearAll.setOnClickListener {
                onRecentSearchClickListener.onClearRecentSearchClick()
            }
        } else if (holder is RecentSearchViewHolder) {
            val recentSearch = recentSearchList[position - if (recentSearchList.isNotEmpty()) 1 else 0]
            holder.binding.tvQuery.text = recentSearch.query
            holder.binding.tvQuery.setOnClickListener {
                onRecentSearchClickListener.onRecentSearchClick(position - if (recentSearchList.isNotEmpty()) 1 else 0)
            }
            holder.binding.ivRemove.setOnClickListener {
                onRecentSearchClickListener.onRecentSearchRemove(position - if (recentSearchList.isNotEmpty()) 1 else 0)
            }
        }
    }

    fun updateList(list: List<RecentSearch>) {
        recentSearchList = list
        notifyDataSetChanged()
    }

    fun setOnRecentSearchClickListener(listener: OnRecentSearchClickListener) {
        onRecentSearchClickListener = listener
    }

    interface OnRecentSearchClickListener {
        fun onRecentSearchClick(position: Int)
        fun onRecentSearchRemove(position: Int)
        fun onClearRecentSearchClick()
    }

    companion object {
        private const val TYPE_CLEAR_RECENT = 0
        private const val TYPE_RECENT_SEARCH_ITEM = 1
    }
}