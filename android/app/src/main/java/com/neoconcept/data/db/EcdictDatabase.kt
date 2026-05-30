package com.neoconcept.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.neoconcept.data.model.VocabularyItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class EcdictDatabase(private val context: Context) : SQLiteOpenHelper(
    context,
    DB_NAME,
    null,
    DB_VERSION
) {

    companion object {
        private const val DB_NAME = "ecdict.db"
        private const val DB_VERSION = 1
        private const val TABLE_NAME = "stardict"
    }

    private var isInitialized = false

    suspend fun init() = withContext(Dispatchers.IO) {
        if (isInitialized) return@withContext

        val dbFile = context.getDatabasePath(DB_NAME)
        if (!dbFile.exists()) {
            copyDatabaseFromAssets(dbFile)
        }

        readableDatabase
        isInitialized = true
    }

    private fun copyDatabaseFromAssets(dbFile: File) {
        dbFile.parentFile?.mkdirs()
        context.assets.open(DB_NAME).use { input ->
            dbFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
    }

    fun isReady(): Boolean = isInitialized

    suspend fun lookup(word: String): VocabularyItem? = withContext(Dispatchers.IO) {
        if (!isInitialized) return@withContext null
        if (word.isBlank()) return@withContext null

        try {
            val db = readableDatabase
            val cursor = db.query(
                TABLE_NAME,
                arrayOf("word", "phonetic", "translation", "definition", "pos", "exchange"),
                "word = ? COLLATE NOCASE",
                arrayOf(word.trim()),
                null,
                null,
                null,
                "1"
            )

            cursor.use {
                if (it.moveToFirst()) {
                    VocabularyItem(
                        word = it.getString(it.getColumnIndexOrThrow("word")),
                        phonetic = it.getString(it.getColumnIndexOrThrow("phonetic")) ?: "",
                        definitionCn = it.getString(it.getColumnIndexOrThrow("translation")) ?: "",
                        partOfSpeech = it.getString(it.getColumnIndexOrThrow("pos")) ?: "",
                        example = it.getString(it.getColumnIndexOrThrow("definition")) ?: ""
                    )
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun search(prefix: String, limit: Int = 20): List<VocabularyItem> = withContext(Dispatchers.IO) {
        if (!isInitialized) return@withContext emptyList()
        if (prefix.isBlank()) return@withContext emptyList()

        try {
            val db = readableDatabase
            val cursor = db.query(
                TABLE_NAME,
                arrayOf("word", "phonetic", "translation", "definition", "pos", "exchange"),
                "word LIKE ?",
                arrayOf("${prefix.trim()}%"),
                null,
                null,
                "word ASC",
                limit.toString()
            )

            cursor.use {
                val results = mutableListOf<VocabularyItem>()
                while (it.moveToNext()) {
                    results.add(
                        VocabularyItem(
                            word = it.getString(it.getColumnIndexOrThrow("word")),
                            phonetic = it.getString(it.getColumnIndexOrThrow("phonetic")) ?: "",
                            definitionCn = it.getString(it.getColumnIndexOrThrow("translation")) ?: "",
                            partOfSpeech = it.getString(it.getColumnIndexOrThrow("pos")) ?: "",
                            example = it.getString(it.getColumnIndexOrThrow("definition")) ?: ""
                        )
                    )
                }
                results
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun onCreate(db: SQLiteDatabase) {}

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}
}
