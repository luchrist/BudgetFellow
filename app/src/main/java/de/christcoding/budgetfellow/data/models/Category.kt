package de.christcoding.budgetfellow.data.models

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category-table")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String = "",
    val color: Color = Color.Unspecified,
    val expense: Boolean = true
): Comparable<Category> {
    override fun compareTo(other: Category): Int {
        return name.compareTo(other.name)
    }
}