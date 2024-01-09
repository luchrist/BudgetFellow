package de.christcoding.budgetfellow

sealed class AddTransactionEvent {
    data class OnAmountChanged(val amount: String) : AddTransactionEvent()
    data class OnPeriodChanged(val period: String) : AddTransactionEvent()
    data class OnCategoryChanged(val category: String): AddTransactionEvent()
    object OnAddClicked : AddTransactionEvent()
}