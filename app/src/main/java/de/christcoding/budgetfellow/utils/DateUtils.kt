package de.christcoding.budgetfellow.utils

import android.content.Context
import androidx.compose.ui.res.stringResource
import de.christcoding.budgetfellow.R
import java.text.DateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

class DateUtils {
    companion object {

        fun formatDay(date: LocalDate): String {
            val today = LocalDate.now()
            val yesterday = today.minusDays(1)

            return when {
                date.isEqual(today) -> "Today"
                date.isEqual(yesterday) -> "Yesterday"
                date.year != today.year -> date.format(DateTimeFormatter.ofPattern("ddd, dd MMM yyyy"))
                else -> date.format(DateTimeFormatter.ofPattern("E, dd MMM"))
            }
        }


        fun convertStringToDate(date: String): LocalDate {
            return LocalDate.parse(date)
        }
        fun convertMillisToDate(millis: Long): String {
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE
            return Date(millis).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate().format(formatter)
        }

        fun getPluralUnit(context: Context, unit: String, amount: String): String {
            val amountInt = if(amount.isBlank()) 0 else {
                try {
                    amount.toInt()
                } catch (e: NumberFormatException) {
                    0
                }
            }
            return if (amountInt == 1) {
                unit
            } else {
                when (unit) {
                    context.resources.getString(R.string.day) -> context.resources.getString(R.string.days)
                    context.resources.getString(R.string.week) -> context.resources.getString(R.string.weeks)
                    context.resources.getString(R.string.month) -> context.resources.getString(R.string.months)
                    context.resources.getString(R.string.year) -> context.resources.getString(R.string.years)
                    else -> unit
                }
            }
        }
    }
}