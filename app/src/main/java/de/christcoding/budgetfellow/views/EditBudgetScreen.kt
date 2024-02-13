package de.christcoding.budgetfellow.views

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import de.christcoding.budgetfellow.data.models.BudgetDetails

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun EditBudgetScreen(navController: NavHostController, budget: BudgetDetails) {
    var bud: BudgetDetails = budget
    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(title = { Text(text = "Create a Budget") },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Back")
                }
                } )
        }
    ){
        Column {
            OutlinedTextField(value = budget.category.name, onValueChange = {}, readOnly = true)
            OutlinedTextField(value = bud.amount.toString(), onValueChange = {bud = bud.copy(amount = it.toDouble())})
            Row {
                Button(onClick = { /*TODO*/ }) {
                    Text(text = "Delete Budget")
                }
                Button(onClick = { /*TODO*/ }) {
                    Text(text = "Save")
                }
            }
        }
    }
}