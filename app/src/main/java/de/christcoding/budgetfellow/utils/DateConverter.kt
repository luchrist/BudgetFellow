package de.christcoding.budgetfellow.utils

import androidx.room.TypeConverter
import java.time.LocalDate


object DateConverter {
    @TypeConverter
    fun toLocalDate(dateLong: Long): LocalDate {
        return LocalDate.ofEpochDay(dateLong)
    }


    @TypeConverter
    fun fromLocalDate(date: LocalDate): Long {
        return date.toEpochDay()
    }
}