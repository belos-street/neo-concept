package com.neoconcept.data.repository

import com.neoconcept.model.progress.AppProgress
import com.neoconcept.model.progress.LessonProgress

interface ProgressRepository {
    suspend fun loadAppProgress(): AppProgress?

    suspend fun saveAppProgress(progress: AppProgress)

    suspend fun loadLessonProgress(bookId: String): List<LessonProgress>

    suspend fun saveLessonProgress(progress: LessonProgress)
}
