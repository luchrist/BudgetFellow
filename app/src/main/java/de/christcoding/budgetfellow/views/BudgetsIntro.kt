package de.christcoding.budgetfellow.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import de.christcoding.budgetfellow.R
import de.christcoding.budgetfellow.navigation.Screen
import de.christcoding.budgetfellow.ui.components.BudgetCard
import de.christcoding.budgetfellow.ui.components.SubTitle
import de.christcoding.budgetfellow.ui.components.Title
import de.christcoding.budgetfellow.viewmodels.MainViewModel

@Composable
fun BudgetsIntroScreen(mainViewModel: MainViewModel, navController: NavHostController) {
Column {
    Title(title = "SET YOUR BUDGETS")
    SubTitle(subTitle = "Edit existing budgets or create new ones. You can always change this later again")
    Text(text = "You will save ${mainViewModel.savingsPerMonth} ${mainViewModel.currency} per month")
    val budgets = mainViewModel.budgets
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
       items(budgets) { budget ->
           BudgetCard(budget = budget)
       }
    }
    OutlinedButton(onClick = { /*TODO*/ }, Modifier.align(alignment = Alignment.CenterHorizontally)) {
        Icon(Icons.Default.Add, contentDescription = null)
        Text(text = stringResource(id = R.string.create_budget))
    }
}
    NextSkipButton(mainViewModel = mainViewModel, onClickActions = {
        navController.navigate(Screen.SetupComplete.route)
        mainViewModel.updateStartingScreen(Screen.SetupComplete.route)
    })
}