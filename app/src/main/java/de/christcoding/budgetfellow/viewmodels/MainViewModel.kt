package de.christcoding.budgetfellow.viewmodels

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.material3.CardColors
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.christcoding.budgetfellow.AddTransactionEvent
import de.christcoding.budgetfellow.R
import de.christcoding.budgetfellow.TransactionMode
import de.christcoding.budgetfellow.TransactionState
import de.christcoding.budgetfellow.data.BudgetRepository
import de.christcoding.budgetfellow.data.Graph
import de.christcoding.budgetfellow.data.TransactionRepository
import de.christcoding.budgetfellow.data.models.Budget
import de.christcoding.budgetfellow.data.models.Transaction
import de.christcoding.budgetfellow.domain.ValidationEvent
import de.christcoding.budgetfellow.domain.use_case.ValidateAmount
import de.christcoding.budgetfellow.domain.use_case.ValidateCategory
import de.christcoding.budgetfellow.domain.use_case.ValidatePeriod
import de.christcoding.budgetfellow.domain.use_case.ValidationResult
import de.christcoding.budgetfellow.navigation.Screen
import de.christcoding.budgetfellow.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.chrono.ChronoLocalDate
import kotlin.random.Random

class MainViewModel(
    private val validateAmount: ValidateAmount = ValidateAmount(),
    private val validatePeriod: ValidatePeriod = ValidatePeriod(),
    private val validateCategory: ValidateCategory = ValidateCategory(),
    private val transactionRepository: TransactionRepository = Graph.transactionRepository,
    private val budgetRepository: BudgetRepository = Graph.budgetRepository,
    private val context: Context
) : ViewModel() {

    private val validationEventChannel = Channel<ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()
    val sp: SharedPreferences = context.getSharedPreferences("budget_fellow", 0)
    var firstIncome by mutableStateOf(sp.getBoolean("first_income", true))
    var state by mutableStateOf(TransactionState())
    var datePicked by mutableStateOf(context.getString(R.string.date))
    var recurring by mutableStateOf(false)
    var recurringPeriod by mutableStateOf("1")
    var periodUnit by mutableStateOf(context.getString(R.string.day))
    var stepDesc by mutableStateOf(context.getString(R.string.let_s_start_by_adding_your_first_income))
    var skip by mutableStateOf(context.getString(R.string.skip))
    var transactionName by mutableStateOf("")
    var transactionDescription by mutableStateOf("")
    var amount by mutableStateOf("")
    var id by mutableStateOf(-2L)
    var rowsUpdated by mutableStateOf(-1)

    var currency by mutableStateOf("€")
    var savingsPerMonth by mutableStateOf(0.0)

    var categories: MutableList<String> = (sp.getStringSet(Constants.KEY_CATEGORIES, listOf("Salary","House", "Food", "Clothing", "Transport", "Self Care","Subscriptions", "Luxury", "Vacation").toSet())
        ?.toMutableList() ?: mutableListOf())
    var selectedCategory by mutableStateOf("")

    lateinit var budgets: List<Budget>

    init {
        viewModelScope.launch {
            budgets = getBudgets()
        }
    }

    suspend fun getBudgets(): List<Budget> {
        val categoryBudgets: MutableList<Budget> = mutableListOf()
            var allBudgets: List<Budget> = listOf()
            budgetRepository.getAllBudgets().collect{
                allBudgets = it
            }
            for (category in categories) {
                categoryBudgets.add(getBudgetForCategory(category, allBudgets))
            }
        return categoryBudgets
    }

    private suspend fun getBudgetForCategory(category: String, budgets: List<Budget>): Budget  {
        for (budget in budgets) {
            if (budget.category == category) {
                return budget
            }
        }
        return createBudgetForCategory(category)
    }

    private suspend fun createBudgetForCategory(category: String): Budget {
        val transactionsOfCurrentMonth: List<Transaction> = getTransactionsOfCurrentMonth()
        var newBudget = 0.0
        for (transaction in transactionsOfCurrentMonth) {
            if (transaction.category == category) {
                newBudget += transaction.amount
            }
        }
        return Budget(category = category, amount = newBudget, spent = newBudget)
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
                val recurringDate = LocalDate.parse(transaction.date)
                if (recurringDate.monthValue == localDate.monthValue) {
                    transactionsOfCurrentMonth.add(transaction)
                    transactions.remove(transaction)
                }
                while (recurringDate.isBefore(getLastDayOfCurrentMonth(localDate))) {
                    when (transaction.recurringIntervalUnit) {
                        context.getString(R.string.day) -> recurringDate.plusDays(transaction.recurringInterval.toLong())
                        context.getString(R.string.week) -> recurringDate.plusWeeks(transaction.recurringInterval.toLong())
                        context.getString(R.string.month) -> recurringDate.plusMonths(transaction.recurringInterval.toLong())
                        context.getString(R.string.year) -> recurringDate.plusYears(transaction.recurringInterval.toLong())
                    }
                    if (recurringDate.monthValue == localDate.monthValue) {
                        transactionsOfCurrentMonth.add(transaction)
                    }
                }
            }
        }
        for (transaction in transactions) {
            if (LocalDate.parse(transaction.date).monthValue == localDate.monthValue) {
                transactionsOfCurrentMonth.add(transaction)
            }
        }
        return transactionsOfCurrentMonth
    }

    private fun getLastDayOfCurrentMonth(localDate: LocalDate): ChronoLocalDate? {
        return localDate.withDayOfMonth(localDate.lengthOfMonth())
    }

    fun onEvent(event: AddTransactionEvent) {
        when (event) {
            is AddTransactionEvent.OnAmountChanged -> {
                state = state.copy(amount = event.amount)
            }
            is AddTransactionEvent.OnPeriodChanged -> {
                state = state.copy(period = event.period)
            }
            is AddTransactionEvent.OnCategoryChanged -> {
                state = state.copy(category = event.category)
            }
            is AddTransactionEvent.OnAddClicked -> {
                submitData()
            }
        }
    }

    private fun submitData() {
        val amountResult = validateAmount.execute(state.amount)
        val periodResult = if (recurring)validatePeriod.execute(state.period) else ValidationResult(true)
        val catResult = validateCategory.execute(state.category)

        val hasError = listOf(
            amountResult,
            periodResult,
            catResult
        ).any { !it.success }

        if(hasError) {
            state = state.copy(
                amountError = amountResult.error,
                periodError = periodResult.error,
                catError = catResult.error
            )
            return
        }
        viewModelScope.launch {
            validationEventChannel.send(ValidationEvent.Success)
        }
    }

    private fun addIncome() {
        addTransaction(getIncomeTransaction())
    }

    private fun addExpense() {
        addTransaction(getExpenseTransaction())
    }

    private fun updateIncome() {
        updateTransaction(getIncomeTransaction())
    }

    private fun updateExpense() {
        updateTransaction(getExpenseTransaction())
    }

    private fun getIncomeTransaction(): Transaction {
       return getTransaction { it.toDouble() }
    }

    private fun getExpenseTransaction(): Transaction {
        return getTransaction { it.toDouble() * -1 }
    }
    private fun getTransaction(converter: (String) -> Double): Transaction {
        val value = if(amount.isBlank()) 0.0 else converter(amount)
        try {
            recurringPeriod.toInt()
        } catch (e: NumberFormatException) {
            recurringPeriod = "0"
        }
        selectedCategory = if (selectedCategory.isBlank()) "Others" else selectedCategory
        if (!categories.contains(selectedCategory)) {
            categories.add(selectedCategory)
            sp.edit().putStringSet(Constants.KEY_CATEGORIES, categories.toSet()).apply()
        }
        return Transaction(
            name = transactionName,
            description = transactionDescription,
            category = selectedCategory,
            amount = value,
            date = datePicked,
            recurring = recurring,
            recurringInterval = recurringPeriod.toInt(),
            recurringIntervalUnit = periodUnit
        )
    }

    private fun addTransaction(transaction: Transaction) {
        viewModelScope.launch(Dispatchers.IO) {
            id = transactionRepository.addATransaction(transaction)
            if (id > -1L) resetForm()
        }
    }

    private fun resetForm() {
        transactionName = ""
        selectedCategory = ""
        transactionDescription = ""
        amount = ""
        datePicked = context.getString(R.string.date)
        recurring = false
        recurringPeriod = "1"
        periodUnit = context.getString(R.string.day)
        resetValidationState()
    }

    private fun resetValidationState() {
        state = TransactionState()
    }

    private fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch(Dispatchers.IO) {
            rowsUpdated = transactionRepository.updateATransaction(transaction)
            if (rowsUpdated > 0) resetForm()
        }
    }

    fun handleSubmit(mode: TransactionMode) {
        when(mode) {
            TransactionMode.IncomeAdd -> {
                addIncome()
                if (firstIncome) {
                    firstIncome = false
                    stepDesc = context.getString(R.string.do_you_want_to_add_another_income)
                    skip = context.getString(R.string.next)
                    sp.edit().putBoolean("first_income", false).apply()
                }
            }
            TransactionMode.ExpenseAdd -> {
                addExpense()
                skip = context.getString(R.string.next)
            }
            TransactionMode.IncomeEdit -> updateIncome()
            TransactionMode.ExpenseEdit -> updateExpense()
        }
    }

    fun getCurrentStartScreen(): String{
        return sp.getString(Constants.SCREEN, Screen.WelcomeAndIncomes.route) ?: Screen.WelcomeAndIncomes.route
    }

    fun updateStartingScreen(route: String) {
        sp.edit().putString(Constants.SCREEN, route).apply()
    }
}