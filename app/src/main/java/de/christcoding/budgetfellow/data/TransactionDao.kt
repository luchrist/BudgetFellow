package de.christcoding.budgetfellow.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import de.christcoding.budgetfellow.data.models.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
abstract class TransactionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun addATransaction(transactionEntity: Transaction): Long //returns the id of the added row

    @Query("Select * from `transaction-table`")
    abstract fun getAllTransactions(): Flow<List<Transaction>>

    @Update
    abstract suspend fun updateATransaction(transactionEntity: Transaction): Int // returns the number of rows updated

    @Delete
    abstract suspend fun deleteATransaction(transactionEntity: Transaction): Int

    @Query("Select * from `transaction-table` where id=:id")
    abstract fun getATransactionById(id:Long): Flow<Transaction>

    @Query("Select * from `transaction-table` where recurring")
    abstract fun getRecurringTransactions(): Flow<List<Transaction>>

    @Query("Select * from `transaction-table` where date between :start and :end")
    abstract fun getTransactionsBetween(start: Long, end: Long): Flow<List<Transaction>>
}