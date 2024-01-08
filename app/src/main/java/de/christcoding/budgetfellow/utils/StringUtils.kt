package de.christcoding.budgetfellow.utils

class StringUtils {

    companion object {
        fun getStringOfLength(string: String, length: Int): String {
            return if (string.trim().length > length) {
                string.trim().substring(0, length-2) + "..."
            } else {
                string.trim()
            }
        }
    }
}