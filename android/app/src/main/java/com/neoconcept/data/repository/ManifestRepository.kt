package com.neoconcept.data.repository

import android.content.Context
import com.neoconcept.data.model.CourseManifest
import com.neoconcept.data.model.Lesson
import com.neoconcept.data.model.LessonStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import org.yaml.snakeyaml.Yaml

class ManifestRepository(private val context: Context) {

    private val _manifest = MutableStateFlow<CourseManifest?>(null)
    val manifest: StateFlow<CourseManifest?> = _manifest

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val yaml = Yaml()

    suspend fun loadManifest() = withContext(Dispatchers.IO) {
        _isLoading.value = true
        _error.value = null

        try {
            val inputStream = context.assets.open("mock/manifest.yaml")
            val data: Map<String, Any> = yaml.load(inputStream)
            val manifest = parseManifest(data)
            _manifest.value = manifest
        } catch (e: Exception) {
            _error.value = "Failed to load manifest: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseManifest(data: Map<String, Any>): CourseManifest {
        val books = (data["books"] as List<Map<String, Any>>?)?.map { book ->
            val units = (book["units"] as List<Map<String, Any>>?)?.map { unit ->
                val lessons = (unit["lessons"] as List<Map<String, Any>>?)?.map { lesson ->
                    Lesson(
                        id = lesson["id"] as String,
                        title = lesson["title"] as String,
                        description = lesson["description"] as String? ?: "",
                        wordCount = (lesson["wordCount"] as Number?)?.toInt() ?: 0,
                        status = parseLessonStatus(lesson["status"] as String?)
                    )
                } ?: emptyList()
                com.neoconcept.data.model.CourseUnit(
                    id = unit["id"] as String,
                    title = unit["title"] as String,
                    lessons = lessons
                )
            } ?: emptyList()
            com.neoconcept.data.model.Book(
                id = book["id"] as String,
                title = book["title"] as String,
                description = book["description"] as String? ?: "",
                coverUrl = book["coverUrl"] as String? ?: "",
                units = units
            )
        } ?: emptyList()

        return CourseManifest(
            version = data["version"] as String? ?: "1.0.0",
            lastUpdated = data["lastUpdated"] as String? ?: "",
            books = books
        )
    }

    private fun parseLessonStatus(status: String?): LessonStatus {
        return when (status) {
            "available" -> LessonStatus.AVAILABLE
            "downloaded" -> LessonStatus.DOWNLOADED
            "locked" -> LessonStatus.LOCKED
            "update_available" -> LessonStatus.UPDATE_AVAILABLE
            else -> LessonStatus.AVAILABLE
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
