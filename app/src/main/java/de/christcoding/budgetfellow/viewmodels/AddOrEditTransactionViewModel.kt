package de.christcoding.budgetfellow.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.christcoding.budgetfellow.AddTransactionEvent
import de.christcoding.budgetfellow.TransactionMode
import de.christcoding.budgetfellow.TransactionState
import de.christcoding.budgetfellow.data.CategoryRepository
import de.christcoding.budgetfellow.data.TransactionRepository
import de.christcoding.budgetfellow.data.models.Category
import de.christcoding.budgetfellow.data.models.Transaction
import de.christcoding.budgetfellow.data.models.TransactionDetails
import de.christcoding.budgetfellow.domain.ValidationEvent
import de.christcoding.budgetfellow.domain.use_case.ValidateAmount
import de.christcoding.budgetfellow.domain.use_case.ValidateCategory
import de.christcoding.budgetfellow.domain.use_case.ValidatePeriod
import de.christcoding.budgetfellow.domain.use_case.ValidationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID
import kotlin.random.Random

open class AddOrEditTransactionViewModel(
    private val validateAmount: ValidateAmount = ValidateAmount(),
    private val validatePeriod: ValidatePeriod = ValidatePeriod(),
    private val validateCategory: ValidateCategory = ValidateCategory(),
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {

    private var _editableTransaction: TransactionDetails? = null
    var state by mutableStateOf(TransactionState())
    private val validationEventChannel = Channel<ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()
    var datePicked by mutableStateOf(LocalDate.now())
    var recurring by mutableStateOf(false)
    var recurringPeriod by mutableStateOf("1")
    var periodUnit by mutableStateOf("Day")
    var stepDesc by mutableStateOf("Let's start by adding your first fix income.")
    var transactionName by mutableStateOf("")
    var transactionId = 0L
    var recurringDeleted = false
    var transactionDescription by mutableStateOf("")
    var amount by mutableStateOf("")
    var id by mutableStateOf(-2L)
    var rowsUpdated by mutableStateOf(-1)
    var currency by mutableStateOf("€")

    val transactionsFlow: StateFlow<List<Transaction>> = transactionRepository.getAllTransactions()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = listOf()
        )

    var categories: StateFlow<List<Category>> = categoryRepository.getAllCategory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    var categoryNames = categories.map { it.map { it.name } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    var selectedCategoryName by mutableStateOf("")
    var selectedCategory by mutableStateOf(Category( name = "House", color = Color(0f, 0.016f, 0.98f, 0.5f).toArgb(), expense = true))
    var transactions by mutableStateOf(listOf<Transaction>())
    init {
        viewModelScope.launch {
            transactionsFlow.collectLatest { transactions = it }
        }
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
            is AddTransactionEvent.OnSaveAllClicked -> {
                submitDataForAll()
            }
            is AddTransactionEvent.OnDeleteClicked -> {
                viewModelScope.launch {
                    if (_editableTransaction != null)
                        transactionRepository.deleteATransaction(Transaction(id = _editableTransaction!!.id, name = _editableTransaction!!.name, description = _editableTransaction!!.description, categoryId = _editableTransaction!!.category.id, amount = _editableTransaction!!.amount, date = _editableTransaction!!.date, recurring = _editableTransaction!!.recurring, recurringInterval = _editableTransaction!!.recurringInterval, recurringIntervalUnit = _editableTransaction!!.recurringIntervalUnit, recurringId = _editableTransaction!!.recurringId, recurringDeleted = _editableTransaction?.recurringDeleted ?: false))
                }
            }
            is AddTransactionEvent.OnDeleteRecurringClicked -> {
                for (transaction in transactions) {
                    if (transaction.recurringId.equals(_editableTransaction!!.recurringId)) {
                        if (transaction.date.isAfter(_editableTransaction!!.date.minusDays(1))) {
                            viewModelScope.launch {
                                transactionRepository.deleteATransaction(transaction)
                            }
                        } else {
                            viewModelScope.launch {
                                transactionRepository.updateATransaction(
                                    transaction.copy(
                                        recurringDeleted = true
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun submitDataForAll() {
        if (checkData()) {
            viewModelScope.launch {
                validationEventChannel.send(ValidationEvent.AllSuccess)
            }
        }
    }

    private fun submitData() {
        if (checkData()) {
            viewModelScope.launch {
                validationEventChannel.send(ValidationEvent.Success)
            }
        }
    }

    private fun checkData(): Boolean {
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
            return false
        }
        return true
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

    private fun updateAllIncomes() {
        updateAllTransactions(getIncomeTransaction())
    }

    private fun updateExpense() {
        updateTransaction(getExpenseTransaction())
    }

    private fun updateAllExpense() {
        updateAllTransactions(getExpenseTransaction())
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
        for (category in categories.value) {
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
            selectedCategory = Category( name = selectedCategoryName, color = Color(randomfloat, randomfloat2, randomfloat3, 0.5f).toArgb(), expense = value < 0)
            viewModelScope.launch(Dispatchers.IO) {
                categoryRepository.addACategory(selectedCategory)
            }
            selectedCategory = selectedCategory.copy(id = categories.value.size.toLong() + 1)
        }
        val recurringId = _editableTransaction?.recurringId ?: UUID.randomUUID().toString()
        var ta = Transaction(
                name = transactionName,
                description = transactionDescription,
                categoryId = selectedCategory.id,
                amount = value,
                date = datePicked,
                recurring = recurring,
                recurringInterval = recurringPeriod.toInt(),
                recurringIntervalUnit = periodUnit,
                recurringId = recurringId,
                recurringDeleted = recurringDeleted
            )
        if (transactionId > 0) {
            ta = ta.copy(id = transactionId)
        }
        return ta
    }

    private fun addTransaction(transaction: Transaction) {
        viewModelScope.launch(Dispatchers.IO) {
            val recurringId = UUID.randomUUID().toString()
            id = transactionRepository.addATransaction(transaction.copy(recurringId = recurringId))
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
        transactionId = 0
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

    private fun updateAllTransactions(transaction: Transaction) {
        val recurringTranses = transactions.filter { it.recurringId == transaction.recurringId }
        viewModelScope.launch {
            rowsUpdated = transactionRepository.updateATransaction(transaction)
            if (rowsUpdated > 0) resetForm()
            if (transaction.recurring) {
                for (trans in recurringTranses) {
                    if (trans.date.isAfter(
                            transaction.date
                        )
                    ) {
                        println("test")
                        rowsUpdated = transactionRepository.updateATransaction(
                            trans.copy(
                                name = transaction.name,
                                description = transaction.description,
                                categoryId = transaction.categoryId,
                                amount = transaction.amount,
                                recurringInterval = transaction.recurringInterval,
                                recurringIntervalUnit = transaction.recurringIntervalUnit
                            )
                        )
                    }
                }
            }
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

    open fun handleAllSubmit(mode: TransactionMode) {
        when (mode) {
            TransactionMode.IncomeEdit -> updateAllIncomes()
            TransactionMode.ExpenseEdit -> updateAllExpense()
            else -> {}
        }
    }

    fun setEditableTransaction(transaction: TransactionDetails?, mode: TransactionMode) {
        if ((_editableTransaction == null || isInvalid(_editableTransaction!!)) && transaction != null) {
            var trans = transaction
            if(mode == TransactionMode.ExpenseEdit) {
                trans = transaction.copy(amount = transaction.amount * -1)
            }
            _editableTransaction = trans
            transactionId = trans.id
            recurringDeleted = trans.recurringDeleted
            transactionName = trans.name
            transactionDescription = trans.description
            amount = trans.amount.toString()
            datePicked = trans.date
            recurring = trans.recurring
            recurringPeriod = trans.recurringInterval.toString()
            periodUnit = trans.recurringIntervalUnit
            selectedCategoryName = trans.category.name
            state = TransactionState(
                category = trans.category.name,
                amount = trans.amount.toString(),
                period = trans.recurringInterval.toString()
            )
        }
    }

    private fun isInvalid(trans: TransactionDetails): Boolean {
        return if(trans.category.name.equals("Uncategorized", true)
            && trans.name.isEmpty()
            && trans.amount == 0.0
            && trans.id == 0L) true
        else false
    }
}