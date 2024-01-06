package de.christcoding.budgetfellow.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Transaction::class],
    version = 1,
    exportSchema = false
)
abstract class Database : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
}