package com.android.stickerpocket.domain.usecase

import com.android.stickerpocket.domain.model.Category
import com.android.stickerpocket.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.collectLatest

class InsertCategoriesUseCase(private val categoriesRepository: CategoryRepository) {
    suspend fun execute(list: List<Category>) {
        categoriesRepository.fetchAll().collectLatest {
            if (it.isEmpty()) {
                categoriesRepository.insertAll(list)
            }
        }
    }
}