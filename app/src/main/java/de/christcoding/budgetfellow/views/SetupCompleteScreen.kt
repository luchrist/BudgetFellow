package de.christcoding.budgetfellow.views

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import de.christcoding.budgetfellow.R
import de.christcoding.budgetfellow.navigation.Screen
import de.christcoding.budgetfellow.utils.StartScreenState
import de.christcoding.budgetfellow.viewmodels.AppViewModelProvider
import de.christcoding.budgetfellow.viewmodels.IntroViewModel
import de.christcoding.budgetfellow.viewmodels.MainViewModel

@Composable
fun SetupCompleteScreen(navigateToHome: () -> Unit) {
    val ctx = LocalContext.current
    val vm: IntroViewModel = viewModel(factory = AppViewModelProvider.Factory)
    vm.skip = stringResource(R.string.done)
    Column {
        Text(text = stringResource(R.string.setup_complete))
        Text(text = stringResource(R.string.keep_tracking_all_your_expenses_and_incomes_to_save_money_and_reach_your_financial_goals))
    }

    NextSkipButton(onClickActions = {
        navigateToHome()
        StartScreenState(ctx).updateStartingScreen(Screen.Transactions.route)
        vm.introFinished()
    })
}