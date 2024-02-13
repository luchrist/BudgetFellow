package de.christcoding.budgetfellow.views

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import de.christcoding.budgetfellow.data.models.Transaction
import de.christcoding.budgetfellow.data.models.TransactionDetails
import de.christcoding.budgetfellow.data.models.groupedByDay
import java.time.LocalDate

@Composable
fun TransactionsList(transactions: List<TransactionDetails>, padding: PaddingValues) {
    val groupedTransactions = transactions.groupedByDay()

    LazyColumn(modifier = Modifier.padding(padding)){
        items(groupedTransactions.keys.toList(), key = { it.toString() }) { date ->
            TransactionDayGroup(date = date, transactions = groupedTransactions[date]!!)
        }
    }
}