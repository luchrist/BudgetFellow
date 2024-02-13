package de.christcoding.budgetfellow.views

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import de.christcoding.budgetfellow.navigation.Screen
import de.christcoding.budgetfellow.utils.StartScreenState

@Composable
fun BudgetsIntroScreen(navController: NavHostController) {
    val ctx = LocalContext.current
    BudgetsScreen(navController)
    NextSkipButton(onClickActions = {
        navController.navigate(Screen.SetupComplete.route)
        StartScreenState(ctx).updateStartingScreen(Screen.SetupComplete.route)
    })
}