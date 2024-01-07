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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import de.christcoding.budgetfellow.navigation.Navigation
import de.christcoding.budgetfellow.ui.theme.BudgetFellowTheme
import de.christcoding.budgetfellow.views.AddFirstIncomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context: Context = this
            BudgetFellowTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation(context)
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