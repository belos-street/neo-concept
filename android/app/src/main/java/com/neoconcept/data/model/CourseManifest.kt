package com.neoconcept.data.model

import com.google.gson.annotations.SerializedName

data class CourseManifest(
    val version: String,
    val lastUpdated: String,
    val books: List<Book>
)

data class Book(
    val id: String,
    val title: String,
    val description: String,
    val coverUrl: String,
    val units: List<CourseUnit>
)

data class CourseUnit(
    val id: String,
    val title: String,
    val lessons: List<Lesson>
)

data class Lesson(
    val id: String,
    val title: String,
    val description: String,
    val wordCount: Int,
    val status: LessonStatus
)

enum class LessonStatus {
    @SerializedName("available")
    AVAILABLE,
    @SerializedName("downloaded")
    DOWNLOADED,
    @SerializedName("locked")
    LOCKED,
    @SerializedName("update_available")
    UPDATE_AVAILABLE
}
