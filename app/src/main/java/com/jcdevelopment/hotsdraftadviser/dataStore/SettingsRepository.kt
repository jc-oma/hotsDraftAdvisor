package com.jcdevelopment.hotsdraftadviser.dataStore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Extension Property für den Context
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsRepository @Inject constructor(
    private val context: Context
) {
    companion object {
        val LANGUAGE_KEY = stringPreferencesKey("selected_language")
    }

    val languageFlow: Flow<GameSettingLanguageEnum> = context.dataStore.data.map { pref ->
        val code = pref[LANGUAGE_KEY] ?: "en"
        GameSettingLanguageEnum.fromIsoCode(code)
    }

    suspend fun saveLanguage(languageCode: String) {
        context.dataStore.edit { settings ->
            settings[LANGUAGE_KEY] = languageCode
        }
    }
}