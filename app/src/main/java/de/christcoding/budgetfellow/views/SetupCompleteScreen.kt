package de.christcoding.budgetfellow.views

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import de.christcoding.budgetfellow.R
import de.christcoding.budgetfellow.navigation.Screen
import de.christcoding.budgetfellow.utils.Constants
import de.christcoding.budgetfellow.utils.StartScreenState
import de.christcoding.budgetfellow.viewmodels.AppViewModelProvider
import de.christcoding.budgetfellow.viewmodels.IntroViewModel

@Composable
fun SetupCompleteScreen(navigateToHome: () -> Unit) {
    val ctx = LocalContext.current
    val sp = ctx.getSharedPreferences(Constants.SP, 0)
    val introNeeded by mutableStateOf( sp.getBoolean(Constants.INTRO, true))
    val vm: IntroViewModel = viewModel(factory = AppViewModelProvider.Factory)
    vm.skip = stringResource(R.string.done)
    Column {
        Text(text = stringResource(R.string.setup_complete))
        Text(text = stringResource(R.string.keep_tracking_all_your_expenses_and_incomes_to_save_money_and_reach_your_financial_goals))
    }

    NextSkipButton(onClickActions = {
        StartScreenState(ctx).updateStartingScreen(Screen.BottomNavigationScreens.Transactions.bRoute)
        sp.edit().putBoolean(Constants.INTRO, false).apply()
        navigateToHome()
    })
}