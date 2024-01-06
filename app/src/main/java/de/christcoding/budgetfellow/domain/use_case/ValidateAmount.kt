package de.christcoding.budgetfellow.domain.use_case

class ValidateAmount {
    fun execute(amount: String): ValidationResult {
        if (amount.isBlank()) {
            return ValidationResult(false, "Amount cannot be empty")
        }
        return try {
            amount.toDouble()
            ValidationResult(true)
        } catch (e: NumberFormatException) {
            ValidationResult(false, "Amount must be a number")
        }
    }
}