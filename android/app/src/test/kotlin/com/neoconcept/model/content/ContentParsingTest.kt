package com.neoconcept.model.content

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.io.File

class ContentParsingTest {
    private val json =
        Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        }

    @Test
    fun `parse manifest json`() {
        val text = readAsset("content/manifest.json")
        val manifest = json.decodeFromString<Manifest>(text)

        assertEquals("1.0.0", manifest.version)
        assertEquals(1, manifest.schemaVersion)
        assertEquals(1, manifest.books.size)
        assertEquals("book01", manifest.books.first().id)
    }

    @Test
    fun `parse book json`() {
        val text = readAsset("content/books/book01/book.json")
        val book = json.decodeFromString<Book>(text)

        assertEquals("book01", book.id)
        assertEquals(2, book.totalLessons)
        assertEquals(2, book.lessons.size)
        assertEquals("book01-L01", book.lessons.first().id)
    }

    @Test
    fun `parse lesson json`() {
        val text = readAsset("content/books/book01/lessons/L01/lesson.json")
        val lesson = json.decodeFromString<Lesson>(text)

        assertEquals("book01-L01", lesson.id)
        assertEquals("book01", lesson.bookId)
        assertEquals("Excuse me!", lesson.title)
        assertEquals(1, lesson.text.paragraphs.size)
        assertEquals(7, lesson.text.paragraphs.first().sentences.size)
        assertEquals(4, lesson.vocabulary.size)
        assertEquals(2, lesson.exercises.fillInBlanks.size)
        assertEquals(2, lesson.exercises.spelling.size)
        assertEquals(2, lesson.exercises.comprehension.questions.size)
        assertNotNull(lesson.banner.remote)
    }

    private fun readAsset(path: String): String {
        return File("src/main/assets/$path").readText()
    }
}
