package de.christcoding.budgetfellow.ui.components

import android.icu.text.CaseMap.Title
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Title(title: String) {
    Text(text = title, fontSize = 20.sp)
    Spacer(modifier = Modifier.height(8.dp))
}