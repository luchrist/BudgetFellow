package de.christcoding.budgetfellow.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import de.christcoding.budgetfellow.R
import de.christcoding.budgetfellow.TransactionMode
import de.christcoding.budgetfellow.navigation.Screen
import de.christcoding.budgetfellow.viewmodels.MainViewModel

@Composable
fun OutcomesIntroScreen(mainViewModel: MainViewModel, navController: NavHostController) {
    mainViewModel.stepDesc = stringResource(R.string.almost_done_now_add_all_your_fix_expenses)
    mainViewModel.skip = stringResource(R.string.skip)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        AddEditInOrOutcome(mainViewModel = mainViewModel, mode = TransactionMode.ExpenseAdd)
    }
    NextSkipButton(mainViewModel = mainViewModel, onClickActions = {
        navController.navigate(Screen.SetBudgets.route)
        mainViewModel.updateStartingScreen(Screen.SetBudgets.route)
    })
}