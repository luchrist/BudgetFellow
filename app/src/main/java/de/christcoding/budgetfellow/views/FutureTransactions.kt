package de.christcoding.budgetfellow.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import de.christcoding.budgetfellow.data.models.TransactionDetails

@Composable
fun FutureTransactions(transactions: List<TransactionDetails>, navController: NavHostController) {
    var visible by remember {
        mutableStateOf(false)
    }
    if(transactions.isNotEmpty()) {
        Card (modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .padding(8.dp),
            elevation = CardDefaults.cardElevation(4.dp),)
        {
            Column {
                Text(
                    text = "Pending",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { visible = !visible }) {
                            if (visible) {
                                Icon(Icons.Filled.KeyboardArrowUp, contentDescription = null )
                            } else {
                                Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null )
                            }
                        }
                        Text(
                            textAlign = TextAlign.Center,
                            text = "${transactions.size} Transactions",
                        )
                    }
                    Text(text = "${transactions.sumOf { it.amount }} €")
                }
                if (visible) {
                    TransactionsList(transactions = transactions, navController = navController)
                }
            }
        }
    }
}