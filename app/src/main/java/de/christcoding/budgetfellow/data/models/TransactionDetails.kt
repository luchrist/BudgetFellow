package de.christcoding.budgetfellow.data.models

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import java.time.LocalDate

data class TransactionDetails(
    val id: Long = 0L,
    val name: String="",
    val description:String="",
    val category: Category = Category(name = "Uncategorized", color =  Color.Gray.toArgb(), expense = true),
    val amount: Double=0.0,
    val date: LocalDate = LocalDate.now(),
    val recurring: Boolean=false,
    val recurringIntervalUnit: String="",
    val recurringInterval: Int=0,
    val recurringId: String = "",
    val recurringDeleted: Boolean = false
)

fun List<TransactionDetails>.groupedByDay(): Map<LocalDate, List<TransactionDetails>> {
    return this.groupBy { it.date }.toSortedMap(compareByDescending { it })
}