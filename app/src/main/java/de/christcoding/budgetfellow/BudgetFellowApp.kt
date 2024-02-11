package de.christcoding.budgetfellow

import android.app.Application
import de.christcoding.budgetfellow.data.AppContainer
import de.christcoding.budgetfellow.data.AppDataContainer
import de.christcoding.budgetfellow.data.Graph

class BudgetFellowApp: Application() {

    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        Graph.provide(this)
        container = AppDataContainer(this)
    }
}