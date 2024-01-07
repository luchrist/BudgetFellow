package de.christcoding.budgetfellow.data

import android.content.Context
import androidx.room.Room
import de.christcoding.budgetfellow.utils.Constants

object Graph {
    lateinit var database: Database

    val transactionRepository by lazy{
        TransactionRepository(transactionDao = database.transactionDao())
    }

    fun provide(context: Context) {
        database = Room.databaseBuilder(context, Database::class.java, Constants.DATABASE_NAME).build()
    }
}