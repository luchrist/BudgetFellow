package de.christcoding.budgetfellow.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import de.christcoding.budgetfellow.TransactionMode
import de.christcoding.budgetfellow.viewmodels.AddOrEditTransactionViewModel
import de.christcoding.budgetfellow.viewmodels.AppViewModelProvider

@Composable
fun AddTransactionScreen(mode: String, navController: NavHostController, padding: PaddingValues) {
    val vm: AddOrEditTransactionViewModel = viewModel(factory = AppViewModelProvider.Factory)
    Column (Modifier.padding(padding)){
        if (mode == "i") {
            AddEditIncomeOrExpense(mode = TransactionMode.IncomeAdd, specificViewModel = vm)
        } else {
            AddEditIncomeOrExpense(mode = TransactionMode.ExpenseAdd, specificViewModel = vm)
        }
    }
}