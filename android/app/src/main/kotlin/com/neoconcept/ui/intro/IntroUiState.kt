package com.neoconcept.ui.intro

import com.neoconcept.model.content.Lesson

sealed interface IntroUiState {
    data object Loading : IntroUiState

    data class Success(val lesson: Lesson) : IntroUiState

    data class Error(val message: String) : IntroUiState
}
