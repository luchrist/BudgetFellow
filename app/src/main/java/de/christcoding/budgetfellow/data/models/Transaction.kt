package de.christcoding.budgetfellow.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import de.christcoding.budgetfellow.utils.DateConverter
import java.time.LocalDate

@Entity(tableName="transaction-table")
@TypeConverters(DateConverter::class)
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String="",
    val description:String="",
    val categoryId: Long,
    val amount: Double=0.0,
    val date: LocalDate = LocalDate.now(),
    val recurring: Boolean=false,
    val recurringIntervalUnit: String="",
    val recurringInterval: Int=0,
    val recurringId: String = "",
    val recurringDeleted: Boolean = false
)

fun Transaction.copyWithoutId(date: LocalDate): Transaction {
    return Transaction(
        name = this.name,
        description = this.description,
        categoryId = this.categoryId,
        amount = this.amount,
        date = date,
        recurring = this.recurring,
        recurringIntervalUnit = this.recurringIntervalUnit,
        recurringInterval = this.recurringInterval,
        recurringId = this.recurringId,
        recurringDeleted = this.recurringDeleted
    )
}

fun List<Transaction>.onlyOne(recurringId: String): Boolean {
    return this.filter { it.recurringId == recurringId }.size <= 1
}

fun List<Transaction>.groupedByDay(): Map<LocalDate, List<Transaction>> {
    return this.groupBy { it.date }.toSortedMap(compareByDescending { it })
}

fun List<Transaction>.groupedByCategory(): Map<Long, List<Transaction>> {
    return this.groupBy { it.categoryId }
}

fun List<Transaction>.groupedByMonth(): Map<String, List<Transaction>> {
    return this.groupBy { it.date.month.toString() }
}
