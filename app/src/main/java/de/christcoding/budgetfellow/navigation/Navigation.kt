package de.christcoding.budgetfellow.navigation

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.christcoding.budgetfellow.viewmodels.MainViewModel
import de.christcoding.budgetfellow.views.AddFirstIncomeScreen
import de.christcoding.budgetfellow.views.BudgetsIntroScreen
import de.christcoding.budgetfellow.views.OutcomesIntroScreen

@Composable
fun Navigation(context: Context,
               mainViewModel: MainViewModel = MainViewModel(context = context),
               navController: NavHostController = rememberNavController()) {

    val startDestination = mainViewModel.getCurrentStartScreen()

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.WelcomeAndIncomes.route) {
            AddFirstIncomeScreen(mainViewModel, navController)
        }
        composable(Screen.Outcomes.route) {
            OutcomesIntroScreen(mainViewModel, navController)
        }
        composable(Screen.SetBudgets.route) {
            BudgetsIntroScreen(mainViewModel, navController)
        }
    }
}