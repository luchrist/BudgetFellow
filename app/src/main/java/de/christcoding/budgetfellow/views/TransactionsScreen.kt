package de.christcoding.budgetfellow.views

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import de.christcoding.budgetfellow.R
import de.christcoding.budgetfellow.navigation.Screen
import de.christcoding.budgetfellow.viewmodels.AppViewModelProvider
import de.christcoding.budgetfellow.viewmodels.BudgetUiState
import de.christcoding.budgetfellow.viewmodels.TransactionViewModel
import de.christcoding.budgetfellow.viewmodels.TransactionsUiState

@Composable
fun TransactionsScreen(navController: NavHostController, padding: PaddingValues) {
    val vm: TransactionViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val transactionsState = vm.transactionsState
    if (vm.categories.isNotEmpty()) {
        vm.updateTransactionState()
    }
    Scaffold (
        modifier = Modifier.padding(padding),
        floatingActionButton = {
            MultiFloatingActionButton(fabIcon = painterResource(id = R.drawable.baseline_add_24),
                items = arrayListOf(FabItem(icon =  painterResource(id = R.drawable.baseline_add_24), label = "Add Income") {navController.navigate("${Screen.BottomNavigationScreens.TransactionAdd.route}/i")},
                    FabItem(icon = painterResource(id = R.drawable.minus), label = "Add Expense") {navController.navigate("${Screen.BottomNavigationScreens.TransactionAdd.route}/e")}))
        }
    ){
        when (transactionsState) {
            is TransactionsUiState.Loading -> {
                LoadingScreen(modifier = Modifier.fillMaxSize())
            }

            is TransactionsUiState.Success -> {
                TransactionsList(transactions = transactionsState.transactions, navController, it)
            }
        }
    }
}