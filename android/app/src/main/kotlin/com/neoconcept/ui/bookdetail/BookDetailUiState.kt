package com.neoconcept.ui.bookdetail

import com.neoconcept.model.content.Book
import com.neoconcept.model.progress.LessonStatus

data class LessonRow(
    val lessonId: String,
    val displayNumber: String,
    val title: String,
    val subtitle: String,
    val status: LessonStatus,
)

data class LessonGroup(
    val startIndex: Int,
    val endIndex: Int,
    val lessons: List<LessonRow>,
)

sealed interface BookDetailUiState {
    data object Loading : BookDetailUiState

    data class Success(
        val book: Book,
        val groups: List<LessonGroup>,
    ) : BookDetailUiState

    data class Error(val message: String) : BookDetailUiState
}
