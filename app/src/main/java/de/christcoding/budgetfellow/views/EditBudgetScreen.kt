package de.christcoding.budgetfellow.views

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import de.christcoding.budgetfellow.viewmodels.AppViewModelProvider
import de.christcoding.budgetfellow.viewmodels.BudgetsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditBudgetScreen(navController: NavHostController, padding: PaddingValues, budgetId: String) {
    val vm: BudgetsViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val ctx = LocalContext.current
    vm.updateEditBudgetState(budgetId)
    Scaffold (
        Modifier.padding(padding),
        topBar = {
            CenterAlignedTopAppBar(title = { Text(text = "Edit Budget") },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Back")
                }
                } )
        }
    ){
        Column (Modifier.padding(it)){
            OutlinedTextField(value = vm.editBudgetState.category, onValueChange = {}, readOnly = true)
            OutlinedTextField(value = vm.editBudgetState.amount, onValueChange = {vm.editBudgetState = vm.editBudgetState.copy(amount = it)})
            Row {
                Button(onClick = {
                    vm.deleteBudget(budgetId)
                }) {
                    Text(text = "Delete Budget")
                }
                Button(onClick = {
                    vm.updateBudget(budgetId)
                }) {
                    Text(text = "Save")
                }
            }
        }
        if (vm.rows > 0) {
            Toast.makeText(ctx, "Budget updated", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
        if (vm.deletedRows > 0) {
            navController.popBackStack()
        }
    }
}