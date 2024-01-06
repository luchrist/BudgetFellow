package de.christcoding.budgetfellow.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.christcoding.budgetfellow.viewmodels.MainViewModel
import de.christcoding.budgetfellow.views.AddFirstIncomeScreen
import de.christcoding.budgetfellow.views.OutcomesIntroScreen

@Composable
fun Navigation(mainViewModel: MainViewModel = viewModel(),
               navController: NavHostController = rememberNavController()) {

    NavHost(navController = navController, startDestination = Screen.WelcomeAndIncomes.route) {
        composable(Screen.WelcomeAndIncomes.route) {
            AddFirstIncomeScreen(mainViewModel, navController)
        }
        composable(Screen.Outcomes.route) {
            OutcomesIntroScreen()
        }
    }
}