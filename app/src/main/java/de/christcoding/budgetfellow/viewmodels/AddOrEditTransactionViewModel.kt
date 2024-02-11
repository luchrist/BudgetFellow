package de.christcoding.budgetfellow.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.christcoding.budgetfellow.AddTransactionEvent
import de.christcoding.budgetfellow.TransactionMode
import de.christcoding.budgetfellow.TransactionState
import de.christcoding.budgetfellow.data.CategoryRepository
import de.christcoding.budgetfellow.data.TransactionRepository
import de.christcoding.budgetfellow.data.models.Category
import de.christcoding.budgetfellow.data.models.Transaction
import de.christcoding.budgetfellow.domain.ValidationEvent
import de.christcoding.budgetfellow.domain.use_case.ValidateAmount
import de.christcoding.budgetfellow.domain.use_case.ValidateCategory
import de.christcoding.budgetfellow.domain.use_case.ValidatePeriod
import de.christcoding.budgetfellow.domain.use_case.ValidationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.random.Random

open class AddOrEditTransactionViewModel(
    private val validateAmount: ValidateAmount = ValidateAmount(),
    private val validatePeriod: ValidatePeriod = ValidatePeriod(),
    private val validateCategory: ValidateCategory = ValidateCategory(),
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    var state by mutableStateOf(TransactionState())
    private val validationEventChannel = Channel<ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()
    var datePicked by mutableStateOf(LocalDate.now())
    var recurring by mutableStateOf(false)
    var recurringPeriod by mutableStateOf("1")
    var periodUnit by mutableStateOf("Day")
    var stepDesc by mutableStateOf("Let's start by adding your first fix income.")
    var transactionName by mutableStateOf("")
    var transactionDescription by mutableStateOf("")
    var amount by mutableStateOf("")
    var id by mutableStateOf(-2L)
    var rowsUpdated by mutableStateOf(-1)
    var currency by mutableStateOf("€")

    var categories: MutableList<Category> =
        mutableListOf(
            Category( name = "House", color = Color(0f, 0.016f, 0.98f, 0.5f), expense = true),
            Category( name = "Food", color = Color(0.6f, 0.4f, 0.0f, 0.4f), expense = true),
            Category( name = "Clothing", color = Color(0f, 0.79f, 0.98f, 0.36f), expense = true),
            Category( name = "Transport", color = Color(1f, 0f, 0f, 0.4f), expense = true),
            Category( name = "Self Care", color = Color(1f, 0.1f, 0.7f, 0.2f), expense = true),
            Category( name = "Subscriptions", color = Color(0.5f, 0.5f, 0.5f, 0.36f), expense = true),
            Category( name = "Luxury", color = Color(0.98f, 0.8f, 0.0f, 0.36f), expense = true),
            Category( name = "Vacation", color = Color(0.1f, 0.8f, 0.7f, 0.4f), expense = true)
        )
    var selectedCategoryName by mutableStateOf("")
    var selectedCategory by mutableStateOf(Category( name = "House", color = Color(0f, 0.016f, 0.98f, 0.5f), expense = true))

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
        selectedCategoryName = if (selectedCategoryName.isBlank()) "Others" else selectedCategoryName
        var catExist = false
        for (category in categories) {
            if (category.name == selectedCategoryName) {
                selectedCategory = category
                catExist = true
                break
            }
        }
        if (!catExist) {
            val randomfloat = Random.nextFloat()
            val randomfloat2 = Random.nextFloat()
            val randomfloat3 = Random.nextFloat()
            selectedCategory = Category( name = selectedCategoryName, color = Color(randomfloat, randomfloat2, randomfloat3, 0.5f), expense = value < 0)
            viewModelScope.launch(Dispatchers.IO) {
                categoryRepository.addACategory(selectedCategory)
            }
        }
        return Transaction(
            name = transactionName,
            description = transactionDescription,
            categoryId = selectedCategory.id,
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
        selectedCategoryName = ""
        transactionDescription = ""
        amount = ""
        datePicked = LocalDate.now()
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