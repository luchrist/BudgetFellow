package de.christcoding.budgetfellow.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import de.christcoding.budgetfellow.data.models.Budget
import kotlinx.coroutines.flow.Flow

@Dao
abstract class BudgetDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun addABudget(budgetEntity: Budget): Long //returns the id of the added row

    @Query("Select * from `budget-table`")
    abstract fun getAllBudgets(): Flow<List<Budget>>

    @Update
    abstract suspend fun updateABudget(budgetEntity: Budget): Int // returns the number of rows updated

    @Delete
    abstract suspend fun deleteABudget(budgetEntity: Budget): Int

    @Query("Select * from `budget-table` where id=:id")
    abstract fun getABudgetById(id:Long): Flow<Budget>
}