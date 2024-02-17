package de.christcoding.budgetfellow.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import de.christcoding.budgetfellow.BudgetState
import de.christcoding.budgetfellow.R
import de.christcoding.budgetfellow.navigation.Screen
import de.christcoding.budgetfellow.ui.components.BudgetCard
import de.christcoding.budgetfellow.ui.components.SubTitle
import de.christcoding.budgetfellow.ui.components.Title
import de.christcoding.budgetfellow.ui.theme.BackgroundElevated
import de.christcoding.budgetfellow.ui.theme.Medium
import de.christcoding.budgetfellow.ui.theme.Positive
import de.christcoding.budgetfellow.viewmodels.AppViewModelProvider
import de.christcoding.budgetfellow.viewmodels.ApplicationViewModel
import de.christcoding.budgetfellow.viewmodels.BudgetUiState
import de.christcoding.budgetfellow.viewmodels.BudgetsViewModel
import de.christcoding.budgetfellow.viewmodels.TransactionsUiState

@Composable
fun BudgetsScreen(navController: NavHostController, padding: PaddingValues) {
    val appViewModel: ApplicationViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val vm: BudgetsViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val budgetUiState = vm.budgetState
    if (vm.categories.isNotEmpty() && vm.transactions.isNotEmpty()) {
        vm.updateBudgetState()
    }
    when (budgetUiState) {
        is BudgetUiState.Loading -> {
            LoadingScreen(modifier = Modifier.fillMaxSize())
        }
        is BudgetUiState.Success -> {
            Column(modifier = Modifier.padding(padding)) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(
                        BackgroundElevated
                    )) {
                    Text(text = "Estimated Monthly Savings", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(top = 8.dp))
                    Text(text = "${budgetUiState.savingsPerMonth} ${appViewModel.currency}", style = MaterialTheme.typography.headlineLarge, modifier = Modifier.padding(bottom = 8.dp))
                    OutlinedButton(onClick = { /*TODO*/ },modifier = Modifier.padding(bottom = 8.dp) ,contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp) ,enabled = false, colors = ButtonColors(disabledContainerColor = Color.Transparent, disabledContentColor = Medium, containerColor = Color.Transparent, contentColor = Color.White)) {
                        Text(text = "${budgetUiState.leftInBudgets}${appViewModel.currency} left in budgets")
                    }
                }
                //val budgets by vm.budgets.collectAsState(initial = listOf())
                LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                    items(budgetUiState.budgets) { budget ->
                        Surface(onClick = {
                            navController.navigate("${Screen.EditBudget.route}/${budget.id}")
                        }){
                            BudgetCard(budget = budget)
                        }
                    }
                }
                Button(onClick = { navController.navigate(Screen.CreateBudget.route)}, Modifier.align(alignment = Alignment.CenterHorizontally)) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Text(text = stringResource(id = R.string.create_budget))
                }
            }
        }
    }
}

@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {
    Image(
        modifier = modifier.size(200.dp),
        painter = painterResource(R.drawable.loading_img),
        contentDescription = stringResource(R.string.loading)
    )
}
