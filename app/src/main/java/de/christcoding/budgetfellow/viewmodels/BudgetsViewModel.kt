package de.christcoding.budgetfellow.viewmodels

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.christcoding.budgetfellow.R
import de.christcoding.budgetfellow.data.BudgetRepository
import de.christcoding.budgetfellow.data.Graph
import de.christcoding.budgetfellow.data.TransactionRepository
import de.christcoding.budgetfellow.data.models.Budget
import de.christcoding.budgetfellow.data.models.Transaction
import de.christcoding.budgetfellow.utils.Constants
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.chrono.ChronoLocalDate

class BudgetsViewModel(
    private val budgetRepository: BudgetRepository = Graph.budgetRepository,
    private val transactionRepository: TransactionRepository = Graph.transactionRepository,
    private val context: Context
): ViewModel() {

    val sp: SharedPreferences = context.getSharedPreferences(Constants.SP, 0)
    var savingsPerMonth by mutableStateOf(0.0)

    var categories: MutableList<String> = (sp.getStringSet(Constants.KEY_CATEGORIES,
        listOf("Salary","House", "Food", "Clothing", "Transport", "Self Care","Subscriptions", "Luxury", "Vacation").toSet())
        ?.toMutableList() ?: mutableListOf())

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

    private suspend fun getBudgetForCategory(category: String, budgets: List<Budget>): Budget {
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
                val recurringDate = transaction.date
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
            if (transaction.date.monthValue == localDate.monthValue) {
                transactionsOfCurrentMonth.add(transaction)
            }
        }
        return transactionsOfCurrentMonth
    }

    private fun getLastDayOfCurrentMonth(localDate: LocalDate): ChronoLocalDate? {
        return localDate.withDayOfMonth(localDate.lengthOfMonth())
    }
}