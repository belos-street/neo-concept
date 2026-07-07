package com.neoconcept.model.content

data class ContentIndex(
    val books: List<IndexedBook>,
    val corruptedEntries: List<CorruptedEntry>,
) {
    val hasCorruptedEntries: Boolean
        get() = corruptedEntries.isNotEmpty()

    val allLessons: List<IndexedLesson>
        get() = books.flatMap { it.lessons }
}

data class IndexedBook(
    val manifestBook: ManifestBook,
    val book: Book,
    val lessons: List<IndexedLesson>,
)

data class IndexedLesson(
    val bookId: String,
    val ref: BookLessonRef,
    val lesson: Lesson,
)

data class CorruptedEntry(
    val bookId: String,
    val lessonId: String,
    val path: String,
    val error: Throwable,
)
