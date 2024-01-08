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
import de.christcoding.budgetfellow.data.Graph
import de.christcoding.budgetfellow.data.models.Transaction
import de.christcoding.budgetfellow.data.TransactionRepository
import de.christcoding.budgetfellow.data.models.Budget
import de.christcoding.budgetfellow.domain.ValidationEvent
import de.christcoding.budgetfellow.domain.use_case.ValidateAmount
import de.christcoding.budgetfellow.domain.use_case.ValidatePeriod
import de.christcoding.budgetfellow.domain.use_case.ValidationResult
import de.christcoding.budgetfellow.navigation.Screen
import de.christcoding.budgetfellow.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val validateAmount: ValidateAmount = ValidateAmount(),
    private val validatePeriod: ValidatePeriod = ValidatePeriod(),
    private val transactionRepository: TransactionRepository = Graph.transactionRepository,
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

    var categories: List<String> = sp.getStringSet("categories", listOf("Salary","House", "Food", "Clothing", "Transport", "Self Care","Subscriptions", "Luxury", "Vacation").toSet())
        ?.toList() ?: listOf()
    var selectedCategory by mutableStateOf("")

    val budgets = listOf(
        Budget("Food", 100.0, 20.0, CardColors(Color.Cyan, Color.Blue, Color.Gray, Color.Gray)),
        Budget("House", 1000.0, 890.0, CardColors(Color.Magenta, Color.DarkGray, Color.Gray, Color.Gray)),
        Budget("Clothing", 100.0, 0.0, CardColors(Color.Green, Color.Magenta, Color.Gray, Color.Gray))
    )

    fun onEvent(event: AddTransactionEvent) {
        when (event) {
            is AddTransactionEvent.OnAmountChanged -> {
                state = state.copy(amount = event.amount)
            }
            is AddTransactionEvent.OnPeriodChanged -> {
                state = state.copy(period = event.period)
            }
            is AddTransactionEvent.OnAddClicked -> {
                submitData()
            }
        }
    }

    private fun submitData() {
        val amountResult = validateAmount.execute(state.amount)
        val periodResult = if (recurring)validatePeriod.execute(state.period) else ValidationResult(true)

        val hasError = listOf(
            amountResult,
            periodResult
        ).any { !it.success }

        if(hasError) {
            state = state.copy(
                amountError = amountResult.error,
                periodError = periodResult.error
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
        return Transaction(
            name = transactionName,
            description = transactionDescription,
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
        transactionDescription = ""
        amount = ""
        datePicked = context.getString(R.string.date)
        recurring = false
        recurringPeriod = "1"
        periodUnit = context.getString(R.string.day)
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