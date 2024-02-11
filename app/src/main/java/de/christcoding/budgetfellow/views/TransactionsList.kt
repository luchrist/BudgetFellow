package de.christcoding.budgetfellow.views

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import de.christcoding.budgetfellow.data.models.Transaction
import de.christcoding.budgetfellow.data.models.TransactionDetails
import de.christcoding.budgetfellow.data.models.groupedByDay
import java.time.LocalDate

@Composable
fun TransactionsList(transactions: List<TransactionDetails>) {
    val groupedTransactions = transactions.groupedByDay()

    LazyColumn{
        items(groupedTransactions.keys.toList(), key = { it.toString() }) { date ->
            TransactionDayGroup(date = date, transactions = groupedTransactions[date]!!)
        }
    }
}