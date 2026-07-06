package com.neoconcept.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neoconcept.data.repository.ContentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val contentRepository: ContentRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
        val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

        init {
            loadIndex()
        }

        private fun loadIndex() {
            viewModelScope.launch {
                contentRepository.buildIndex().fold(
                    onSuccess = { index ->
                        _uiState.value =
                            HomeUiState.Success(
                                books = index.books,
                                hasCorruptedEntries = index.hasCorruptedEntries,
                            )
                    },
                    onFailure = { error ->
                        _uiState.value = HomeUiState.Error(error.message.orEmpty())
                    },
                )
            }
        }
    }
