package de.christcoding.budgetfellow.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.christcoding.budgetfellow.R
import de.christcoding.budgetfellow.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropDown(mainViewModel: MainViewModel) {
    var expanded by remember {
        mutableStateOf(false)
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = mainViewModel.selectedCategory,
            onValueChange = {
                mainViewModel.selectedCategory = it
            },
            readOnly = true,
            leadingIcon = {
                Image(
                    painter = painterResource(id = R.drawable.baseline_category_24),
                    contentDescription = null
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded,

                )
            },
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.menuAnchor()
        )
        Box {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }) {
                mainViewModel.categories.forEach { category ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = category
                            )
                        },
                        onClick = {
                            mainViewModel.selectedCategory = category
                            expanded = false
                        })
                }
            }
        }
    }

}