package com.neoconcept.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.neoconcept.model.progress.AppProgress
import com.neoconcept.model.progress.LessonProgress
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProgressRepositoryImpl
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
    ) : ProgressRepository {
        private val json =
            Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            }

        private val appProgressKey = stringPreferencesKey("app_progress")

        override suspend fun loadAppProgress(): AppProgress? =
            dataStore.data
                .map { preferences ->
                    preferences[appProgressKey]?.let { raw ->
                        try {
                            json.decodeFromString<AppProgress>(raw)
                        } catch (_: SerializationException) {
                            null
                        }
                    }
                }
                .first()

        override suspend fun saveAppProgress(progress: AppProgress) {
            dataStore.edit { preferences ->
                preferences[appProgressKey] = json.encodeToString(AppProgress.serializer(), progress)
            }
        }

        override suspend fun loadLessonProgress(bookId: String): List<LessonProgress> {
            val key = lessonProgressKey(bookId)
            return dataStore.data
                .map { preferences ->
                    preferences[key]?.let { raw ->
                        try {
                            json.decodeFromString(ListSerializer(LessonProgress.serializer()), raw)
                        } catch (_: SerializationException) {
                            emptyList()
                        }
                    }.orEmpty()
                }
                .first()
        }

        override suspend fun saveLessonProgress(progress: LessonProgress) {
            val bookId = progress.lessonId.substringBefore("-L")
            val key = lessonProgressKey(bookId)
            dataStore.edit { preferences ->
                val existing =
                    preferences[key]?.let { raw ->
                        try {
                            json.decodeFromString<List<LessonProgress>>(raw).toMutableList()
                        } catch (_: SerializationException) {
                            mutableListOf()
                        }
                    } ?: mutableListOf()

                existing.removeAll { it.lessonId == progress.lessonId }
                existing.add(progress)
                preferences[key] = json.encodeToString(ListSerializer(LessonProgress.serializer()), existing)
            }
        }

        private fun lessonProgressKey(bookId: String) = stringPreferencesKey("lesson_progress_$bookId")
    }
