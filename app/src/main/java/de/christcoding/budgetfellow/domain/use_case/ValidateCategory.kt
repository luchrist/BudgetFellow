package de.christcoding.budgetfellow.domain.use_case

class ValidateCategory {
    fun execute(category: String): ValidationResult {
        return if (category.isBlank()) {
            ValidationResult(false, "Category cannot be empty")
        } else {
            ValidationResult(true)
        }
    }
}