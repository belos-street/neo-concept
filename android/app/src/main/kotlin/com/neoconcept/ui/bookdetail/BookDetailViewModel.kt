package com.neoconcept.ui.bookdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.neoconcept.data.repository.ContentRepository
import com.neoconcept.data.repository.ProgressRepository
import com.neoconcept.model.content.IndexedBook
import com.neoconcept.model.progress.LessonStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val GROUP_SIZE = 10

@HiltViewModel
class BookDetailViewModel
    @Inject
    constructor(
        private val savedStateHandle: SavedStateHandle,
        private val contentRepository: ContentRepository,
        private val progressRepository: ProgressRepository,
    ) : ViewModel() {
        private val bookId: String = checkNotNull(savedStateHandle["bookId"])

        private val _uiState = MutableStateFlow<BookDetailUiState>(BookDetailUiState.Loading)
        val uiState: StateFlow<BookDetailUiState> = _uiState.asStateFlow()

        init {
            loadBookDetail()
        }

        private fun loadBookDetail() {
            viewModelScope.launch {
                contentRepository.buildIndex().fold(
                    onSuccess = { index ->
                        val indexedBook = index.books.find { it.book.id == bookId }
                        if (indexedBook == null) {
                            _uiState.value = BookDetailUiState.Error("Book not found: $bookId")
                            return@fold
                        }
                        val lessonProgressMap =
                            progressRepository
                                .loadLessonProgress(bookId)
                                .associateBy { it.lessonId }
                        val groups = buildGroups(indexedBook, lessonProgressMap)
                        _uiState.value = BookDetailUiState.Success(book = indexedBook.book, groups = groups)
                    },
                    onFailure = { error ->
                        _uiState.value = BookDetailUiState.Error(error.message.orEmpty())
                    },
                )
            }
        }

        private fun buildGroups(
            indexedBook: IndexedBook,
            lessonProgressMap: Map<String, com.neoconcept.model.progress.LessonProgress>,
        ): List<LessonGroup> {
            val lessons = indexedBook.lessons
            val groups = mutableListOf<LessonGroup>()
            var i = 0
            while (i < lessons.size) {
                val end = minOf(i + GROUP_SIZE, lessons.size)
                val rows =
                    lessons.subList(i, end).map { indexedLesson ->
                        val progress = lessonProgressMap[indexedLesson.ref.id]
                        LessonRow(
                            lessonId = indexedLesson.ref.id,
                            displayNumber = indexedLesson.ref.displayNumber,
                            title = indexedLesson.ref.title,
                            subtitle = indexedLesson.lesson.subtitle,
                            status = progress?.status ?: LessonStatus.NOT_STARTED,
                        )
                    }
                groups.add(
                    LessonGroup(
                        startIndex = i + 1,
                        endIndex = end,
                        lessons = rows,
                    ),
                )
                i = end
            }
            return groups
        }
    }
