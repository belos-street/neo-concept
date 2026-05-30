package com.neoconcept.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.progressDataStore: DataStore<Preferences> by preferencesDataStore(name = "progress")

data class LessonProgress(
    val lessonId: String,
    val step: Int,
    val completed: Boolean,
    val timeSpentSeconds: Int,
    val lastUpdated: Long
)

class ProgressRepository(private val context: Context) {
    private object Keys {
        val LESSON_PROGRESS = stringPreferencesKey("lesson_progress")
        val TOTAL_TIME_SPENT = longPreferencesKey("total_time_spent")
        val COMPLETED_LESSONS = intPreferencesKey("completed_lessons")
    }

    val totalTimeSpent: Flow<Long> = context.progressDataStore.data.map { preferences ->
        preferences[Keys.TOTAL_TIME_SPENT] ?: 0L
    }

    val completedLessons: Flow<Int> = context.progressDataStore.data.map { preferences ->
        preferences[Keys.COMPLETED_LESSONS] ?: 0
    }

    val lessonProgressMap: Flow<Map<String, LessonProgress>> = context.progressDataStore.data.map { preferences ->
        val progressJson = preferences[Keys.LESSON_PROGRESS] ?: "{}"
        parseProgressJson(progressJson)
    }

    suspend fun updateLessonProgress(progress: LessonProgress) {
        context.progressDataStore.edit { preferences ->
            val currentProgress = parseProgressJson(preferences[Keys.LESSON_PROGRESS] ?: "{}")
            val updatedProgress = currentProgress.toMutableMap()
            updatedProgress[progress.lessonId] = progress
            preferences[Keys.LESSON_PROGRESS] = serializeProgressJson(updatedProgress)

            val oldProgress = currentProgress[progress.lessonId]
            val timeDiff = progress.timeSpentSeconds - (oldProgress?.timeSpentSeconds ?: 0)
            if (timeDiff > 0) {
                preferences[Keys.TOTAL_TIME_SPENT] = (preferences[Keys.TOTAL_TIME_SPENT] ?: 0L) + timeDiff
            }

            if (progress.completed && (oldProgress == null || !oldProgress.completed)) {
                preferences[Keys.COMPLETED_LESSONS] = (preferences[Keys.COMPLETED_LESSONS] ?: 0) + 1
            }
        }
    }

    private fun parseProgressJson(json: String): Map<String, LessonProgress> {
        if (json == "{}") return emptyMap()
        return try {
            val map = mutableMapOf<String, LessonProgress>()
            val entries = json.removeSurrounding("{", "}").split("},")
            for (entry in entries) {
                val parts = entry.split(":")
                if (parts.size >= 2) {
                    val lessonId = parts[0].trim().removeSurrounding("\"")
                    val step = parts[1].trim().toIntOrNull() ?: 0
                    val completed = parts.getOrNull(2)?.trim()?.toBooleanStrictOrNull() ?: false
                    val timeSpent = parts.getOrNull(3)?.trim()?.toIntOrNull() ?: 0
                    val lastUpdated = parts.getOrNull(4)?.trim()?.toLongOrNull() ?: 0L
                    map[lessonId] = LessonProgress(lessonId, step, completed, timeSpent, lastUpdated)
                }
            }
            map
        } catch (e: Exception) {
            emptyMap()
        }
    }

    private fun serializeProgressJson(progress: Map<String, LessonProgress>): String {
        if (progress.isEmpty()) return "{}"
        val entries = progress.entries.joinToString(",") { (id, p) ->
            "\"$id\":${p.step},${p.completed},${p.timeSpentSeconds},${p.lastUpdated}"
        }
        return "{$entries}"
    }
}
