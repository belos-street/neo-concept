package com.neoconcept.ui.home

import com.neoconcept.model.content.IndexedBook

data class ContinueProgress(
    val bookId: String,
    val lessonId: String,
    val step: Int,
    val bookTitle: String,
    val lessonTitle: String,
    val stepLabel: String,
    val totalLessons: Int,
)

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Success(
        val books: List<IndexedBook>,
        val hasCorruptedEntries: Boolean,
        val continueProgress: ContinueProgress?,
    ) : HomeUiState

    data class Error(val message: String) : HomeUiState
}
