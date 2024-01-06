package de.christcoding.budgetfellow

import android.app.Application
import de.christcoding.budgetfellow.data.Graph

class BudgetFellowApp: Application() {
    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
    }
}