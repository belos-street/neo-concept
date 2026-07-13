package com.neoconcept.ui.completion

import com.neoconcept.model.content.BookLessonRef
import com.neoconcept.model.content.VocabularyItem

sealed interface CompletionUiState {
    data object Loading : CompletionUiState

    data class Success(
        val displayNumber: String,
        val title: String,
        val vocabulary: List<VocabularyItem>,
        val nextLesson: BookLessonRef?,
    ) : CompletionUiState

    data class Error(val message: String) : CompletionUiState
}
