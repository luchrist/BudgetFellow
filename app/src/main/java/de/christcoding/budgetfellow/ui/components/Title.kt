package de.christcoding.budgetfellow.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Title(title: String) {
    Text(text = title, fontSize = 24.sp, modifier = Modifier.padding(8.dp).fillMaxWidth(), textAlign = TextAlign.Center)
    Spacer(modifier = Modifier.height(8.dp))
}