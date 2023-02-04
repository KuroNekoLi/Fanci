package com.cmoney.kolfanci.model.persistence

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "fanci_preferences"
)

class SettingsDataStore(private val preference_datastore: DataStore<Preferences>) {
    private val IS_TUTORIAL = booleanPreferencesKey("is_tutorial")  //是否新手導引過

    val isTutorial: Flow<Boolean> = preference_datastore.data.map {
        it[IS_TUTORIAL] ?: false
    }

    suspend fun onTutorialOpen() {
        preference_datastore.edit { preferences ->
            preferences[IS_TUTORIAL] = true
        }
    }
}