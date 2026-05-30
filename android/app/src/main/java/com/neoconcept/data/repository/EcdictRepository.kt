package com.neoconcept.data.repository

import android.content.Context
import com.neoconcept.data.db.EcdictDatabase
import com.neoconcept.data.model.VocabularyItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EcdictRepository(private val context: Context) {

    private val database = EcdictDatabase(context)

    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    suspend fun init() {
        database.init()
        _isReady.value = database.isReady()
    }

    suspend fun lookup(word: String): VocabularyItem? {
        return database.lookup(word)
    }

    suspend fun search(prefix: String, limit: Int = 20): List<VocabularyItem> {
        return database.search(prefix, limit)
    }

    fun isReady(): Boolean = database.isReady()
}
