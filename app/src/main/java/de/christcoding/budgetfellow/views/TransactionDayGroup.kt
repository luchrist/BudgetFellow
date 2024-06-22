package de.christcoding.budgetfellow.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import de.christcoding.budgetfellow.data.datastore.StoreAppSettings
import de.christcoding.budgetfellow.data.models.Transaction
import de.christcoding.budgetfellow.data.models.TransactionDetails
import de.christcoding.budgetfellow.utils.DateUtils
import de.christcoding.budgetfellow.viewmodels.AppViewModelProvider
import de.christcoding.budgetfellow.viewmodels.TransactionViewModel
import java.time.LocalDate

@Composable
fun TransactionDayGroup(date: LocalDate, transactions: MutableList<TransactionDetails>, navController: NavHostController, inIntro: Boolean = false) {
    val vm: TransactionViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val ctx = LocalContext.current
    val dataStore = StoreAppSettings(ctx)
    val start by dataStore.getCycleStart.collectAsState(1)
    val smart by dataStore.getSmartCycle.collectAsState(false)

    vm.cycleStart = start
    vm.smartCycle = smart
    Card (modifier = Modifier
        .clip(RoundedCornerShape(8.dp))
        .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp),)
    {
        Column {
            Text(text = DateUtils.formatDay(date), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            transactions.forEach { transaction ->
                Divider()
                SwipeToDeleteContainer(item = transaction, onDismiss = {
                    transactions.remove(transaction)
                    vm.deleteTransaction(Transaction(
                        id = transaction.id,
                        name = transaction.name,
                        amount = transaction.amount,
                        categoryId = transaction.category.id,
                        date = transaction.date,
                        description = transaction.description,
                        recurringIntervalUnit = transaction.recurringIntervalUnit,
                        recurringInterval = transaction.recurringInterval,
                        recurring = transaction.recurring,
                        recurringDeleted = transaction.recurringDeleted
                    ))
                }) {
                    TransactionItem(transaction = transaction, navController = navController, inIntro)
                }
            }
        }
    }
}