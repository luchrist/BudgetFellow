package de.christcoding.budgetfellow.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.christcoding.budgetfellow.R
import de.christcoding.budgetfellow.data.models.Budget
import de.christcoding.budgetfellow.data.models.BudgetDetails
import de.christcoding.budgetfellow.utils.StringUtils

@Composable
fun BudgetCard(budget: BudgetDetails) {
    Card(modifier = Modifier.height(100.dp).clip(RoundedCornerShape(8.dp)).padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)) {
        Text(text = StringUtils.getStringOfLength(budget.category.name, 8),
            Modifier
                .fillMaxWidth()
                .padding(4.dp), fontSize = 16.sp, maxLines = 1, )
        Text(text = budget.amount.toString(), modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally), fontSize = 16.sp, maxLines = 1)
        Row (Modifier.fillMaxWidth().padding(4.dp), horizontalArrangement = Arrangement.SpaceBetween){
            Text(text = stringResource(R.string.spent))
            Text(text = budget.spent.toString())
        }
    }
}