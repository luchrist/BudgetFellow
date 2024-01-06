package de.christcoding.budgetfellow.domain

sealed class ValidationEvent {
    object Success : ValidationEvent()
}