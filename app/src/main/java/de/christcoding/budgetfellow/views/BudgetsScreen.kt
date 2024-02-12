package de.christcoding.budgetfellow.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import de.christcoding.budgetfellow.R
import de.christcoding.budgetfellow.ui.components.BudgetCard
import de.christcoding.budgetfellow.ui.components.SubTitle
import de.christcoding.budgetfellow.ui.components.Title
import de.christcoding.budgetfellow.viewmodels.AppViewModelProvider
import de.christcoding.budgetfellow.viewmodels.ApplicationViewModel
import de.christcoding.budgetfellow.viewmodels.BudgetsViewModel

@Composable
fun BudgetsScreen() {
    val appViewModel: ApplicationViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val vm: BudgetsViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val budgetUiState by vm.budgetState.collectAsState()
    Column {
        Title(title = "SET YOUR BUDGETS")
        SubTitle(subTitle = "Edit existing budgets or create new ones. You can always change this later again")
        Text(text = "You will save ${budgetUiState.savingsPerMonth} ${appViewModel.currency} per month")
        //val budgets by vm.budgets.collectAsState(initial = listOf())
        LazyVerticalGrid(columns = GridCells.Fixed(2)) {
            items(budgetUiState.budgets) { budget ->
                BudgetCard(budget = budget)
            }
        }
        Button(onClick = { /*TODO*/ }, Modifier.align(alignment = Alignment.CenterHorizontally)) {
            Icon(Icons.Default.Add, contentDescription = null)
            Text(text = stringResource(id = R.string.create_budget))
        }
    }
}