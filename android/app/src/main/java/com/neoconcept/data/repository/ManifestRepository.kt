package com.neoconcept.data.repository

import android.content.Context
import com.google.gson.Gson
import com.neoconcept.data.model.CourseManifest
import com.neoconcept.data.model.Lesson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext

class ManifestRepository(private val context: Context) {

    private val _manifest = MutableStateFlow<CourseManifest?>(null)
    val manifest: StateFlow<CourseManifest?> = _manifest

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val gson = Gson()

    suspend fun loadManifest() = withContext(Dispatchers.IO) {
        _isLoading.value = true
        _error.value = null

        try {
            val json = context.assets.open("mock/manifest.json")
                .bufferedReader()
                .use { it.readText() }

            val manifest = gson.fromJson(json, CourseManifest::class.java)
            _manifest.value = manifest
        } catch (e: Exception) {
            _error.value = "Failed to load manifest: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    fun findLessonById(lessonId: String): Lesson? {
        val currentManifest = _manifest.value ?: return null
        for (book in currentManifest.books) {
            for (unit in book.units) {
                for (lesson in unit.lessons) {
                    if (lesson.id == lessonId) {
                        return lesson
                    }
                }
            }
        }
        return null
    }
}
