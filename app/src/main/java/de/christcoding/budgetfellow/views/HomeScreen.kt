package de.christcoding.budgetfellow.views

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import de.christcoding.budgetfellow.navigation.Navigation
import de.christcoding.budgetfellow.navigation.Screen
import de.christcoding.budgetfellow.ui.theme.TopAppBarBackground
import de.christcoding.budgetfellow.ui.theme.Unselected

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val controller: NavHostController = rememberNavController()
    val navBackStackEntry by controller.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val ctx = LocalContext.current
    val screensInBottom = listOf(
        Screen.BottomNavigationScreens.Transactions,
        Screen.BottomNavigationScreens.Budgets
    )
    val title = when(currentRoute) {
        Screen.BottomNavigationScreens.Transactions.bRoute -> Screen.BottomNavigationScreens.Transactions.title
        Screen.BottomNavigationScreens.Budgets.bRoute -> Screen.BottomNavigationScreens.Budgets.title
        "${Screen.BottomNavigationScreens.TransactionAdd.bRoute}/{mode}" -> Screen.BottomNavigationScreens.TransactionAdd.title
        Screen.BottomNavigationScreens.BudgetCreate.bRoute -> Screen.BottomNavigationScreens.BudgetCreate.title
        Screen.BottomNavigationScreens.BudgetDetail.bRoute -> Screen.BottomNavigationScreens.BudgetDetail.title
        else -> {
            if (currentRoute?.startsWith(Screen.BottomNavigationScreens.TransactionDetail.bRoute) == true) {
                Screen.BottomNavigationScreens.TransactionDetail.title
            } else {
                ""
            }
        }
    }

    Scaffold (
        topBar = {
            if (title.isNotEmpty()){
                CenterAlignedTopAppBar(title = { Text(text = title) },
                    navigationIcon = {
                        if(currentRoute != Screen.BottomNavigationScreens.Transactions.bRoute &&
                            currentRoute != Screen.BottomNavigationScreens.Budgets.bRoute) {
                            IconButton(onClick = { controller.popBackStack() }) {
                                Icon(Icons.Filled.Close, contentDescription = "Close Details View")
                            }
                        } else {
                            IconButton(onClick = { }){
                                Icon(Icons.Filled.Settings, contentDescription = "Settings")
                            }
                        }
                    })
            }
                 },
        bottomBar = {
        NavigationBar(Modifier.wrapContentWidth(), containerColor = TopAppBarBackground ) {
              screensInBottom.forEach {
                  val isSelected = currentRoute == it.bRoute
                  val tint = if(isSelected)Color.White else Unselected
                  BottomNavigationItem(selected = currentRoute == it.bRoute,
                      onClick = { controller.navigate(it.bRoute) },
                      icon = {
                            Icon(tint = tint,
                                painter = painterResource(id = it.icon),
                                contentDescription = it.title,
                                modifier = Modifier.height(30.dp))
                      },
                      label = {
                          Text(text = it.title, color = tint)
                      },
                      selectedContentColor = Color.White,
                      unselectedContentColor = Unselected)
              }      
        }
    }){
        Navigation(context = ctx, navController = controller, padding = it)
    }
}