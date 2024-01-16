package de.christcoding.budgetfellow.viewmodels

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.christcoding.budgetfellow.AddTransactionEvent
import de.christcoding.budgetfellow.R
import de.christcoding.budgetfellow.TransactionMode
import de.christcoding.budgetfellow.TransactionState
import de.christcoding.budgetfellow.data.Graph
import de.christcoding.budgetfellow.data.TransactionRepository
import de.christcoding.budgetfellow.data.models.Transaction
import de.christcoding.budgetfellow.domain.ValidationEvent
import de.christcoding.budgetfellow.domain.use_case.ValidateAmount
import de.christcoding.budgetfellow.domain.use_case.ValidateCategory
import de.christcoding.budgetfellow.domain.use_case.ValidatePeriod
import de.christcoding.budgetfellow.domain.use_case.ValidationResult
import de.christcoding.budgetfellow.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

open class AddOrEditTransactionViewModel(
    private val validateAmount: ValidateAmount = ValidateAmount(),
    private val validatePeriod: ValidatePeriod = ValidatePeriod(),
    private val validateCategory: ValidateCategory = ValidateCategory(),
    private val transactionRepository: TransactionRepository = Graph.transactionRepository,
    private val context: Context
) : ViewModel() {

    var state by mutableStateOf(TransactionState())
    private val validationEventChannel = Channel<ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()
    val sp: SharedPreferences = context.getSharedPreferences(Constants.SP, 0)
    var datePicked by mutableStateOf(LocalDate.now())
    var recurring by mutableStateOf(false)
    var recurringPeriod by mutableStateOf("1")
    var periodUnit by mutableStateOf(context.getString(R.string.day))
    var stepDesc by mutableStateOf(context.getString(R.string.let_s_start_by_adding_your_first_income))
    var transactionName by mutableStateOf("")
    var transactionDescription by mutableStateOf("")
    var amount by mutableStateOf("")
    var id by mutableStateOf(-2L)
    var rowsUpdated by mutableStateOf(-1)
    var currency by mutableStateOf("€")

    var categories: MutableList<String> = (sp.getStringSet(
        Constants.KEY_CATEGORIES,
        listOf(
            "Salary",
            "House",
            "Food",
            "Clothing",
            "Transport",
            "Self Care",
            "Subscriptions",
            "Luxury",
            "Vacation"
        ).toSet()
    )
        ?.toMutableList() ?: mutableListOf())
    var selectedCategory by mutableStateOf("")

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
        val periodResult =
            if (recurring) validatePeriod.execute(state.period) else ValidationResult(true)
        val catResult = validateCategory.execute(state.category)

        val hasError = listOf(
            amountResult,
            periodResult,
            catResult
        ).any { !it.success }

        if (hasError) {
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
        val value = if (amount.isBlank()) 0.0 else converter(amount)
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
        datePicked = LocalDate.now()
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

    open fun handleSubmit(mode: TransactionMode) {
        when (mode) {
            TransactionMode.IncomeAdd -> {
                addIncome()
            }

            TransactionMode.ExpenseAdd -> {
                addExpense()
            }

            TransactionMode.IncomeEdit -> updateIncome()
            TransactionMode.ExpenseEdit -> updateExpense()
        }
    }
}