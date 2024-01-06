package de.christcoding.budgetfellow.viewmodels

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import de.christcoding.budgetfellow.AddTransactionEvent
import de.christcoding.budgetfellow.R
import de.christcoding.budgetfellow.TransactionMode
import de.christcoding.budgetfellow.TransactionState
import de.christcoding.budgetfellow.data.Graph
import de.christcoding.budgetfellow.data.Transaction
import de.christcoding.budgetfellow.data.TransactionRepository
import de.christcoding.budgetfellow.domain.ValidationEvent
import de.christcoding.budgetfellow.domain.use_case.ValidateAmount
import de.christcoding.budgetfellow.domain.use_case.ValidatePeriod
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val validateAmount: ValidateAmount = ValidateAmount(),
    private val validatePeriod: ValidatePeriod = ValidatePeriod(),
    private val transactionRepository: TransactionRepository = Graph.transactionRepository,
    application: Application
) : AndroidViewModel(application) {

    private val validationEventChannel = Channel<ValidationEvent>()
    val validationEvents = validationEventChannel.receiveAsFlow()

    var state by mutableStateOf(TransactionState())
    var datePicked by mutableStateOf(application.getString(R.string.date))
    var recurring by mutableStateOf(false)
    var recurringPeriod by mutableStateOf("1")
    var periodUnit by mutableStateOf(application.getString(R.string.day))
    var title by mutableStateOf(application.getString(R.string.hi_i_am_your_budget_fellow_i_will_help_you_reach_your_financial_goals))
    var stepDesc by mutableStateOf(application.getString(R.string.let_s_start_by_adding_your_first_income))
    var skip by mutableStateOf(application.getString(R.string.skip))
    var transactionName by mutableStateOf("")
    var transactionDescription by mutableStateOf("")
    var amount by mutableStateOf("")

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
        val periodResult = validatePeriod.execute(state.period)

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
            transactionRepository.addATransaction(transaction)
        }
    }

    private fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch(Dispatchers.IO) {
            transactionRepository.updateATransaction(transaction)
        }
    }

    fun handleSubmit(mode: TransactionMode) {
        when(mode) {
            TransactionMode.IncomeAdd -> addIncome()
            TransactionMode.ExpenseAdd -> addExpense()
            TransactionMode.IncomeEdit -> updateIncome()
            TransactionMode.ExpenseEdit -> updateExpense()
        }
    }
}