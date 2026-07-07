package com.neoconcept.ui.home

import com.neoconcept.model.content.IndexedBook

sealed interface HomeUiState {
    data object Loading : HomeUiState

    data class Success(
        val books: List<IndexedBook>,
        val hasCorruptedEntries: Boolean,
    ) : HomeUiState

    data class Error(val message: String) : HomeUiState
}
