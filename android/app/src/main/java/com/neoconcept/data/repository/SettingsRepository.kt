package com.neoconcept.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {
    private object Keys {
        val LEARNING_MODE = stringPreferencesKey("learning_mode")
        val TTS_SPEED = floatPreferencesKey("tts_speed")
        val SPEAKING_THRESHOLD = floatPreferencesKey("speaking_threshold")
        val DOWNLOAD_SERVER = stringPreferencesKey("download_server")
    }

    val learningMode: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[Keys.LEARNING_MODE] ?: "linear"
    }

    val ttsSpeed: Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[Keys.TTS_SPEED] ?: 1.0f
    }

    val speakingThreshold: Flow<Float> = context.dataStore.data.map { preferences ->
        preferences[Keys.SPEAKING_THRESHOLD] ?: 0.6f
    }

    val downloadServer: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[Keys.DOWNLOAD_SERVER] ?: ""
    }

    suspend fun setLearningMode(mode: String) {
        context.dataStore.edit { preferences ->
            preferences[Keys.LEARNING_MODE] = mode
        }
    }

    suspend fun setTtsSpeed(speed: Float) {
        context.dataStore.edit { preferences ->
            preferences[Keys.TTS_SPEED] = speed
        }
    }

    suspend fun setSpeakingThreshold(threshold: Float) {
        context.dataStore.edit { preferences ->
            preferences[Keys.SPEAKING_THRESHOLD] = threshold
        }
    }

    suspend fun setDownloadServer(server: String) {
        context.dataStore.edit { preferences ->
            preferences[Keys.DOWNLOAD_SERVER] = server
        }
    }
}
