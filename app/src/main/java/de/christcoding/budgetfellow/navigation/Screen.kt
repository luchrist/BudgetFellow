package de.christcoding.budgetfellow.navigation

import androidx.annotation.DrawableRes
import de.christcoding.budgetfellow.R

sealed class Screen(val route: String) {
    object WelcomeAndIncomes : Screen("welcome_incomes_screen")
    object Outcomes : Screen("outcomes_screen")
    object SetBudgets : Screen("set_budgets_screen")
    object CreateBudget : Screen("create_budget_screen")
    object EditBudget : Screen("edit_budget_screen")
    object SetupComplete : Screen("setup_complete_screen")
    object Transactions : Screen("transactions_screen")
    object TransactionDetail : Screen("transaction_detail_screen")
    object BudgetDetail : Screen("budget_detail_screen")

    sealed class BottomNavigationScreens(val bRoute: String, @DrawableRes val icon: Int, val title: String): Screen(bRoute) {
        object Transactions: BottomNavigationScreens("transactions", R.drawable._177548_ecommerce_shop_transaction_icon, "Transactions")
        object Budgets: BottomNavigationScreens("budgets", R.drawable._0735565_arrow_down_tray_icon, "Budgets")
    }
}