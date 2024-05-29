package com.android.stickerpocket.domain.usecase

import com.android.stickerpocket.domain.model.Category
import com.android.stickerpocket.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.collectLatest

class InsertOrReplaceCategoriesUseCase(private val categoriesRepository: CategoryRepository) {
    suspend fun execute(list: List<Category>) {
       categoriesRepository.insertAll(list)
    }
}