package de.christcoding.budgetfellow.data

import de.christcoding.budgetfellow.data.models.Budget

class BudgetRepository(private val budgetDao: BudgetDao) {

    suspend fun addABudget(budget: Budget): Long {
        return budgetDao.addABudget(budget)
    }

    fun getAllBudgets() = budgetDao.getAllBudgets()

    fun getABudgetById(id: Long) = budgetDao.getABudgetById(id)

    suspend fun updateABudget(budget: Budget): Int {
        return budgetDao.updateABudget(budget)
    }

    suspend fun deleteABudget(budget: Budget): Int {
        return budgetDao.deleteABudget(budget)
    }
}