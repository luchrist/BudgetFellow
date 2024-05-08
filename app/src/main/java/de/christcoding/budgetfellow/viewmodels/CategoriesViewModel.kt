package de.christcoding.budgetfellow.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.christcoding.budgetfellow.data.CategoryRepository
import de.christcoding.budgetfellow.data.models.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class CategoriesViewModel(
    private val categoryRepository: CategoryRepository
): ViewModel() {
    fun getCategory(categoryId: Long): Flow<Category> {
        return categoryRepository.getACategoryById(categoryId)
    }

    fun saveCategory(categoryId: Long, categoryName: String) {

    }

    var categories: StateFlow<List<Category>> = categoryRepository.getAllCategory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}