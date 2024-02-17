package de.christcoding.budgetfellow.viewmodels

import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.christcoding.budgetfellow.BudgetState
import de.christcoding.budgetfellow.data.BudgetRepository
import de.christcoding.budgetfellow.data.CategoryRepository
import de.christcoding.budgetfellow.data.TransactionRepository
import de.christcoding.budgetfellow.data.models.Budget
import de.christcoding.budgetfellow.data.models.BudgetDetails
import de.christcoding.budgetfellow.data.models.Category
import de.christcoding.budgetfellow.data.models.Transaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
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

    val categoriesFlow: StateFlow<List<Category>> = categoryRepository.getExpenseCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = listOf()
        )
    val incCategoriesFlow: StateFlow<List<Category>> = categoryRepository.getIncomeCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = listOf()
        )

    val expCategoriesFlow: StateFlow<List<Category>> = categoryRepository.getExpenseCategories()
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
    var incCategories by mutableStateOf(listOf<Category>())
    var expCategories by mutableStateOf(listOf<Category>())
    var transactions by mutableStateOf(listOf<Transaction>())

    var createBudgetState: BudgetState by mutableStateOf(BudgetState())
    var editBudgetState: BudgetState by mutableStateOf(BudgetState())
    var id: Long by mutableStateOf(-2L)
    var rows: Int by mutableStateOf(0)
    var deletedRows: Int by mutableStateOf(0)

    init {
        viewModelScope.launch {
            budgetsFlow.collectLatest { budgets = it }
        }
        viewModelScope.launch {
            categoriesFlow.collectLatest { categories = it }
        }
        viewModelScope.launch {
            incCategoriesFlow.collectLatest { incCategories = it }
        }
        viewModelScope.launch {
            expCategoriesFlow.collectLatest { expCategories = it }
        }
        viewModelScope.launch {
            transactionsFlow.collectLatest { transactions = it }
        }
    }

    fun getCurrentBudgetState(budgetId: String): StateFlow<BudgetState> {
        return budgetRepository.getABudgetById(budgetId.toLong()).map { budget ->
            BudgetState(
                budget.amount.toString(),
                category = categories.find { it.id == budget.categoryId }?.name ?: "",
                spent = budget.spent.toString()
            ) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = BudgetState("0.0", category = "", spent = "0.0")
            )
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
        if(categories.isEmpty() || incCategories.isEmpty() ) {
            budgetState = BudgetUiState.Loading
        }
        val budgetDetails: List<BudgetDetails> = getBudgets(budgets)
        val savingsPerMonth = calcSavingPerMonth(budgetDetails)
        val leftInBudgets = calcLeftInBudgets(budgetDetails)
        budgetState = BudgetUiState.Success(budgets = budgetDetails, savingsPerMonth = savingsPerMonth, leftInBudgets = leftInBudgets)
    }

    private fun calcLeftInBudgets(budgetDetails: List<BudgetDetails>): Double {
        val totalBudget = budgetDetails.sumOf { it.amount }
        val totalSpent = budgetDetails.sumOf { it.spent }
        return totalBudget - totalSpent
    }

    private fun calcSavingPerMonth(budgetDetails: List<BudgetDetails>): Double {
        val totalBudget = budgetDetails.sumOf { it.amount }
        var totalIncome = 0.0
        for (category in incCategories) {
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
            val budgetForCategory = getBudgetForCategory(category, allBudgets)
            if(budgetForCategory != null) {
                categoryBudgets.add(budgetForCategory)
            }
        }
        return categoryBudgets
    }

    private fun getBudgetForCategory(category: Category, budgets: List<Budget>): BudgetDetails? {
        for (budget in budgets) {
            if (budget.categoryId == category.id) {
                val spent = getSpentAmountForCategory(category.id)
                return BudgetDetails(
                    id = budget.id,
                    category = category,
                    amount = budget.amount,
                    spent = spent)
            }
        }
        return createBudgetForCategory(category)
    }

    private fun getSpentAmountForCategory(categoryId: Long): Double {
        val transactionsOfCurrentMonth: List<Transaction> = getTransactionsOfCurrentMonth()
        var spent = 0.0
        for (transaction in transactionsOfCurrentMonth) {
            if (transaction.categoryId == categoryId) {
                spent -= transaction.amount
            }
        }
        return spent
    }

    private fun createBudgetForCategory(category: Category): BudgetDetails? {
        val transactionsOfCurrentMonth: List<Transaction> = getTransactionsOfCurrentMonth()
        var newBudgetAmount = 0.0
        for (transaction in transactionsOfCurrentMonth) {
            if (transaction.categoryId == category.id) {
                newBudgetAmount -= transaction.amount
            }
        }
        if(newBudgetAmount == 0.0) {
            return null
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

    fun checkIfEnough() {

    }

    fun saveBudget() {
        var catId = categories.find { it.name == createBudgetState.category }?.id ?: 0L
        if(catId == 0L) {
            viewModelScope.launch(Dispatchers.IO) {
                categoryRepository.addACategory(Category(
                    name = createBudgetState.category,
                    color = 0,
                    expense = true
                ))
            }
            catId = categories.get(categories.size - 1).id + 1
        }
        viewModelScope.launch(Dispatchers.IO) {
            id = budgetRepository.addABudget(Budget(
                categoryId = catId,
                amount = createBudgetState.amount.toDouble(),
                spent = createBudgetState.spent.toDouble()
            ))
            createBudgetState = BudgetState()
        }
    }

    fun updateEditBudgetState(budgetId: String) {
        val budget = budgets.findLast { it.id == budgetId.toLong() }
        if (budget != null) {
            editBudgetState = BudgetState(
                amount = (budget.amount).toString(),
                category = categories.find { category -> category.id == budget.categoryId }?.name ?: "",
                spent = budget.spent.toString()
            )
        }
    }

    fun updateBudget(budgetId: String) {
        val catId = categories.find { it.name == editBudgetState.category }?.id ?: 0L
        viewModelScope.launch(Dispatchers.IO) {
            rows = budgetRepository.updateABudget(Budget(
                id = budgetId.toLong(),
                categoryId = catId,
                amount = editBudgetState.amount.toDouble(),
                spent = editBudgetState.spent.toDouble()
            ))
        }
    }

    fun deleteBudget(budgetId: String) {
        val catId = categories.find { it.name == editBudgetState.category }?.id ?: 0L
        viewModelScope.launch(Dispatchers.IO) {
            deletedRows = budgetRepository.deleteABudget(Budget(
                id = budgetId.toLong(),
                categoryId = catId,
                amount = editBudgetState.amount.toDouble(),
                spent = editBudgetState.spent.toDouble()
            ))
        }
    }
}

data class CreateBudgetUiState(
    val categorie: Category? = null,
    val amount: Double = 0.0,
    val spent: Double = 0.0
)

sealed interface BudgetUiState {
    data class Success(val budgets: List<BudgetDetails>, val savingsPerMonth: Double, val leftInBudgets: Double) : BudgetUiState
    object Loading : BudgetUiState
}