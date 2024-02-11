package de.christcoding.budgetfellow.data.models

import androidx.compose.material3.CardColors
import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="budget-table")
data class Budget(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val categoryId: Long,
    val amount: Double,
    val spent: Double)
