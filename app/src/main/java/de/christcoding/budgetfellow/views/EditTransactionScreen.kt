package de.christcoding.budgetfellow.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import de.christcoding.budgetfellow.TransactionMode
import de.christcoding.budgetfellow.viewmodels.AppViewModelProvider
import de.christcoding.budgetfellow.viewmodels.TransactionViewModel

@Composable
fun EditTransactionScreen(navController: NavHostController, padding: PaddingValues, transactionId: String) {
    val vm: TransactionViewModel = viewModel(factory = AppViewModelProvider.Factory)
   vm.updateEditTransactionState(transactionId)
    val transactionMode = if(vm.editTransactionState.category.expense) TransactionMode.ExpenseEdit else TransactionMode.IncomeEdit
    Column (Modifier.padding(padding)){
        AddEditIncomeOrExpense(mode = transactionMode, vm.editTransactionState)
    }
}