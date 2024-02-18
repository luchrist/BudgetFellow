package de.christcoding.budgetfellow.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import de.christcoding.budgetfellow.utils.Constants
import de.christcoding.budgetfellow.utils.StartScreenState
import de.christcoding.budgetfellow.viewmodels.AppViewModelProvider
import de.christcoding.budgetfellow.viewmodels.IntroViewModel

@Composable
fun AddFirstIncomeScreen(navController: NavHostController) {
    val ctx = LocalContext.current
    val sp = ctx.getSharedPreferences(Constants.SP, 0)
    val firstOpening by mutableStateOf( sp.getBoolean(Constants.FIRST_OPENING, true))
    val vm: IntroViewModel = viewModel(factory = AppViewModelProvider.Factory)
    if(firstOpening) {
        vm.saveInitialCategoriesInDB()
        sp.edit().putBoolean(Constants.FIRST_OPENING, false).apply()
    }
    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        if (vm.firstIncome) {
            Text(
                text = ctx.getString(R.string.hi_i_am_your_budget_fellow),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(R.string.here_to_help_you_reach_your_financial_goals),
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(6.dp))
            Divider()
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = ctx.getString(R.string.let_s_start_by_adding_your_first_income),
                style = MaterialTheme.typography.titleSmall
            )
        } else {
            Text(
                text = ctx.getString(R.string.do_you_want_to_add_another_income),
                style = MaterialTheme.typography.titleMedium
            )
        }
        AddEditIncomeOrExpense(TransactionMode.IncomeAdd)
    }
    NextSkipButton(onClickActions = {
        navController.navigate(Screen.Outcomes.route)
        StartScreenState(ctx).updateStartingScreen(Screen.Outcomes.route)
    })
}
