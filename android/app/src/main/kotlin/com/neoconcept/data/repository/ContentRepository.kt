package com.neoconcept.data.repository

import com.neoconcept.model.content.Book
import com.neoconcept.model.content.ContentIndex
import com.neoconcept.model.content.Lesson
import com.neoconcept.model.content.Manifest

interface ContentRepository {
    suspend fun loadManifest(): Result<Manifest>

    suspend fun loadBook(path: String): Result<Book>

    suspend fun loadLesson(path: String): Result<Lesson>

    suspend fun buildIndex(): Result<ContentIndex>
}
