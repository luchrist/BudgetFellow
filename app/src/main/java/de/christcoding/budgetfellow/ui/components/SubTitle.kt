package de.christcoding.budgetfellow.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SubTitle(subTitle: String) {
    Text(text = subTitle, fontSize = 20.sp, modifier = Modifier.padding(8.dp))
    Spacer(modifier = Modifier.height(8.dp))
}