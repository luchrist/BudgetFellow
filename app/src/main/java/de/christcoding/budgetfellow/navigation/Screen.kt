package de.christcoding.budgetfellow.navigation

sealed class Screen(val route: String) {
    object WelcomeAndIncomes : Screen("welcome_incomes_screen")
    object Outcomes : Screen("add_screen")
    object SetBudgets : Screen("edit_screen")
    object SetupComplete : Screen("detail_screen")
    object Transactions : Screen("transactions_screen")
    object TransactionDetail : Screen("transaction_detail_screen")
    object Budgets : Screen("budgets_screen")
    object BudgetDetail : Screen("budget_detail_screen")
}