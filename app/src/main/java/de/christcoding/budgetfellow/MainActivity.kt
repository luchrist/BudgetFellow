package de.christcoding.budgetfellow

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import de.christcoding.budgetfellow.navigation.Navigation
import de.christcoding.budgetfellow.navigation.Screen
import de.christcoding.budgetfellow.ui.theme.BudgetFellowTheme
import de.christcoding.budgetfellow.utils.Constants
import de.christcoding.budgetfellow.utils.StartScreenState
import de.christcoding.budgetfellow.views.AddFirstIncomeScreen
import de.christcoding.budgetfellow.views.HomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sp = getSharedPreferences(Constants.SP, 0)
        val introNeeded by mutableStateOf( sp.getBoolean(Constants.INTRO, true))
        if(!introNeeded) {
            StartScreenState(this).updateStartingScreen(Screen.BottomNavigationScreens.Transactions.bRoute)
        }
        setContent {
            val context: Context = this
            BudgetFellowTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if(introNeeded) {
                        Navigation(context = context)
                    } else {
                        HomeScreen()
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "id:Nexus One", backgroundColor = 0xFF673AB7)
@Composable
fun GreetingPreview() {
    BudgetFellowTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            //Navigation()
        }
    }
}