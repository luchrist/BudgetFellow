package de.christcoding.budgetfellow.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import de.christcoding.budgetfellow.data.models.Category
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CategoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun addACategory(categoryEntity: Category): Long //returns the id of the added row

    @Query("Select * from `category-table`")
    abstract fun getAllCategories(): Flow<List<Category>>

    @Update
    abstract suspend fun updateACategory(categoryEntity: Category): Int // returns the number of rows updated

    @Delete
    abstract suspend fun deleteACategory(categoryEntity: Category): Int

    @Query("Select * from `category-table` where id=:id")
    abstract fun getACategoryById(id:Long): Flow<Category>

    @Query("Select * from `category-table` where expense")
    abstract fun getExpenseCategories(): Flow<List<Category>>

    @Query("Select * from `category-table` where not expense")
    abstract fun getIncomeCategories(): Flow<List<Category>>
}