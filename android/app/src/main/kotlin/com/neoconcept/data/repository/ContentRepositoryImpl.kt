package com.neoconcept.data.repository

import com.neoconcept.data.local.AssetDataSource
import com.neoconcept.model.content.Book
import com.neoconcept.model.content.ContentIndex
import com.neoconcept.model.content.CorruptedEntry
import com.neoconcept.model.content.IndexedBook
import com.neoconcept.model.content.IndexedLesson
import com.neoconcept.model.content.Lesson
import com.neoconcept.model.content.Manifest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContentRepositoryImpl
    @Inject
    constructor(
        private val assetDataSource: AssetDataSource,
    ) : ContentRepository {
        private val json =
            Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
            }

        override suspend fun loadManifest(): Result<Manifest> = loadJson("content/manifest.json")

        override suspend fun loadBook(path: String): Result<Book> = loadJson("content/$path")

        override suspend fun loadLesson(path: String): Result<Lesson> = loadJson("content/$path")

        @Suppress("InjectDispatcher")
        override suspend fun buildIndex(): Result<ContentIndex> =
            withContext(Dispatchers.IO) {
                loadManifest().fold(
                    onSuccess = { manifest ->
                        val indexedBooks = mutableListOf<IndexedBook>()
                        val corruptedEntries = mutableListOf<CorruptedEntry>()

                        manifest.books.forEach { manifestBook ->
                            loadBook(manifestBook.path).fold(
                                onSuccess = { book ->
                                    val lessons = mutableListOf<IndexedLesson>()
                                    book.lessons.forEach { ref ->
                                        val lessonPath = "books/${book.id}/${ref.path}"
                                        loadLesson(lessonPath).fold(
                                            onSuccess = { lesson ->
                                                lessons.add(IndexedLesson(book.id, ref, lesson))
                                            },
                                            onFailure = { error ->
                                                corruptedEntries.add(
                                                    CorruptedEntry(
                                                        bookId = book.id,
                                                        lessonId = ref.id,
                                                        path = lessonPath,
                                                        error = error,
                                                    ),
                                                )
                                            },
                                        )
                                    }
                                    indexedBooks.add(IndexedBook(manifestBook, book, lessons))
                                },
                                onFailure = {
                                    // 整本书损坏时直接跳过，具体错误由调用方通过 Result 处理。
                                },
                            )
                        }

                        Result.success(ContentIndex(indexedBooks, corruptedEntries))
                    },
                    onFailure = { Result.failure(it) },
                )
            }

        private inline fun <reified T> loadJson(path: String): Result<T> {
            return try {
                assetDataSource.open(path).bufferedReader().use { reader ->
                    val text = reader.readText()
                    Result.success(json.decodeFromString(text))
                }
            } catch (e: IOException) {
                Result.failure(ContentError.FileNotFound(path, e))
            } catch (e: SerializationException) {
                Result.failure(ContentError.ParseFailed(path, e))
            } catch (e: IllegalArgumentException) {
                Result.failure(ContentError.ParseFailed(path, e))
            }
        }
    }

sealed class ContentError(
    message: String,
    override val cause: Throwable? = null,
) : Exception(message, cause) {
    data class FileNotFound(val path: String, override val cause: Throwable) :
        ContentError("File not found: $path", cause)

    data class ParseFailed(val path: String, override val cause: Throwable) :
        ContentError("Failed to parse: $path", cause)
}
