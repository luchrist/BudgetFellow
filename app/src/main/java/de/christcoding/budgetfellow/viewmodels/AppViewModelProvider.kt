package de.christcoding.budgetfellow.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import de.christcoding.budgetfellow.BudgetFellowApp

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            BudgetsViewModel(
                budgetFellowApp().container.budgetRepository,
                budgetFellowApp().container.transactionRepository,
                budgetFellowApp().container.categoryRepository,
               )
        }
        initializer {
            IntroViewModel(
                budgetFellowApp().container.transactionRepository,
                budgetFellowApp().container.categoryRepository,
            )
        }
        initializer {
            MainViewModel(
                transactionRepository = budgetFellowApp().container.transactionRepository,
                budgetRepository = budgetFellowApp().container.budgetRepository,
                categoryRepository = budgetFellowApp().container.categoryRepository,
            )
        }
        initializer {
            ApplicationViewModel()
        }

        initializer {
            AddOrEditTransactionViewModel(
                transactionRepository = budgetFellowApp().container.transactionRepository,
                categoryRepository = budgetFellowApp().container.categoryRepository,
            )
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [InventoryApplication].
 */
fun CreationExtras.budgetFellowApp(): BudgetFellowApp =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as BudgetFellowApp)
