package com.neoconcept.model.progress

import kotlinx.serialization.Serializable

@Serializable
data class AppProgress(
    val bookId: String,
    val lessonId: String,
    val step: Int,
)
