package de.christcoding.budgetfellow.views

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import de.christcoding.budgetfellow.data.models.Transaction
import de.christcoding.budgetfellow.data.models.TransactionDetails
import de.christcoding.budgetfellow.utils.DateUtils
import java.time.LocalDate

@Composable
fun TransactionDayGroup(date: LocalDate, transactions: List<TransactionDetails>) {
    Column {
        Text(text = DateUtils.formatDay(date), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Divider()
        transactions.forEach { transaction ->
            TransactionItem(transaction = transaction)
        }
    }
}