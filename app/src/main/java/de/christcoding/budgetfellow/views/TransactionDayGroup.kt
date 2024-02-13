package de.christcoding.budgetfellow.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import de.christcoding.budgetfellow.data.models.Transaction
import de.christcoding.budgetfellow.data.models.TransactionDetails
import de.christcoding.budgetfellow.ui.theme.DarkGrey
import de.christcoding.budgetfellow.utils.DateUtils
import java.time.LocalDate

@Composable
fun TransactionDayGroup(date: LocalDate, transactions: List<TransactionDetails>) {
    Card (modifier = Modifier.clip(RoundedCornerShape(8.dp)).padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),)
    {
        Column {
            Text(text = DateUtils.formatDay(date), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Divider()
            transactions.forEach { transaction ->
                TransactionItem(transaction = transaction)
            }
        }
    }
}