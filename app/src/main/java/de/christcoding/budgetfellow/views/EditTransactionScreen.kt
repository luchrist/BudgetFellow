package de.christcoding.budgetfellow.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import de.christcoding.budgetfellow.TransactionMode
import de.christcoding.budgetfellow.viewmodels.AddOrEditTransactionViewModel
import de.christcoding.budgetfellow.viewmodels.AppViewModelProvider
import de.christcoding.budgetfellow.viewmodels.TransactionViewModel

@Composable
fun EditTransactionScreen(navController: NavHostController, padding: PaddingValues, transactionId: String) {
    val addOrEditTransactionViewModel: AddOrEditTransactionViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val vm: TransactionViewModel = viewModel(factory = AppViewModelProvider.Factory)
    if(vm.transactions.isNotEmpty()) {
        LaunchedEffect(key1 = LocalContext.current) {
            vm.updateEditTransactionState(transactionId)
        }
    }
    if (vm.transactionMode != null) {
        Column (Modifier.padding(padding)){
            AddEditIncomeOrExpense(mode = vm.transactionMode!!, vm.editTransactionState, addOrEditTransactionViewModel)
        }
    }
}