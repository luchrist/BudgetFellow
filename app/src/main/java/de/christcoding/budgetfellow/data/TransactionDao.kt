package de.christcoding.budgetfellow.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
abstract class TransactionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun addATransaction(transactionEntity: Transaction)

    @Query("Select * from `transaction-table`")
    abstract fun getAllTransactions(): Flow<List<Transaction>>

    @Update
    abstract suspend fun updateATransaction(transactionEntity: Transaction)

    @Delete
    abstract suspend fun deleteATransaction(transactionEntity: Transaction)

    @Query("Select * from `transaction-table` where id=:id")
    abstract fun getATransactionById(id:Long): Flow<Transaction>
}