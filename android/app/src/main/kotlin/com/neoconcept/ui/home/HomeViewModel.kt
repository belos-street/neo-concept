package com.neoconcept.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neoconcept.data.repository.ContentRepository
import com.neoconcept.data.repository.ProgressRepository
import com.neoconcept.model.content.ContentIndex
import com.neoconcept.model.content.IndexedBook
import com.neoconcept.model.content.IndexedLesson
import com.neoconcept.model.progress.AppProgress
import com.neoconcept.model.progress.LessonSteps
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
        private val progressRepository: ProgressRepository,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
        val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

        init {
            loadHomeData()
        }

        private fun loadHomeData() {
            viewModelScope.launch {
                contentRepository.buildIndex().fold(
                    onSuccess = { index ->
                        val progress = progressRepository.loadAppProgress()
                        _uiState.value =
                            HomeUiState.Success(
                                books = index.books,
                                hasCorruptedEntries = index.hasCorruptedEntries,
                                continueProgress = resolveContinueProgress(index, progress),
                            )
                    },
                    onFailure = { error ->
                        _uiState.value = HomeUiState.Error(error.message.orEmpty())
                    },
                )
            }
        }

        private fun resolveContinueProgress(
            index: ContentIndex,
            progress: AppProgress?,
        ): ContinueProgress? =
            if (progress == null || progress.step >= LessonSteps.COMPLETION) {
                null
            } else {
                resolveFromIndex(index, progress)
            }

        private fun resolveFromIndex(
            index: ContentIndex,
            progress: AppProgress,
        ): ContinueProgress? =
            index.books
                .find { it.book.id == progress.bookId }
                ?.let { indexedBook ->
                    indexedBook.lessons
                        .find { it.lesson.id == progress.lessonId }
                        ?.let { indexedLesson ->
                            buildContinueProgress(progress, indexedBook, indexedLesson)
                        }
                }

        private fun buildContinueProgress(
            progress: AppProgress,
            indexedBook: IndexedBook,
            indexedLesson: IndexedLesson,
        ): ContinueProgress =
            ContinueProgress(
                bookId = indexedBook.book.id,
                lessonId = indexedLesson.lesson.id,
                step = progress.step,
                bookTitle = indexedBook.book.title,
                lessonTitle = indexedLesson.lesson.title,
                stepLabel = LessonSteps.label(progress.step),
                totalLessons = indexedBook.book.totalLessons,
            )
    }
