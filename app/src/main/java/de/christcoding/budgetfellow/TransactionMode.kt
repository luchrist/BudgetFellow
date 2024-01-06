package de.christcoding.budgetfellow

sealed class TransactionMode {
    object IncomeAdd : TransactionMode()
    object ExpenseAdd : TransactionMode()
    object IncomeEdit : TransactionMode()
    object ExpenseEdit : TransactionMode()
}