package de.christcoding.budgetfellow.utils

import android.content.Context
import androidx.compose.ui.res.stringResource
import de.christcoding.budgetfellow.R
import java.text.DateFormat
import java.util.Date

class DateUtils {
    companion object {
        fun convertMillisToDate(millis: Long): String {
            val formatter = DateFormat.getDateInstance()
            return formatter.format(Date(millis))
        }

        fun getPluralUnit(context: Context, unit: String, amount: String): String {
            val amountInt = if(amount.isBlank()) 0 else amount.toInt()
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