package com.neoconcept.model.progress

import kotlinx.serialization.Serializable

@Serializable
enum class LessonStatus {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED,
    SPEAKING_PENDING,
}

@Serializable
data class LessonProgress(
    val lessonId: String,
    val status: LessonStatus,
    val lastStep: Int = 0,
)
