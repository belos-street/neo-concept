package com.neoconcept.ui.lesson

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

private const val TOTAL_STEPS = 6

@HiltViewModel
class LessonViewModel
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

        private val _uiState = MutableStateFlow<LessonUiState>(LessonUiState.Loading)
        val uiState: StateFlow<LessonUiState> = _uiState.asStateFlow()

        init {
            loadLesson()
        }

        private fun loadLesson() {
            viewModelScope.launch {
                contentRepository.loadLesson(path).fold(
                    onSuccess = { lesson ->
                        val progress = progressRepository.loadLessonProgress(bookId).find { it.lessonId == lessonId }
                        val step = maxOf(1, progress?.lastStep ?: 1)
                        _uiState.value = LessonUiState.Success(lesson = lesson, currentStep = step)
                    },
                    onFailure = { error ->
                        _uiState.value = LessonUiState.Error(error.message.orEmpty())
                    },
                )
            }
        }

        fun moveToNextStep() {
            val state = _uiState.value as? LessonUiState.Success ?: return
            val nextStep = minOf(state.currentStep + 1, TOTAL_STEPS)
            if (nextStep == state.currentStep) return
            _uiState.value = state.copy(currentStep = nextStep)
            saveProgress(nextStep)
        }

        fun goToStep(step: Int) {
            val state = _uiState.value as? LessonUiState.Success ?: return
            val target = step.coerceIn(1, TOTAL_STEPS)
            if (target == state.currentStep) return
            _uiState.value = state.copy(currentStep = target)
            saveProgress(target)
        }

        private fun saveProgress(step: Int) {
            applicationScope.launch {
                progressRepository.saveAppProgress(
                    AppProgress(bookId = bookId, lessonId = lessonId, step = step),
                )
                progressRepository.saveLessonProgress(
                    LessonProgress(
                        lessonId = lessonId,
                        status = LessonStatus.IN_PROGRESS,
                        lastStep = step,
                    ),
                )
            }
        }
    }
