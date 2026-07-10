package com.neoconcept.ui.lesson

import com.neoconcept.model.content.Lesson

sealed interface LessonUiState {
    data object Loading : LessonUiState

    data class Success(
        val lesson: Lesson,
        val currentStep: Int,
    ) : LessonUiState

    data class Error(val message: String) : LessonUiState
}
