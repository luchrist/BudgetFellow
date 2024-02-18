package de.christcoding.budgetfellow.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import de.christcoding.budgetfellow.data.models.TransactionDetails
import de.christcoding.budgetfellow.navigation.Screen
import de.christcoding.budgetfellow.ui.theme.CardBackground
import de.christcoding.budgetfellow.ui.theme.Shapes
import de.christcoding.budgetfellow.viewmodels.AppViewModelProvider
import de.christcoding.budgetfellow.viewmodels.ApplicationViewModel
import java.text.DecimalFormat

@Composable
fun TransactionItem(transaction: TransactionDetails, navController: NavHostController) {

    val appViewModel: ApplicationViewModel = viewModel(factory = AppViewModelProvider.Factory)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceBright)
            .padding(8.dp)
            .clickable(onClick = {navController.navigate("${Screen.BottomNavigationScreens.TransactionDetail.route}/${transaction.id}")})
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = transaction.name, style = MaterialTheme.typography.headlineMedium)
            Text(text = "${DecimalFormat("0.#").format(transaction.amount)} ${appViewModel.currency}", style = MaterialTheme.typography.headlineMedium)
        }
        Spacer(modifier = Modifier.padding(4.dp))
        Surface(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            shape = Shapes.medium,
            color = Color(transaction.category.color).copy(alpha = 0.15f)
            ) {
            Text(transaction.category.name, style = MaterialTheme.typography.bodySmall, color = Color( transaction.category.color).copy(alpha = 1f))
        }
    }
}