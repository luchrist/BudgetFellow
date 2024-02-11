package de.christcoding.budgetfellow.data.models

import android.os.Parcelable
import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import de.christcoding.budgetfellow.domain.use_case.ValidateCategory
import de.christcoding.budgetfellow.utils.DateConverter
import kotlinx.parcelize.Parcelize
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
)

fun List<Transaction>.groupedByDay(): Map<LocalDate, List<Transaction>> {
    return this.groupBy { it.date }.toSortedMap(compareByDescending { it })
}

fun List<Transaction>.groupedByCategory(): Map<Long, List<Transaction>> {
    return this.groupBy { it.categoryId }
}

fun List<Transaction>.groupedByMonth(): Map<String, List<Transaction>> {
    return this.groupBy { it.date.month.toString() }
}
