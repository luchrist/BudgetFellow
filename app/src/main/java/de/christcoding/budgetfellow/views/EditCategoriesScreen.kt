package de.christcoding.budgetfellow.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import de.christcoding.budgetfellow.data.models.Category
import de.christcoding.budgetfellow.navigation.Screen
import de.christcoding.budgetfellow.ui.theme.Shapes
import de.christcoding.budgetfellow.viewmodels.AppViewModelProvider
import de.christcoding.budgetfellow.viewmodels.CategoriesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCategoriesScreen(navController: NavHostController, padding: PaddingValues) {
    val vm: CategoriesViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val categories: List<Category> = vm.categories.collectAsState(initial = emptyList()).value

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(text = "Categories") }, navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = null)
                }
            })
        }
    ) {
        LazyColumn(modifier = Modifier.padding(it).padding(bottom = padding.calculateBottomPadding())) {
            items(categories) { category ->
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp), onClick = {navController.navigate("${Screen.EditCategory.route}/${category.id}")}) {
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = category.name, fontSize = 20.sp, modifier = Modifier.padding(8.dp), textAlign = TextAlign.Center)
                        Button(onClick = { /*TODO*/ }, colors = ButtonColors( containerColor = Color(category.color), contentColor = Color(category.color), disabledContentColor = Color(category.color), disabledContainerColor = Color(category.color)), shape = Shapes.medium) {}
                    }
                }
            }
        }
    }
}