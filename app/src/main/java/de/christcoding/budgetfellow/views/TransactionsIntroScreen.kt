package de.christcoding.budgetfellow.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import de.christcoding.budgetfellow.R
import de.christcoding.budgetfellow.navigation.Screen
import de.christcoding.budgetfellow.ui.components.SubTitle
import de.christcoding.budgetfellow.ui.components.Title
import de.christcoding.budgetfellow.utils.StartScreenState

@Composable
fun TransactionsIntroScreen(navController: NavHostController) {
    val ctx = LocalContext.current
    Column {
        Title(title = "YOUR TRANSACTIONS")
        SubTitle(subTitle = "Here you have an Overview of your transactions and can always edit them or add new ones later on.")
        Row (Modifier.weight(1f)){
            TransactionsScreen(navController = navController, padding = PaddingValues(), inIntro = true)
        }
        Column (Modifier.fillMaxWidth(), horizontalAlignment = Alignment.End){
            NextSkipButton(onClickActions = {
                navController.navigate(Screen.SetBudgets.route)
                StartScreenState(ctx).updateStartingScreen(Screen.SetBudgets.route)
            }, stringResource(id = R.string.contin))
        }
    }
}