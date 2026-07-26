package com.anuragdeshpande.printhive.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "printhive_server_prefs")

class ServerPreferencesRepository(private val context: Context) {

    private object Keys {
        val SERVER_URL = stringPreferencesKey("server_url")
        val API_KEY = stringPreferencesKey("api_key")
        val IS_PAIRED = booleanPreferencesKey("is_paired")
    }

    val serverUrlFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.SERVER_URL] ?: DEFAULT_SERVER_URL
    }

    val apiKeyFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[Keys.API_KEY] ?: ""
    }

    val isPairedFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[Keys.IS_PAIRED] ?: false
    }

    suspend fun saveServerUrl(url: String) {
        val normalized = url.trim().removeSuffix("/")
        context.dataStore.edit { prefs ->
            prefs[Keys.SERVER_URL] = normalized
        }
    }

    suspend fun saveApiKey(apiKey: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.API_KEY] = apiKey.trim()
        }
    }

    suspend fun setPaired(paired: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.IS_PAIRED] = paired
        }
    }

    companion object {
        const val DEFAULT_SERVER_URL = "http://192.168.1.102:8000"
    }
}
