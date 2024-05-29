package com.android.stickerpocket.domain.repository

import com.android.stickerpocket.domain.dao.CategoryDAO
import com.android.stickerpocket.domain.model.Category
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val dao: CategoryDAO) {
    suspend fun fetchAll(): Flow<List<Category>> = dao.fetchAll()
    suspend fun insertAll(list: List<Category>) = dao.insertAll(list)
}