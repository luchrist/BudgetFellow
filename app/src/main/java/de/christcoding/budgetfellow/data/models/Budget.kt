package de.christcoding.budgetfellow.data.models

import androidx.compose.material3.CardColors
import androidx.compose.ui.graphics.Color

data class Budget(val category: String, val amount: Double, val spent: Double, val colors: CardColors)
