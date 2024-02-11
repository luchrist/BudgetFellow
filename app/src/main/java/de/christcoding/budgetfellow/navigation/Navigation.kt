package de.christcoding.budgetfellow.navigation

import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.christcoding.budgetfellow.utils.StartScreenState
import de.christcoding.budgetfellow.viewmodels.MainViewModel
import de.christcoding.budgetfellow.views.AddFirstIncomeScreen
import de.christcoding.budgetfellow.views.BudgetsIntroScreen
import de.christcoding.budgetfellow.views.BudgetsScreen
import de.christcoding.budgetfellow.views.OutcomesIntroScreen
import de.christcoding.budgetfellow.views.SetupCompleteScreen
import de.christcoding.budgetfellow.views.TransactionsScreen

@Composable
fun Navigation(context: Context,
               navController: NavHostController = rememberNavController(),
               padding: PaddingValues = PaddingValues(0.dp)) {

    val startDestination = StartScreenState(context).getCurrentStartScreen()

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.WelcomeAndIncomes.route) {
            AddFirstIncomeScreen(navController)
        }
        composable(Screen.Outcomes.route) {
            OutcomesIntroScreen(navController)
        }
        composable(Screen.SetBudgets.route) {
            BudgetsIntroScreen(navController)
        }
        composable(Screen.SetupComplete.route) {
            SetupCompleteScreen(navigateToHome = { navController.navigate(Screen.Transactions.route) })
        }
        composable(Screen.BottomNavigationScreens.Transactions.bRoute) {
            TransactionsScreen()
        }
        composable(Screen.BottomNavigationScreens.Budgets.bRoute) {
            BudgetsScreen()
        }
    }
}