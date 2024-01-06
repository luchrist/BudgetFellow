package de.christcoding.budgetfellow.domain.use_case

data class ValidationResult(val success: Boolean, val error: String? = null)
