package de.christcoding.budgetfellow.viewmodels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import de.christcoding.budgetfellow.R
import de.christcoding.budgetfellow.TransactionMode
import de.christcoding.budgetfellow.data.CategoryRepository
import de.christcoding.budgetfellow.data.TransactionRepository
import de.christcoding.budgetfellow.utils.Constants

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
}