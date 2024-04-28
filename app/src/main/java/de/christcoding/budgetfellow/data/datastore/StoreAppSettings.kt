package de.christcoding.budgetfellow.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

class StoreAppSettings(private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
        val CYCLE_START_KEY = intPreferencesKey("cycle_start")
        val SMART_CYCLE_KEY = booleanPreferencesKey("smart_cycle")
    }

    val getCycleStart = context.dataStore.data
        .map { settings ->
        settings[CYCLE_START_KEY] ?: 1
    }

    suspend fun updateCycleStart(day: Int) {
        if(day < 1 || day > 28) throw IllegalArgumentException("Day must be between 1 and 28")
        context.dataStore.edit { settings ->
            settings[CYCLE_START_KEY] = day
        }
    }

    val getSmartCycle = context.dataStore.data
        .map { settings ->
        settings[SMART_CYCLE_KEY] ?: false
    }

    suspend fun updateSmartCycle(set: Boolean) {
        context.dataStore.edit { settings ->
            settings[SMART_CYCLE_KEY] = set
        }
    }
}