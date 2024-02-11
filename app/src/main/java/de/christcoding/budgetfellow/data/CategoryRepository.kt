package de.christcoding.budgetfellow.data

import de.christcoding.budgetfellow.data.models.Category

class CategoryRepository(private val categoryDao: CategoryDao) {

    suspend fun addACategory(category: Category): Long {
        return categoryDao.addACategory(category)
    }

    fun getAllCategory() = categoryDao.getAllCategories()

    fun getACategoryById(id: Long) = categoryDao.getACategoryById(id)

    fun getExpenseCategories() = categoryDao.getExpenseCategories()

    fun getIncomeCategories() = categoryDao.getIncomeCategories()

    suspend fun updateACategory(category: Category): Int {
        return categoryDao.updateACategory(category)
    }

    suspend fun deleteACategory(category: Category): Int {
        return categoryDao.deleteACategory(category)
    }
}