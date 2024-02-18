package de.christcoding.budgetfellow.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.viewModelScope
import de.christcoding.budgetfellow.TransactionMode
import de.christcoding.budgetfellow.data.CategoryRepository
import de.christcoding.budgetfellow.data.TransactionRepository
import de.christcoding.budgetfellow.data.models.Category
import kotlinx.coroutines.launch

class IntroViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
): AddOrEditTransactionViewModel(transactionRepository = transactionRepository, categoryRepository = categoryRepository) {

    var skip by mutableStateOf("Skip")
    var firstIncome by mutableStateOf(true)

    fun introFinished() {
       //use DataStore<Preferences>
    }

    override fun handleSubmit(mode: TransactionMode) {
        super.handleSubmit(mode)
        firstIncome = false
        skip = "Next"
    }


    val categoryList: MutableList<Category> =
        mutableListOf(
            Category(name = "Salary", color = Color(0f, 0.68f, 0f, 0.5f).toArgb(), expense = false),
            Category(name = "Assets", color = Color(0f, 0.4f, 0.98f, 0.5f).toArgb(), expense = false),
            Category( name = "House", color = Color(0f, 0.016f, 0.98f, 0.5f).toArgb(), expense = true),
            Category( name = "Food", color = Color(0.6f, 0.4f, 0.0f, 0.4f).toArgb(), expense = true),
            Category( name = "Clothing", color = Color(0f, 0.79f, 0.98f, 0.36f).toArgb(), expense = true),
            Category( name = "Transport", color = Color(1f, 0f, 0f, 0.4f).toArgb(), expense = true),
            Category( name = "Self Care", color = Color(1f, 0.1f, 0.7f, 0.2f).toArgb(), expense = true),
            Category( name = "Subscriptions", color = Color(0.5f, 0.5f, 0.5f, 0.36f).toArgb(), expense = true),
            Category( name = "Luxury", color = Color(0.98f, 0.8f, 0.0f, 0.36f).toArgb(), expense = true),
            Category( name = "Vacation", color = Color(0.1f, 0.8f, 0.7f, 0.4f).toArgb(), expense = true)
        )
    fun saveInitialCategoriesInDB() {
        viewModelScope.launch {
            for (category in categoryList)
                categoryRepository.addACategory(category)
        }
    }
}