package de.christcoding.budgetfellow.views

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import de.christcoding.budgetfellow.data.models.Transaction
import de.christcoding.budgetfellow.data.models.TransactionDetails
import de.christcoding.budgetfellow.data.models.groupedByDay
import java.time.LocalDate

@Composable
fun TransactionsList(transactions: List<TransactionDetails>, navController: NavHostController, inIntro: Boolean = false) {
    val groupedTransactions = transactions.groupedByDay()

    LazyColumn{
        items(groupedTransactions.keys.toList(), key = { it.toString() }) { date ->
            TransactionDayGroup(date = date, transactions = groupedTransactions[date]!!.toMutableList(), navController = navController, inIntro = inIntro)
        }
    }
}