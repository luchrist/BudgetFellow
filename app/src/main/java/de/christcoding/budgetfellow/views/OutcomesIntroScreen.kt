package de.christcoding.budgetfellow.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import de.christcoding.budgetfellow.R
import de.christcoding.budgetfellow.TransactionMode
import de.christcoding.budgetfellow.navigation.Screen
import de.christcoding.budgetfellow.utils.StartScreenState
import de.christcoding.budgetfellow.viewmodels.AppViewModelProvider
import de.christcoding.budgetfellow.viewmodels.IntroViewModel

@Composable
fun OutcomesIntroScreen(navController: NavHostController) {
    val ctx = LocalContext.current
    val vm: IntroViewModel = viewModel(factory = AppViewModelProvider.Factory)
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(text = stringResource(R.string.almost_done_now_add_all_your_fix_expenses))
        AddEditIncomeOrExpense(mode = TransactionMode.ExpenseAdd, specificViewModel = vm)
    }
    NextSkipButton( onClickActions = {
        navController.navigate(Screen.TransactionsIntro.route)
        StartScreenState(ctx).updateStartingScreen(Screen.TransactionsIntro.route)
    }, vm.skip)
}