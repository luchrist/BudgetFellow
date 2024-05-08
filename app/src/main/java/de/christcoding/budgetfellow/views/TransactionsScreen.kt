package de.christcoding.budgetfellow.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import de.christcoding.budgetfellow.R
import de.christcoding.budgetfellow.data.datastore.StoreAppSettings
import de.christcoding.budgetfellow.navigation.Screen
import de.christcoding.budgetfellow.ui.theme.Positive
import de.christcoding.budgetfellow.viewmodels.AppViewModelProvider
import de.christcoding.budgetfellow.viewmodels.TransactionViewModel
import de.christcoding.budgetfellow.viewmodels.TransactionsUiState

@Composable
fun TransactionsScreen(navController: NavHostController, padding: PaddingValues) {
    val vm: TransactionViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val ctx = LocalContext.current
    val dataStore = StoreAppSettings(ctx)
    val start by dataStore.getCycleStart.collectAsState(1)
    val smart by dataStore.getSmartCycle.collectAsState(false)

    vm.cycleStart = start
    vm.smartCycle = smart

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
                Column(modifier = Modifier.padding(it)) {
                    Column(modifier = Modifier.fillMaxWidth().padding(8.dp).background(MaterialTheme.colorScheme.surfaceVariant)) {
                        Text(text = "Cycle Balance", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(top = 8.dp))
                        Text(text = "${transactionsState.monthlyBalance} €", style = MaterialTheme.typography.headlineLarge, modifier = Modifier.padding(bottom = 8.dp))
                    }
                    Column(modifier = Modifier.fillMaxWidth().padding(8.dp).background(MaterialTheme.colorScheme.surfaceVariant)) {
                        Text(text = "Cycle Planned Balance", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(top = 8.dp))
                        Text(text = "${transactionsState.futureMonthlyBalance} €", style = MaterialTheme.typography.headlineLarge)
//                        Button(onClick = { /*TODO*/ },modifier = Modifier.padding(bottom = 8.dp) ,contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp) ,enabled = false, colors = ButtonColors(disabledContainerColor = Positive, disabledContentColor = Color.White, containerColor = Positive, contentColor = Color.White)) {
//                            Icon(painterResource(id = R.drawable.baseline_trending_up_24), contentDescription = null)
//                            Spacer(modifier = Modifier.padding(4.dp))
//                            Text(text = "+13%")
//                        }
                    }
                    FutureTransactions(transactions = transactionsState.futureTransactions, navController)
                    TransactionsList(transactions = transactionsState.transactions, navController)   
                }
            }
        }
    }
}