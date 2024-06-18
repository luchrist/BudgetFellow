package de.christcoding.budgetfellow.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import de.christcoding.budgetfellow.TransactionMode
import de.christcoding.budgetfellow.TransactionState
import de.christcoding.budgetfellow.data.CategoryRepository
import de.christcoding.budgetfellow.data.TransactionRepository
import de.christcoding.budgetfellow.data.datastore.StoreAppSettings
import de.christcoding.budgetfellow.data.models.Category
import de.christcoding.budgetfellow.data.models.Transaction
import de.christcoding.budgetfellow.data.models.TransactionDetails
import de.christcoding.budgetfellow.data.models.copyWithoutId
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Month

class TransactionViewModel(
    private val transactionRepository: TransactionRepository,
    categoryRepository: CategoryRepository,
): ApplicationViewModel() {

    var cycleStart by mutableStateOf(1)
    var smartCycle by mutableStateOf(true)
    var cycleState by mutableStateOf(false)

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
    var categories by mutableStateOf(listOf<Category>())
    var transactions by mutableStateOf(listOf<Transaction>())
    var transactionMode: TransactionMode? by mutableStateOf(null)
    var currentMonth by mutableStateOf(convertToCamelCase(LocalDate.now().month))
    var allTransactionsTillEndOfCycle by mutableStateOf(listOf<Transaction>())

    private fun convertToCamelCase(month: Month?): String {
        when(month)
        {
            Month.JANUARY -> return "January"
            Month.FEBRUARY -> return "February"
            Month.MARCH -> return "March"
            Month.APRIL -> return "April"
            Month.MAY -> return "May"
            Month.JUNE -> return "June"
            Month.JULY -> return "July"
            Month.AUGUST -> return "August"
            Month.SEPTEMBER -> return "September"
            Month.OCTOBER -> return "October"
            Month.NOVEMBER -> return "November"
            Month.DECEMBER -> return "December"
            else -> {
                return "January"}
        }
    }

    private fun getAllTransactionTillDay(day: LocalDate): List<Transaction> {
        val allTransactions: MutableList<Transaction> = mutableListOf()
        for(transaction in transactions) {
            if (transaction.date.isBefore(day.plusDays(1))) {
                allTransactions.add(transaction)
            }
            if(transaction.recurring && !transaction.recurringDeleted) {
                var date = transaction.date
                while (date.isBefore(day)) {
                    when(transaction.recurringIntervalUnit) {
                        "Day" -> date = date.plusDays(transaction.recurringInterval.toLong())
                        "Week" -> date = date.plusWeeks(transaction.recurringInterval.toLong())
                        "Month" -> date = date.plusMonths(transaction.recurringInterval.toLong())
                        "Year" -> date = date.plusYears(transaction.recurringInterval.toLong())
                    }
                    if (date.isBefore(day.plusDays(1))
                        && transactions.filter { it.recurringId == transaction.recurringId }.none { it.date == date }
                        && transactions.filter { it.recurringId == transaction.recurringId }.none { it.date.isAfter(date)  }) {
                        allTransactions.add(transaction.copyWithoutId(date = date))
                        viewModelScope.launch {
                            transactionRepository.addATransaction(transaction.copyWithoutId(date = date))
                        }
                    } else {
                        break
                    }
                }
            }
        }
        return allTransactions
    }

    var editTransactionState by mutableStateOf(TransactionDetails())

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
        } else {
            allTransactionsTillEndOfCycle = getAllTransActionTillEndOfCycle()
            val allTransactionsTillToday = allTransactionsTillEndOfCycle.filter { it.date.isBefore(LocalDate.now().plusDays(1)) }
            transactionsState = TransactionsUiState.Success(
                allTransactionsTillToday.map { transaction ->
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
                    recurringInterval = transaction.recurringInterval,
                    recurringId = transaction.recurringId,
                    recurringDeleted = transaction.recurringDeleted
                )
            },
                getAllFutureTransactionsThisCycle().map { transaction ->
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
                        recurringInterval = transaction.recurringInterval,
                        recurringId = transaction.recurringId,
                        recurringDeleted = transaction.recurringDeleted
                    )
                },
                calcMonthlyBalance(allTransactionsTillToday), calcMonthlyIncome(allTransactionsTillToday), calcFutureMonthlyBalance())
        }
    }

    private fun calcFutureMonthlyBalance(): Double {
        return if(LocalDate.now().dayOfMonth < cycleStart) {
            allTransactionsTillEndOfCycle
                .filter { it.date.isAfter(LocalDate.now().withDayOfMonth(cycleStart).minusDays(1).minusMonths(1)) }
                .sumOf { it.amount }
        } else {
            allTransactionsTillEndOfCycle
                .filter { it.date.isAfter(LocalDate.now().withDayOfMonth(cycleStart).minusDays(1)) }
                .sumOf { it.amount }
        }
    }

    private fun getAllFutureTransactionsThisCycle(): List<Transaction> {
        return allTransactionsTillEndOfCycle
            .filter { it.date.isAfter(LocalDate.now()) }
    }

    private fun getAllTransActionTillEndOfCycle(): List<Transaction>{
        val now = LocalDate.now()
        updateCycleStart()
        return if(cycleStart == 1) {
            getAllTransactionTillDay(now.withDayOfMonth(now.lengthOfMonth()))
        }
        else if (now.dayOfMonth < cycleStart) {
            getAllTransactionTillDay(now.withDayOfMonth(cycleStart-1))
        } else {
            getAllTransactionTillDay(now.plusMonths(1).withDayOfMonth(cycleStart-1))
        }
    }

    private fun updateCycleStart() {
        if(smartCycle && transactions.any { it.recurring }) {
            cycleStart = transactions.filter { it.recurring }.maxBy { it.amount }.date.dayOfMonth
        }
    }

    private fun calcMonthlyIncome(allTransactions: List<Transaction>): Double {
        updateCycleStart()
        if(LocalDate.now().dayOfMonth < cycleStart) {
            return allTransactions
                .filter { it.date.isAfter(LocalDate.now().withDayOfMonth(cycleStart).minusDays(1).minusMonths(1)) }
                .filter { it.amount > 0 }
                .sumOf { it.amount }
        }
        return allTransactions
            .filter { it.date.isAfter(LocalDate.now().withDayOfMonth(cycleStart).minusDays(1)) }
            .filter { it.amount > 0 }
            .sumOf { it.amount }
    }

    private fun calcMonthlyBalance(allTransactions: List<Transaction>): Double {
        if(LocalDate.now().dayOfMonth < cycleStart) {
            return allTransactions
                .filter { it.date.isAfter(LocalDate.now().withDayOfMonth(cycleStart).minusDays(1).minusMonths(1)) }
                .filter { it.date.isBefore(LocalDate.now().plusDays(1)) }
                .sumOf { it.amount }
        }
        return allTransactions
            .filter { it.date.isAfter(LocalDate.now().withDayOfMonth(cycleStart).minusDays(1)) }
            .filter { it.date.isBefore(LocalDate.now().plusDays(1)) }
            .sumOf { it.amount }
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.deleteATransaction(transaction)
        }
    }

    fun updateEditTransactionState(transactionId: String) {
        val transaction = transactions.findLast { it.id == transactionId.toLong() }
        if (transaction != null) {
            editTransactionState = TransactionDetails(
                id = transaction.id,
                name = transaction.name,
                description = transaction.description,
                category = categories.find { it.id == transaction.categoryId } ?: Category(name = "Uncategorized", color = 0, expense = true),
                amount = transaction.amount,
                date = transaction.date,
                recurring = transaction.recurring,
                recurringIntervalUnit = transaction.recurringIntervalUnit,
                recurringInterval = transaction.recurringInterval,
                recurringId = transaction.recurringId,
                recurringDeleted = transaction.recurringDeleted
            )
            transactionMode = if(editTransactionState.category.expense) TransactionMode.ExpenseEdit else TransactionMode.IncomeEdit
        }
    }

    private fun mapToTransactionState(transaction: Transaction): TransactionState {
        return TransactionState(
            amount = transaction.amount.toString(),
            period = transaction.recurringInterval.toString(),
            category = transaction.categoryId.toString()
        )
    }

    fun getCycleData(dataStore: StoreAppSettings) {
        viewModelScope.launch {
            dataStore.getCycleStart.collectLatest { cycleStart = it }
            dataStore.getSmartCycle.collectLatest { smartCycle = it }
            cycleState = true
        }
    }
}

sealed interface TransactionsUiState {
    data class Success(val transactions: List<TransactionDetails>,
        val futureTransactions: List<TransactionDetails>,
        val monthlyBalance: Double,
        val monthlyIncome: Double,
        val futureMonthlyBalance: Double
        ) : TransactionsUiState
    object Loading : TransactionsUiState
}