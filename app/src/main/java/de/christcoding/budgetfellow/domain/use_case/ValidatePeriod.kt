package de.christcoding.budgetfellow.domain.use_case

class ValidatePeriod {

    fun execute(period: String): ValidationResult {
        if (period.isBlank()) {
            return ValidationResult(false, "Period cannot be empty")
        }
        return try {
            period.toInt()
            ValidationResult(true)
        } catch (e: NumberFormatException) {
            ValidationResult(false, "Period must be a number")
        }
    }
}