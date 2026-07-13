package com.neoconcept.ui.completion

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neoconcept.data.repository.ContentRepository
import com.neoconcept.data.repository.ProgressRepository
import com.neoconcept.model.content.BookLessonRef
import com.neoconcept.model.content.IndexedLesson
import com.neoconcept.model.progress.LessonProgress
import com.neoconcept.model.progress.LessonStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompletionViewModel
    @Inject
    constructor(
        private val savedStateHandle: SavedStateHandle,
        private val contentRepository: ContentRepository,
        private val progressRepository: ProgressRepository,
    ) : ViewModel() {
        private val bookId: String = checkNotNull(savedStateHandle["bookId"])
        private val lessonId: String = checkNotNull(savedStateHandle["lessonId"])

        private val _uiState = MutableStateFlow<CompletionUiState>(CompletionUiState.Loading)
        val uiState: StateFlow<CompletionUiState> = _uiState.asStateFlow()

        init {
            loadCompletionData()
        }

        private fun loadCompletionData() {
            viewModelScope.launch {
                contentRepository.buildIndex().fold(
                    onSuccess = { index ->
                        val indexedBook = index.books.find { it.book.id == bookId }
                        val indexedLesson = indexedBook?.lessons?.find { it.ref.id == lessonId }
                        if (indexedLesson == null) {
                            _uiState.value = CompletionUiState.Error("Lesson not found")
                            return@fold
                        }

                        markLessonCompleted()

                        val nextLesson = findNextLesson(indexedBook.lessons, lessonId)
                        _uiState.value =
                            CompletionUiState.Success(
                                displayNumber = indexedLesson.ref.displayNumber,
                                title = indexedLesson.ref.title,
                                vocabulary = indexedLesson.lesson.vocabulary,
                                nextLesson = nextLesson,
                            )
                    },
                    onFailure = { error ->
                        _uiState.value = CompletionUiState.Error(error.message.orEmpty())
                    },
                )
            }
        }

        private fun findNextLesson(
            lessons: List<IndexedLesson>,
            currentLessonId: String,
        ): BookLessonRef? {
            val currentIndex = lessons.indexOfFirst { it.ref.id == currentLessonId }
            val nextIndex = currentIndex + 1
            return if (nextIndex in lessons.indices) lessons[nextIndex].ref else null
        }

        private suspend fun markLessonCompleted() {
            progressRepository.saveLessonProgress(
                LessonProgress(
                    lessonId = lessonId,
                    status = LessonStatus.COMPLETED,
                    lastStep = TOTAL_STEPS,
                ),
            )
        }

        companion object {
            private const val TOTAL_STEPS = 6
        }
    }
