package de.christcoding.budgetfellow.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.christcoding.budgetfellow.R
import de.christcoding.budgetfellow.data.models.BudgetDetails
import de.christcoding.budgetfellow.utils.StringUtils

@Composable
fun BudgetCard(budget: BudgetDetails) {
    Card(modifier = Modifier
        .height(100.dp)
        .clip(RoundedCornerShape(8.dp))
        .padding(8.dp),
        colors = CardColors(
            containerColor = Color(budget.category.color),
            contentColor = Color.White,
            disabledContentColor = Color.White,
            disabledContainerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(4.dp)) {
        Text(text = StringUtils.getStringOfLength(budget.category.name, 8),
            Modifier
                .fillMaxWidth()
                .padding(top = 4.dp, start = 32.dp), fontSize = 18.sp, maxLines = 1, )
        Text(text = "${budget.amount}€", modifier = Modifier
            .fillMaxWidth()
            .padding(start = 32.dp)
            .align(Alignment.CenterHorizontally), fontSize = 23.sp, maxLines = 1)
        Row (
            Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp), horizontalArrangement = Arrangement.SpaceBetween){
            Text(text = stringResource(R.string.left), modifier = Modifier.padding(start = 32.dp))
            if(budget.amount < budget.spent) {
                Button(onClick = { /*TODO*/ }, enabled = false, colors = ButtonColors(
                    disabledContainerColor = Color.Red,
                    disabledContentColor = Color.White,
                    containerColor = Color.Transparent,
                    contentColor = Color.White
                ), modifier = Modifier.padding(end = 16.dp), contentPadding = PaddingValues(horizontal = 4.dp)
                ) {
                    Text(
                        text = "${(budget.amount - budget.spent)}€",
                    )
                }
            } else {
                Text(
                    text = "${(budget.amount - budget.spent)}€",
                    modifier = Modifier.padding(end = 16.dp)
                )
            }
        }
    }
}