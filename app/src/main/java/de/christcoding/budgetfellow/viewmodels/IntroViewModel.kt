package de.christcoding.budgetfellow.viewmodels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import de.christcoding.budgetfellow.R
import de.christcoding.budgetfellow.TransactionMode
import de.christcoding.budgetfellow.utils.Constants

class IntroViewModel(
    private val context: Context
): AddOrEditTransactionViewModel(context = context) {

    var skip by mutableStateOf(context.getString(R.string.skip))
    var firstIncome by mutableStateOf(true)

    fun introFinished() {
        context.getSharedPreferences(Constants.SP, 0)
            .edit()
            .putBoolean(Constants.INTRO, false)
            .apply()
    }

    override fun handleSubmit(mode: TransactionMode) {
        super.handleSubmit(mode)
        firstIncome = false
        skip = context.getString(R.string.next)
    }
}