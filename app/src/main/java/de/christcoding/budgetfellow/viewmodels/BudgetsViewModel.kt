package de.christcoding.budgetfellow.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.christcoding.budgetfellow.data.BudgetRepository
import de.christcoding.budgetfellow.data.CategoryRepository
import de.christcoding.budgetfellow.data.TransactionRepository
import de.christcoding.budgetfellow.data.models.Budget
import de.christcoding.budgetfellow.data.models.Category
import de.christcoding.budgetfellow.data.models.Transaction
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.chrono.ChronoLocalDate

class BudgetsViewModel(
    private val budgetRepository: BudgetRepository,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
): ViewModel() {

    var savingsPerMonth by mutableStateOf(0.0)

    var budgets: List<Budget> = mutableListOf()

    init {
        viewModelScope.launch {
            budgets = getBudgets()
        }
    }

    suspend fun getBudgets(): List<Budget> {
        val categoryBudgets: MutableList<Budget> = mutableListOf()
        var allBudgets: List<Budget> = listOf()
        var categories: List<Category> = listOf()
        budgetRepository.getAllBudgets().collect{
            allBudgets = it
        }
        categoryRepository.getAllCategory().collect {
            categories = it
        }
        for (category in categories) {
            categoryBudgets.add(getBudgetForCategory(category, allBudgets))
        }
        return categoryBudgets
    }

    private suspend fun getBudgetForCategory(category: Category, budgets: List<Budget>): Budget {
        for (budget in budgets) {
            if (budget.categoryId == category.id) {
                return budget
            }
        }
        return createBudgetForCategory(category)
    }

    private suspend fun createBudgetForCategory(category: Category): Budget {
        val transactionsOfCurrentMonth: List<Transaction> = getTransactionsOfCurrentMonth()
        var newBudget = 0.0
        for (transaction in transactionsOfCurrentMonth) {
            if (transaction.categoryId == category.id) {
                newBudget += transaction.amount
            }
        }
        return Budget(categoryId = category.id, amount = newBudget, spent = newBudget)
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
}