package de.christcoding.budgetfellow.data.models

import androidx.compose.ui.graphics.Color

data class Category(
    val name: String,
    val color: Color,
    val expense: Boolean
)