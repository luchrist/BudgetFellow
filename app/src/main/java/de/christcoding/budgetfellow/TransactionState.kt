package de.christcoding.budgetfellow

data class TransactionState(
    val amount: String = "",
    val amountError: String? = null,
    val period: String = "1",
    val periodError: String? = null,
    val category: String = "",
    val catError: String? = null
    )