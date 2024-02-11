package de.christcoding.budgetfellow.data.models

data class BudgetDetails(
    val id: Long = 0L,
    val category: Category,
    val amount: Double,
    val spent: Double
)
