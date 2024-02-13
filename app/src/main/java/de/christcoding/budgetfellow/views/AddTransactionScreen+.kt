package de.christcoding.budgetfellow.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import de.christcoding.budgetfellow.TransactionMode

@Composable
fun AddTransactionScreen(mode: String, navController: NavHostController, padding: PaddingValues) {
    Column (Modifier.padding(padding)){
        if (mode == "i") {
            AddEditIncomeOrExpense(mode = TransactionMode.IncomeAdd)
        } else {
            AddEditIncomeOrExpense(mode = TransactionMode.ExpenseAdd)
        }
    }
}