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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
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
        }
        viewModelScope.launch {
            categoriesFlow.collectLatest { categories = it }
        }
        viewModelScope.launch {
            transactionsFlow.collectLatest { transactions = it }
        }
    }

    var budgetState: BudgetUiState by mutableStateOf(BudgetUiState.Loading)
        private set

            /*StateFlow<BudgetUiState> = flow {
        while (true) {
            emit(getBudgetState())
            delay(1_000)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = BudgetUiState()
    )*/

    fun updateBudgetState() {
        if(categories.isEmpty() || transactions.isEmpty()) {
            budgetState = BudgetUiState.Loading
        }
        val budgetDetails: List<BudgetDetails> = getBudgets(budgets)
        val savingsPerMonth = calcSavingPerMonth(budgetDetails)
        budgetState = BudgetUiState.Success(budgets = budgetDetails, savingsPerMonth = savingsPerMonth)
    }

    private fun calcSavingPerMonth(budgetDetails: List<BudgetDetails>): Double {
        val incomeCategories = categories.filter { !it.expense }.toList()
        val totalBudget = budgetDetails.sumOf { it.amount }
        var totalIncome = 0.0
        for (category in incomeCategories) {
            for (transaction in transactions) {
                if (transaction.categoryId == category.id && transaction.date.monthValue == LocalDate.now().monthValue) {
                    totalIncome += if(transaction.recurring) {
                        getTransactionValuePerMonth(transaction)
                    } else {
                        transaction.amount
                    }
                }
            }
        }
        return totalIncome - totalBudget
    }
    private fun getTransactionValuePerMonth(transaction: Transaction): Double {
        val localDate = LocalDate.now()
        var recurringDate = transaction.date
        var transactionValue = 0.0
        while (recurringDate.isBefore(getLastDayOfCurrentMonth(localDate))) {
            if (recurringDate.monthValue == localDate.monthValue) {
                transactionValue += transaction.amount
            }
            when (transaction.recurringIntervalUnit) {
                "Day" -> recurringDate = recurringDate.plusDays(transaction.recurringInterval.toLong())
                "Week" -> recurringDate = recurringDate.plusWeeks(transaction.recurringInterval.toLong())
                "Month" -> recurringDate = recurringDate.plusMonths(transaction.recurringInterval.toLong())
                "Year" -> recurringDate = recurringDate.plusYears(transaction.recurringInterval.toLong())
            }
        }
        return transactionValue
    }

    fun getBudgets(allBudgets: List<Budget>): List<BudgetDetails> {
        val categoryBudgets: MutableList<BudgetDetails> = mutableListOf()
        for (category in categories) {
            categoryBudgets.add(getBudgetForCategory(category, allBudgets))
        }
        return categoryBudgets
    }

    private fun getBudgetForCategory(category: Category, budgets: List<Budget>): BudgetDetails {
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

    private fun createBudgetForCategory(category: Category): BudgetDetails {
        val transactionsOfCurrentMonth: List<Transaction> = getTransactionsOfCurrentMonth()
        var newBudgetAmount = 0.0
        for (transaction in transactionsOfCurrentMonth) {
            if (transaction.categoryId == category.id) {
                newBudgetAmount += transaction.amount
            }
        }
        val newBudget = BudgetDetails(category = category, amount = newBudgetAmount, spent = newBudgetAmount)
        viewModelScope.launch {
            budgetRepository.addABudget(Budget(categoryId = category.id, amount = newBudgetAmount, spent = newBudgetAmount))
        }
        return newBudget
    }

    private fun getTransactionsOfCurrentMonth(): List<Transaction> {
        val localDate = LocalDate.now()
        val transactionsOfCurrentMonth: MutableList<Transaction> = mutableListOf()
        for (transaction in transactions) {
            if (transaction.recurring) {
                var recurringDate = transaction.date
                if (recurringDate.monthValue == localDate.monthValue) {
                    transactionsOfCurrentMonth.add(transaction)
                }
                while (recurringDate.isBefore(getLastDayOfCurrentMonth(localDate))) {
                    when (transaction.recurringIntervalUnit) {
                        "Day" -> recurringDate = recurringDate.plusDays(transaction.recurringInterval.toLong())
                        "Week" -> recurringDate = recurringDate.plusWeeks(transaction.recurringInterval.toLong())
                        "Month" -> {
                            recurringDate = recurringDate.plusMonths(transaction.recurringInterval.toLong())
                        }
                        "Year" -> recurringDate = recurringDate.plusYears(transaction.recurringInterval.toLong())
                    }
                    if (recurringDate.monthValue == localDate.monthValue) {
                        transactionsOfCurrentMonth.add(transaction)
                    }
                }
            } else if (transaction.date.monthValue == localDate.monthValue) {
                transactionsOfCurrentMonth.add(transaction)
            }
        }
        return transactionsOfCurrentMonth
    }

    private fun getLastDayOfCurrentMonth(localDate: LocalDate): ChronoLocalDate? {
        return localDate.withDayOfMonth(localDate.lengthOfMonth())
    }
}

sealed interface BudgetUiState {
    data class Success(val budgets: List<BudgetDetails>, val savingsPerMonth: Double) : BudgetUiState
    object Loading : BudgetUiState
}