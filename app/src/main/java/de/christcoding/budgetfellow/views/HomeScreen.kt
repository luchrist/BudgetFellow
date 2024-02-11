package de.christcoding.budgetfellow.views

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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

    Scaffold (bottomBar = {
        NavigationBar(Modifier.wrapContentWidth(), containerColor = TopAppBarBackground ) {
              screensInBottom.forEach {
                  val isSelected = currentRoute == it.bRoute
                  val tint = if(isSelected)Color.White else Color.Black
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
                      unselectedContentColor = Color.Black)
              }      
        }
    }){
        Navigation(context = ctx, navController = controller, padding = it)
    }
}