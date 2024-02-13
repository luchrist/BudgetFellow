package de.christcoding.budgetfellow

data class BudgetState(
    val amount: String = "0.0",
    val amountError: String? = null,
    val category: String = "",
    val catError: String? = null,
    val spent: String = "0.0",
    val spentError: String? = null
): CategoryState