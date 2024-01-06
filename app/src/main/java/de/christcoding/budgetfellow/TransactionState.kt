package de.christcoding.budgetfellow

data class TransactionState(
    val amount: String = "",
    val amountError: String? = null,
    val period: String = "",
    val periodError: String? = null,
)