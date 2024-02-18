package de.christcoding.budgetfellow.views

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import de.christcoding.budgetfellow.viewmodels.AppViewModelProvider
import de.christcoding.budgetfellow.viewmodels.BudgetsViewModel
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

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
        Column (
            Modifier
                .padding(it)
                .padding(start = 16.dp)){
            OutlinedTextField(value = vm.editBudgetState.category, onValueChange = {}, readOnly = true, shape = RoundedCornerShape(16.dp))
            Spacer(modifier = Modifier.padding(8.dp))
            OutlinedTextField(
                value = vm.editBudgetState.amount,
                onValueChange = {vm.editBudgetState = vm.editBudgetState.copy(amount = it)},
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Number
                )
            )
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
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