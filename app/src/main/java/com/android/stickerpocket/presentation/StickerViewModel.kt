package com.android.stickerpocket.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.stickerpocket.StickerApplication
import com.android.stickerpocket.domain.model.RecentSearch
import com.android.stickerpocket.domain.usecase.CreateOrUpdatedRecentSearchUseCase
import com.android.stickerpocket.domain.usecase.DeleteRecentSearchUseCase
import com.android.stickerpocket.domain.usecase.GetRecentSearchUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class StickerViewModel: ViewModel() {

    sealed class Result {
        data class RecentSearches(val recentSearches: List<RecentSearch>): Result()
    }

    private val _liveData = MutableLiveData<Result>()
    val liveData: MutableLiveData<Result> = _liveData

    private var recentSearchUseCase: GetRecentSearchUseCase
    private var createOrUpdatedRecentSearchUseCase: CreateOrUpdatedRecentSearchUseCase
    private var deleteRecentSearchUseCase: DeleteRecentSearchUseCase


    private var recentSearchs: MutableList<RecentSearch> = mutableListOf()
    init {
        deleteRecentSearchUseCase = DeleteRecentSearchUseCase(StickerApplication.instance.recentSearchRepository)
        recentSearchUseCase = GetRecentSearchUseCase(StickerApplication.instance.recentSearchRepository)
        createOrUpdatedRecentSearchUseCase = CreateOrUpdatedRecentSearchUseCase(StickerApplication.instance.recentSearchRepository)
        fetchRecentSearches()
    }

    private fun fetchRecentSearches() {
        CoroutineScope(Dispatchers.Default).launch {
            when (val result = recentSearchUseCase.execute()) {
                is GetRecentSearchUseCase.Result.Success -> {
                    recentSearchs = result.items.toMutableList()
                }
                is GetRecentSearchUseCase.Result.Failure -> {

                }
            }
        }
    }
    fun getRecentSearches() = recentSearchs
    fun updateRecentSearch(position: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val recentSearch = recentSearchs[position].apply {
                this.time = Date().time
            }
            createOrUpdatedRecentSearchUseCase.execute(recentSearch)
        }
    }

    fun saveRecentSearch(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val recentSearch = RecentSearch(query, Date().time)
            createOrUpdatedRecentSearchUseCase.execute(recentSearch)
            fetchRecentSearches()
        }
    }

    fun removeRecentSearch(position: Int) {
        val recentSearchToDelete = recentSearchs[position]
        CoroutineScope(Dispatchers.IO).launch {
            recentSearchs.removeAt(position)
            deleteRecentSearchUseCase.execute(DeleteRecentSearchUseCase.Params(recentSearch = recentSearchToDelete))
            fetchRecentSearches()
        }
    }
}