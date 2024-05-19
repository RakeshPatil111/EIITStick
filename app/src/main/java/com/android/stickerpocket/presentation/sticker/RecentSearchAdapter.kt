package com.android.stickerpocket.presentation.sticker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.stickerpocket.databinding.ItemRecentSearchBinding
import com.android.stickerpocket.domain.model.RecentSearch

class RecentSearchAdapter: RecyclerView.Adapter<RecentSearchAdapter.ViewHolder>() {

    private var recentSearchList: List<RecentSearch> = emptyList()
    private lateinit var onRecentSearchClickListener: OnRecentSearchClickListener
    inner class ViewHolder(val binding: ItemRecentSearchBinding) : RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemRecentSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount() = recentSearchList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvQuery.text = recentSearchList[position].query
        holder.binding.root.setOnClickListener {
            onRecentSearchClickListener.onRecentSearchClick(position)
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
    }
}