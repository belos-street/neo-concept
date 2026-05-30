package com.neoconcept.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.progressDataStore: DataStore<Preferences> by preferencesDataStore(name = "progress")

data class LessonProgress(
    val lessonId: String,
    val currentStep: Int = 0,
    val totalSteps: Int = 6,
    val completedSteps: List<Int> = emptyList(),
    val lastAccessedAt: Long = System.currentTimeMillis(),
    val finished: Boolean = false,
    val timeSpentSeconds: Int = 0
)

class ProgressRepository(private val context: Context) {
    private val gson = Gson()
    private val type = object : TypeToken<Map<String, LessonProgress>>() {}.type

    private object Keys {
        val PROGRESS_MAP = stringPreferencesKey("progress_map")
        val TOTAL_TIME_SPENT = longPreferencesKey("total_time_spent")
        val COMPLETED_LESSONS = intPreferencesKey("completed_lessons")
    }

    val totalTimeSpent: Flow<Long> = context.progressDataStore.data.map { preferences ->
        preferences[Keys.TOTAL_TIME_SPENT] ?: 0L
    }

    val completedLessons: Flow<Int> = context.progressDataStore.data.map { preferences ->
        preferences[Keys.COMPLETED_LESSONS] ?: 0
    }

    val progressMap: Flow<Map<String, LessonProgress>> =
        context.progressDataStore.data.map { preferences ->
            val json = preferences[Keys.PROGRESS_MAP] ?: "{}"
            try {
                gson.fromJson(json, type) ?: emptyMap()
            } catch (e: Exception) {
                emptyMap()
            }
        }

    fun getLessonProgress(lessonId: String): Flow<LessonProgress?> =
        progressMap.map { it[lessonId] }

    suspend fun startLesson(lessonId: String, totalSteps: Int = 6) {
        context.progressDataStore.edit { preferences ->
            val current = loadMap(preferences)
            val updated = current.toMutableMap()
            updated[lessonId] = LessonProgress(
                lessonId = lessonId,
                currentStep = 0,
                totalSteps = totalSteps,
                completedSteps = emptyList(),
                lastAccessedAt = System.currentTimeMillis(),
                finished = false
            )
            preferences[Keys.PROGRESS_MAP] = gson.toJson(updated)
        }
    }

    suspend fun completeStep(lessonId: String, stepIndex: Int) {
        context.progressDataStore.edit { preferences ->
            val current = loadMap(preferences)
            val progress = current[lessonId] ?: return@edit
            if (stepIndex !in progress.completedSteps) {
                val updated = current.toMutableMap()
                updated[lessonId] = progress.copy(
                    completedSteps = progress.completedSteps + stepIndex,
                    currentStep = stepIndex,
                    lastAccessedAt = System.currentTimeMillis()
                )
                preferences[Keys.PROGRESS_MAP] = gson.toJson(updated)
            }
        }
    }

    suspend fun undoCompleteStep(lessonId: String, stepIndex: Int) {
        context.progressDataStore.edit { preferences ->
            val current = loadMap(preferences)
            val progress = current[lessonId] ?: return@edit
            val updated = current.toMutableMap()
            updated[lessonId] = progress.copy(
                completedSteps = progress.completedSteps.filter { it != stepIndex },
                currentStep = stepIndex,
                finished = false,
                lastAccessedAt = System.currentTimeMillis()
            )
            preferences[Keys.PROGRESS_MAP] = gson.toJson(updated)
        }
    }

    suspend fun goToStep(lessonId: String, stepIndex: Int) {
        context.progressDataStore.edit { preferences ->
            val current = loadMap(preferences)
            val progress = current[lessonId] ?: return@edit
            val updated = current.toMutableMap()
            updated[lessonId] = progress.copy(
                currentStep = stepIndex,
                lastAccessedAt = System.currentTimeMillis()
            )
            preferences[Keys.PROGRESS_MAP] = gson.toJson(updated)
        }
    }

    suspend fun finishLesson(lessonId: String) {
        context.progressDataStore.edit { preferences ->
            val current = loadMap(preferences)
            val progress = current[lessonId] ?: return@edit
            val updated = current.toMutableMap()
            updated[lessonId] = progress.copy(
                finished = true,
                lastAccessedAt = System.currentTimeMillis()
            )
            preferences[Keys.PROGRESS_MAP] = gson.toJson(updated)

            preferences[Keys.COMPLETED_LESSONS] = (preferences[Keys.COMPLETED_LESSONS] ?: 0) + 1
        }
    }

    suspend fun resetLesson(lessonId: String) {
        context.progressDataStore.edit { preferences ->
            val current = loadMap(preferences)
            val updated = current.toMutableMap()
            updated.remove(lessonId)
            preferences[Keys.PROGRESS_MAP] = gson.toJson(updated)
        }
    }

    private fun loadMap(preferences: Preferences): Map<String, LessonProgress> {
        val json = preferences[Keys.PROGRESS_MAP] ?: "{}"
        return try {
            gson.fromJson(json, type) ?: emptyMap()
        } catch (e: Exception) {
            emptyMap()
        }
    }
}
