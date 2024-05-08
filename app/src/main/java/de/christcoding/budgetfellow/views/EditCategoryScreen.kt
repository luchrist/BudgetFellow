package de.christcoding.budgetfellow.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import de.christcoding.budgetfellow.viewmodels.AppViewModelProvider
import de.christcoding.budgetfellow.viewmodels.CategoriesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCategoryScreen(navController: NavHostController, categoryId: Long, padding: PaddingValues) {
    val vm: CategoriesViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val category = vm.getCategory(categoryId).collectAsState(initial = null).value
    var categoryName by remember { mutableStateOf(category?.name ?: "") }
    val controller = rememberColorPickerController()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = {
                if (categoryId == 0L) {
                    Text(text = "Add Category")
                } else {
                    Text(text = "Edit Category")
                }
            },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = null)
                    }
                })
        }
    ){
        Column (modifier = Modifier
            .padding(it)
            .padding(bottom = padding.calculateBottomPadding())){
            TextField(value = categoryName , onValueChange = { categoryName = it }, label = { Text("Category Name") })
            HsvColorPicker(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(450.dp)
                    .padding(10.dp),
                initialColor = Color(category?.color ?: 0),
                controller = controller,
                onColorChanged = { colorEnvelope: ColorEnvelope ->
                    // do something
                }
            )
            Button(onClick = {
                vm.saveCategory(categoryId, categoryName)
                if(categoryId != 0L) {
                    navController.popBackStack()
                }
            }) {
                Text("Save")
            }
        }
    }

}