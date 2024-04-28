package de.christcoding.budgetfellow.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

open class ApplicationViewModel: ViewModel() {
    var currency by mutableStateOf("€")
}