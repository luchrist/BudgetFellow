package de.christcoding.budgetfellow.views

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
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBudgetScreen(navController: NavHostController) {

    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(title = { Text(text = "Create a Budget") },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) {
                    Icon(imageVector = Icons.Filled.Close, contentDescription = "Back")
                }} )
        }
    ){
        AutoCompleteTextView(vm = )
        OutlinedTextField(value = budgetAmount, onValueChange = )
        Button(onClick = { /*TODO*/ }) {
            Text(text = "Create")
        }
    }
}