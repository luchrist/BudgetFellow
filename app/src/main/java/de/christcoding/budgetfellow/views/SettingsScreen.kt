package de.christcoding.budgetfellow.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import de.christcoding.budgetfellow.data.datastore.StoreAppSettings
import de.christcoding.budgetfellow.viewmodels.AppViewModelProvider
import de.christcoding.budgetfellow.viewmodels.ApplicationViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navHostController: NavHostController) {
    var expanded by remember {
        mutableStateOf(false)
    }
    val vm: ApplicationViewModel = viewModel(factory = AppViewModelProvider.Factory)
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val dataStore = StoreAppSettings(ctx)
    val cycleStart by dataStore.getCycleStart.collectAsState(1)
    val smartCycle by dataStore.getSmartCycle.collectAsState(true)

   Scaffold(
       topBar = { CenterAlignedTopAppBar(title = { Text(text = "Settings")}, navigationIcon = {
           IconButton(onClick = {navHostController.popBackStack()}) {
               Icon(Icons.Default.Close, contentDescription = null)
           }
       }) }
   ) {
       Row(
           Modifier
               .fillMaxWidth()
               .padding(it)
               .padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
           Text(text = "Cycle Start")
           ExposedDropdownMenuBox(
               expanded = expanded,
               onExpandedChange = { expanded = it }) {
               OutlinedTextField(
                   value = "${cycleStart} of month",
                   onValueChange = {},
                   readOnly = true,
                   leadingIcon = {
                       Icon(Icons.Default.DateRange, contentDescription = null)
                   },
                   trailingIcon = {
                       ExposedDropdownMenuDefaults.TrailingIcon(
                           expanded = expanded
                       )
                   },
                   shape = RoundedCornerShape(16.dp),
                   //modifier = Modifier.menuAnchor()
               )
               Box {
                   ExposedDropdownMenu(
                       expanded = expanded,
                       onDismissRequest = { expanded = false }) {
                       for (i in 1..29) {
                           DropdownMenuItem(onClick = {
                               expanded = !expanded
                               scope.launch {
                                   dataStore.updateCycleStart(i)
                               }
                           }) {
                               (Text(text = "$i of month"))
                           }
                       }
                   }
               }
           }
           Checkbox(checked = smartCycle, onCheckedChange = { scope.launch {
                dataStore.updateSmartCycle(it)
            }
           })
       }
   }
}