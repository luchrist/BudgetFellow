package de.christcoding.budgetfellow.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.christcoding.budgetfellow.viewmodels.AppViewModelProvider
import de.christcoding.budgetfellow.viewmodels.IntroViewModel

@Composable
fun NextSkipButton(onClickActions: () -> Unit, skipText: String) {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.padding(8.dp)
    ) {
        OutlinedButton(
            onClick = {
                onClickActions()
            },
        ) {
            Icon(Icons.Default.ArrowForward, contentDescription = "next")
            Text(text = skipText)
        }
    }
}