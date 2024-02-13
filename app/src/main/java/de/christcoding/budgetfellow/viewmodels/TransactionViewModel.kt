package de.christcoding.budgetfellow.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.christcoding.budgetfellow.data.CategoryRepository
import de.christcoding.budgetfellow.data.TransactionRepository
import de.christcoding.budgetfellow.data.models.Category
import de.christcoding.budgetfellow.data.models.Transaction
import de.christcoding.budgetfellow.data.models.TransactionDetails
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TransactionViewModel(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
): ViewModel() {

    val categoriesFlow: StateFlow<List<Category>> = categoryRepository.getExpenseCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = listOf()
        )

    val transactionsFlow: StateFlow<List<Transaction>> = transactionRepository.getAllTransactions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = listOf()
        )
    var categories by mutableStateOf(listOf<Category>())
    var transactions by mutableStateOf(listOf<Transaction>())

    init {
        viewModelScope.launch {
            categoriesFlow.collectLatest { categories = it }
        }
        viewModelScope.launch {
            transactionsFlow.collectLatest { transactions = it }
        }
    }

    var transactionsState: TransactionsUiState by mutableStateOf(TransactionsUiState.Loading)
        private set

    fun updateTransactionState() {
        if(categories.isEmpty()) {
            transactionsState = TransactionsUiState.Loading
        }
        transactionsState = TransactionsUiState.Success(transactions.map { transaction ->
            val category = categories.find { it.id == transaction.categoryId }
            TransactionDetails(
                id = transaction.id,
                name = transaction.name,
                description = transaction.description,
                category = category ?: Category(name = "Uncategorized", color = 0, expense = true),
                amount = transaction.amount,
                date = transaction.date,
                recurring = transaction.recurring,
                recurringIntervalUnit = transaction.recurringIntervalUnit,
                recurringInterval = transaction.recurringInterval
            )
        })
    }
}

sealed interface TransactionsUiState {
    data class Success(val transactions: List<TransactionDetails>) : TransactionsUiState
    object Loading : TransactionsUiState
}