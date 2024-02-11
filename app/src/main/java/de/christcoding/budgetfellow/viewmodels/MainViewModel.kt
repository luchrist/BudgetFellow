package de.christcoding.budgetfellow.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.christcoding.budgetfellow.AddTransactionEvent
import de.christcoding.budgetfellow.TransactionState
import de.christcoding.budgetfellow.data.BudgetRepository
import de.christcoding.budgetfellow.data.CategoryRepository
import de.christcoding.budgetfellow.data.Graph
import de.christcoding.budgetfellow.data.TransactionRepository
import de.christcoding.budgetfellow.data.models.Transaction
import de.christcoding.budgetfellow.domain.ValidationEvent
import de.christcoding.budgetfellow.domain.use_case.ValidateAmount
import de.christcoding.budgetfellow.domain.use_case.ValidateCategory
import de.christcoding.budgetfellow.domain.use_case.ValidatePeriod
import de.christcoding.budgetfellow.domain.use_case.ValidationResult
import de.christcoding.budgetfellow.navigation.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.chrono.ChronoLocalDate

class MainViewModel(
    private val validateAmount: ValidateAmount = ValidateAmount(),
    private val validatePeriod: ValidatePeriod = ValidatePeriod(),
    private val validateCategory: ValidateCategory = ValidateCategory(),
    private val transactionRepository: TransactionRepository = Graph.transactionRepository,
    private val budgetRepository: BudgetRepository = Graph.budgetRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    private val validationEventChannel = Channel<ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()
    var firstIncome by mutableStateOf(true) //datastore
    var state by mutableStateOf(TransactionState())
    var datePicked by mutableStateOf("Date")
    var recurring by mutableStateOf(false)
    var recurringPeriod by mutableStateOf("1")
    var periodUnit by mutableStateOf("Day")
    var stepDesc by mutableStateOf("Let's start by adding your first fix income.")
    var skip by mutableStateOf("Skip")
    var transactionName by mutableStateOf("")
    var transactionDescription by mutableStateOf("")
    var amount by mutableStateOf("")
    var id by mutableStateOf(-2L)
    var rowsUpdated by mutableStateOf(-1)

    var currency by mutableStateOf("€")
    var savingsPerMonth by mutableStateOf(0.0)

    var categories: MutableList<String> = mutableListOf("Salary","House", "Food", "Clothing", "Transport", "Self Care","Subscriptions", "Luxury", "Vacation")

    var selectedCategory by mutableStateOf("")

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
        datePicked = LocalDate.now().toString()
        recurring = false
        recurringPeriod = "1"
        periodUnit = "Day"
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


    fun getCurrentStartScreen(): String{
        return Screen.WelcomeAndIncomes.route//sp.getString(Constants.SCREEN, Screen.WelcomeAndIncomes.route) ?: Screen.WelcomeAndIncomes.route
    }

    fun updateStartingScreen(route: String) {
        //sp.edit().putString(Constants.SCREEN, route).apply()
    }
}