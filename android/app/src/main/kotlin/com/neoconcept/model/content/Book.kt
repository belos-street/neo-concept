package com.neoconcept.model.content

import kotlinx.serialization.Serializable

@Serializable
data class Book(
    val id: String,
    val title: String,
    val subtitle: String,
    val order: Int,
    val totalLessons: Int,
    val lessons: List<BookLessonRef>,
)

@Serializable
data class BookLessonRef(
    val id: String,
    val displayNumber: String,
    val title: String,
    val path: String,
    val banner: Banner,
)
