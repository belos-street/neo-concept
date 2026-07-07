package com.neoconcept.data.repository

import com.neoconcept.data.local.FakeAssetDataSource
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class ContentIndexTest {
    @get:Rule
    val tempFolder = TemporaryFolder()

    @Test
    fun `build index loads all healthy lessons`() {
        runBlocking {
            val repository = ContentRepositoryImpl(FakeAssetDataSource(File("src/main/assets")))

            val index = repository.buildIndex().getOrThrow()

            assertEquals(1, index.books.size)
            assertEquals("book01", index.books.first().book.id)
            assertEquals(2, index.allLessons.size)
            assertEquals("book01-L01", index.allLessons.first().lesson.id)
            assertTrue(index.corruptedEntries.isEmpty())
        }
    }

    @Test
    fun `build index filters corrupted lesson and reports it`() {
        runBlocking {
            val contentRoot = File("src/main/assets/content")
            val tempContent = File(tempFolder.root, "content")
            contentRoot.copyRecursively(tempContent)

            val corruptedLesson = File(tempContent, "books/book01/lessons/L02/lesson.json")
            corruptedLesson.writeText("{ this is not valid json")

            val repository = ContentRepositoryImpl(FakeAssetDataSource(tempFolder.root))
            val index = repository.buildIndex().getOrThrow()

            assertEquals(1, index.books.size)
            assertEquals(1, index.allLessons.size)
            assertEquals("book01-L01", index.allLessons.first().lesson.id)
            assertEquals(1, index.corruptedEntries.size)
            assertEquals("book01-L02", index.corruptedEntries.first().lessonId)
            assertTrue(index.hasCorruptedEntries)
        }
    }
}
