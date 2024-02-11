package de.christcoding.budgetfellow.data

import android.content.Context

interface AppContainer {
    val budgetRepository: BudgetRepository
    val categoryRepository: CategoryRepository
    val transactionRepository: TransactionRepository
}

/**
 * [AppContainer] implementation that provides instance of [OfflineItemsRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for [ItemsRepository]
     */
    override val budgetRepository: BudgetRepository by lazy {
        BudgetRepository(BudgetDatabase.getDatabase(context).budgetDao())
    }

    override val categoryRepository: CategoryRepository by lazy {
        CategoryRepository(BudgetDatabase.getDatabase(context).categoryDao())
    }

    override val transactionRepository: TransactionRepository by lazy {
        TransactionRepository(BudgetDatabase.getDatabase(context).transactionDao())
    }
}