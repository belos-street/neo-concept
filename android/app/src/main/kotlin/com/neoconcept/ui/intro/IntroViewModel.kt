package com.neoconcept.ui.intro

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neoconcept.data.repository.ContentRepository
import com.neoconcept.data.repository.ProgressRepository
import com.neoconcept.di.ApplicationScope
import com.neoconcept.model.progress.AppProgress
import com.neoconcept.model.progress.LessonProgress
import com.neoconcept.model.progress.LessonStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroViewModel
    @Inject
    constructor(
        private val savedStateHandle: SavedStateHandle,
        private val contentRepository: ContentRepository,
        private val progressRepository: ProgressRepository,
        @ApplicationScope private val applicationScope: CoroutineScope,
    ) : ViewModel() {
        private val refPath: String = checkNotNull(savedStateHandle["refPath"])
        private val bookId: String = checkNotNull(savedStateHandle["bookId"])
        private val lessonId: String = checkNotNull(savedStateHandle["lessonId"])
        private val path: String = "books/$bookId/${Uri.decode(refPath)}"

        private val _uiState = MutableStateFlow<IntroUiState>(IntroUiState.Loading)
        val uiState: StateFlow<IntroUiState> = _uiState.asStateFlow()

        init {
            loadLesson()
        }

        private fun loadLesson() {
            viewModelScope.launch {
                contentRepository.loadLesson(path).fold(
                    onSuccess = { lesson ->
                        _uiState.value = IntroUiState.Success(lesson = lesson)
                    },
                    onFailure = { error ->
                        _uiState.value = IntroUiState.Error(error.message.orEmpty())
                    },
                )
            }
        }

        fun startLearning(onSaved: () -> Unit) {
            applicationScope.launch {
                progressRepository.saveAppProgress(
                    AppProgress(bookId = bookId, lessonId = lessonId, step = 1),
                )
                progressRepository.saveLessonProgress(
                    LessonProgress(
                        lessonId = lessonId,
                        status = LessonStatus.IN_PROGRESS,
                        lastStep = 1,
                    ),
                )
                onSaved()
            }
        }
    }
