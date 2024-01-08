package de.christcoding.budgetfellow.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="transaction-table")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String="",
    val description:String="",
    val amount: Double=0.0,
    val date: String="",
    val recurring: Boolean=false,
    val recurringIntervalUnit: String="",
    val recurringInterval: Int=0,
)
