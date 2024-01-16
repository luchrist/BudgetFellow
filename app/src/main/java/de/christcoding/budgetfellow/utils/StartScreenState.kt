package de.christcoding.budgetfellow.utils

import android.content.Context
import de.christcoding.budgetfellow.navigation.Screen

class StartScreenState(ctx: Context) {

    val sp = ctx.getSharedPreferences(Constants.SP, 0)
    fun getCurrentStartScreen(): String {
        return sp.getString(Constants.SCREEN, Screen.WelcomeAndIncomes.route)
            ?: Screen.WelcomeAndIncomes.route
    }

    fun updateStartingScreen(route: String) {
        sp.edit().putString(Constants.SCREEN, route).apply()
    }
}