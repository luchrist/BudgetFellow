package de.christcoding.budgetfellow.views

import AutoCompleteTextView
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import de.christcoding.budgetfellow.AddTransactionEvent
import de.christcoding.budgetfellow.R
import de.christcoding.budgetfellow.viewmodels.AppViewModelProvider
import de.christcoding.budgetfellow.viewmodels.BudgetsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBudgetScreen(navController: NavHostController, padding: PaddingValues) {
    val ctx = LocalContext.current
    val vm: BudgetsViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val budgetState = vm.createBudgetState
    Scaffold (
        Modifier.padding(padding),
        topBar = {
            CenterAlignedTopAppBar(title = { Text(text = "Create a Budget") },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Back")
                }} )
        }
    ){
        Column (Modifier.padding(it)){
            AutoCompleteTextView(elements = vm.expCategories.map { it.name}, currentElement = budgetState.category, onElementChanged = {
                vm.createBudgetState = budgetState.copy(category = it)
                vm.checkIfEnough()
            }, title = stringResource(R.string.category), error = budgetState.catError)
            OutlinedTextField(value = budgetState.amount, onValueChange = {
                vm.createBudgetState = budgetState.copy(amount = it)
                vm.checkIfEnough()
            },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Number
                ),
                isError = budgetState.amountError != null,
                label = { Text(text = stringResource(R.string.amount)) }
            )
            Button(onClick = {vm.saveBudget()}) {
                Text(text = "Create")
            }
        }
        if(vm.id > -1) {
            Toast.makeText(ctx, "Budget created", Toast.LENGTH_SHORT).show()
        }
    }
}