package de.christcoding.budgetfellow.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.christcoding.budgetfellow.data.BudgetRepository
import de.christcoding.budgetfellow.data.CategoryRepository
import de.christcoding.budgetfellow.data.TransactionRepository
import de.christcoding.budgetfellow.data.models.Budget
import de.christcoding.budgetfellow.data.models.BudgetDetails
import de.christcoding.budgetfellow.data.models.Category
import de.christcoding.budgetfellow.data.models.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.chrono.ChronoLocalDate

class BudgetsViewModel(
    private val budgetRepository: BudgetRepository,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
): ViewModel() {

    val budgetsFlow: StateFlow<List<Budget>> = budgetRepository.getAllBudgets()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = listOf()
        )

    val categoriesFlow: StateFlow<List<Category>> = categoryRepository.getAllCategory()
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

    var budgets by mutableStateOf(listOf<Budget>())
    var categories by mutableStateOf(listOf<Category>())
    var transactions by mutableStateOf(listOf<Transaction>())

    init {
        viewModelScope.launch {
            budgetsFlow.collectLatest { budgets = it }
            categoriesFlow.collectLatest{ categories = it }
        }
        viewModelScope.launch {
            transactionsFlow.collectLatest { transactions = it }
        }
    }

    var budgetState: StateFlow<BudgetUiState> = flow<BudgetUiState> {
        emitBudgetState()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BudgetUiState()
    )

    private fun emitBudgetState() {

    }

    private fun getBudgetsState(): BudgetUiState {
        return mapToBudgetUiState(budgets)
    }

    private suspend fun mapToBudgetUiState(budgets: List<Budget>): BudgetUiState {
        val budgetDetails = getBudgets(budgets)
        return BudgetUiState(budgets = budgetDetails)
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    suspend fun getBudgets(allBudgets: List<Budget>): List<BudgetDetails> {
        val categoryBudgets: MutableList<BudgetDetails> = mutableListOf()
        val categories: List<Category> = categoryRepository.getAllCategory().firstOrNull() ?: listOf()
        for (category in categories) {
            categoryBudgets.add(getBudgetForCategory(category, allBudgets))
        }
        return categoryBudgets
    }

    private suspend fun getBudgetForCategory(category: Category, budgets: List<Budget>): BudgetDetails {
        for (budget in budgets) {
            if (budget.categoryId == category.id) {
                return BudgetDetails(
                    id = budget.id,
                    category = category,
                    amount = budget.amount,
                    spent = budget.spent)
            }
        }
        return createBudgetForCategory(category)
    }

    private suspend fun createBudgetForCategory(category: Category): BudgetDetails {
        val transactionsOfCurrentMonth: List<Transaction> = getTransactionsOfCurrentMonth()
        var newBudgetAmount = 0.0
        for (transaction in transactionsOfCurrentMonth) {
            if (transaction.categoryId == category.id) {
                newBudgetAmount += transaction.amount
            }
        }
        val newBudget = BudgetDetails(category = category, amount = newBudgetAmount, spent = newBudgetAmount)
        budgetRepository.addABudget(Budget(categoryId = category.id, amount = newBudgetAmount, spent = newBudgetAmount))
        return newBudget
    }

    private suspend fun getTransactionsOfCurrentMonth(): List<Transaction> {
        var transactions: MutableList<Transaction> = mutableListOf()
        transactionRepository.getAllTransactions().collect {
            transactions = it.toMutableList()
        }
        val localDate = LocalDate.now()
        val transactionsOfCurrentMonth: MutableList<Transaction> = mutableListOf()
        for (transaction in transactions) {
            if (transaction.recurring) {
                val recurringDate = transaction.date
                if (recurringDate.monthValue == localDate.monthValue) {
                    transactionsOfCurrentMonth.add(transaction)
                    transactions.remove(transaction)
                }
                while (recurringDate.isBefore(getLastDayOfCurrentMonth(localDate))) {
                    when (transaction.recurringIntervalUnit) {
                        "Day" -> recurringDate.plusDays(transaction.recurringInterval.toLong())
                        "Week" -> recurringDate.plusWeeks(transaction.recurringInterval.toLong())
                        "Month" -> recurringDate.plusMonths(transaction.recurringInterval.toLong())
                        "Year" -> recurringDate.plusYears(transaction.recurringInterval.toLong())
                    }
                    if (recurringDate.monthValue == localDate.monthValue) {
                        transactionsOfCurrentMonth.add(transaction)
                    }
                }
            }
        }
        for (transaction in transactions) {
            if (transaction.date.monthValue == localDate.monthValue) {
                transactionsOfCurrentMonth.add(transaction)
            }
        }
        return transactionsOfCurrentMonth
    }

    private fun getLastDayOfCurrentMonth(localDate: LocalDate): ChronoLocalDate? {
        return localDate.withDayOfMonth(localDate.lengthOfMonth())
    }
}

data class BudgetUiState(val budgets: List<BudgetDetails> = listOf(), val savingsPerMonth: Double = 0.0)