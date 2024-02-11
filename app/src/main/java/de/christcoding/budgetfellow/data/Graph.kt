package de.christcoding.budgetfellow.data

import android.content.Context
import androidx.room.Room
import de.christcoding.budgetfellow.utils.Constants

object Graph {
    lateinit var database: BudgetDatabase

    val transactionRepository by lazy{
        TransactionRepository(transactionDao = database.transactionDao())
    }

    val budgetRepository by lazy{
        BudgetRepository(budgetDao = database.budgetDao())
    }

    val categoryRepository by lazy{
        CategoryRepository(categoryDao = database.categoryDao())
    }

    fun provide(context: Context) {
        database = Room.databaseBuilder(context, BudgetDatabase::class.java, Constants.DATABASE_NAME).build()
    }
}