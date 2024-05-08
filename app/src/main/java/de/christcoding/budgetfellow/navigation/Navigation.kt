package de.christcoding.budgetfellow.navigation

import android.content.Context
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import de.christcoding.budgetfellow.utils.StartScreenState
import de.christcoding.budgetfellow.views.AddFirstIncomeScreen
import de.christcoding.budgetfellow.views.AddTransactionScreen
import de.christcoding.budgetfellow.views.BudgetsIntroScreen
import de.christcoding.budgetfellow.views.BudgetsScreen
import de.christcoding.budgetfellow.views.CreateBudgetScreen
import de.christcoding.budgetfellow.views.EditBudgetScreen
import de.christcoding.budgetfellow.views.EditCategoriesScreen
import de.christcoding.budgetfellow.views.EditCategoryScreen
import de.christcoding.budgetfellow.views.EditTransactionScreen
import de.christcoding.budgetfellow.views.HomeScreen
import de.christcoding.budgetfellow.views.OutcomesIntroScreen
import de.christcoding.budgetfellow.views.SettingsScreen
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
        composable(Screen.CreateBudget.route) {
            CreateBudgetScreen(navController = navController, padding)
        }
        composable("${Screen.EditBudget.route}/{budgetId}", arguments = listOf(navArgument("budgetId") { type = NavType.StringType })) {
            EditBudgetScreen(navController = navController, padding, it.arguments?.getString("budgetId") ?: "0")
        }
        composable(Screen.SetupComplete.route) {
            SetupCompleteScreen(navigateToHome = { navController.navigate(Screen.Home.route) })
        }
        composable(Screen.Home.route) {
            HomeScreen()
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController, padding)
        }
        composable(Screen.EditCategories.route) {
            EditCategoriesScreen(navController, padding)
        }
        composable("${Screen.EditCategory.route}/{categoryId}", arguments = listOf(navArgument("categoryId") { type = NavType.LongType })) {
            EditCategoryScreen(navController, it.arguments?.getLong("categoryId") ?: 0, padding)
        }
        composable(Screen.BottomNavigationScreens.Transactions.bRoute) {
            TransactionsScreen(navController, padding)
        }
        composable(Screen.BottomNavigationScreens.Budgets.bRoute) {
            BudgetsScreen(navController, padding)
        }
        composable("${Screen.BottomNavigationScreens.TransactionDetail.bRoute}/{transactionId}", arguments = listOf(navArgument("transactionId") { type = NavType.StringType })) {
            EditTransactionScreen(navController, padding, it.arguments?.getString("transactionId") ?: "0")
        }
        composable("${Screen.BottomNavigationScreens.BudgetDetail.bRoute}/{budgetId}", arguments = listOf(navArgument("budgetId") { type = NavType.StringType })) {
            EditBudgetScreen(navController, padding, it.arguments?.getString("budgetId") ?: "0")
        }
        composable(Screen.BottomNavigationScreens.BudgetCreate.bRoute) {
            CreateBudgetScreen(navController, padding)
        }
        composable("${Screen.BottomNavigationScreens.TransactionAdd.bRoute}/{mode}", arguments = listOf(navArgument("mode") { type = NavType.StringType })) {
            AddTransactionScreen(it.arguments?.getString("mode") ?: "e", navController, padding)
        }
    }
}
