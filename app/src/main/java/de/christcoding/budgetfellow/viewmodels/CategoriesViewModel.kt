package de.christcoding.budgetfellow.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
    var catState:  CategoryUiState by mutableStateOf(CategoryUiState.Loading)
        private set
    fun getCategory(categoryId: Long): StateFlow<Category?> {
        return categoryRepository.getACategoryById(categoryId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )
    }

    fun saveCategory(categoryId: Long, categoryName: String) {

    }

    fun updateCategoryState(category: Category) {
        catState = CategoryUiState.Success(category)
    }

    var categories: StateFlow<List<Category>> = categoryRepository.getAllCategory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}
sealed interface CategoryUiState {
    object Loading : CategoryUiState
    data class Success(val category: Category) : CategoryUiState
}