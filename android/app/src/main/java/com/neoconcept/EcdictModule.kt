package com.neoconcept

import android.database.sqlite.SQLiteDatabase
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import java.io.File
import java.io.FileOutputStream

class EcdictModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {

  private var db: SQLiteDatabase? = null
  private var initialized = false

  override fun getName(): String = "EcdictModule"

  @ReactMethod
  fun init(promise: Promise) {
    try {
      if (initialized) {
        promise.resolve(initialized)
        return
      }
      val dbFile = copyAsset("ecdict.db", "ecdict/ecdict.db")
      db = SQLiteDatabase.openDatabase(
        dbFile.absolutePath, null, SQLiteDatabase.OPEN_READONLY
      )
      initialized = true
      promise.resolve(initialized)
    } catch (e: Exception) {
      promise.reject("ECDICT_INIT_ERROR", e.message, e)
    }
  }

  @ReactMethod
  fun lookup(word: String, promise: Promise) {
    if (!initialized || db == null) {
      promise.resolve(null)
      return
    }
    try {
      val cursor = db!!.rawQuery(
        """SELECT word, phonetic, definition, pos, exchange
           FROM stardict WHERE word = ? COLLATE NOCASE LIMIT 1""",
        arrayOf(word)
      )
      if (cursor.moveToFirst()) {
        val result = Arguments.createMap().apply {
          putString("word", cursor.getString(0))
          putString("phonetic", cursor.getString(1) ?: "")
          putString("definition", cursor.getString(2) ?: "")
          putString("pos", cursor.getString(3) ?: "")
          putString("exchange", cursor.getString(4) ?: "")
        }
        cursor.close()
        promise.resolve(result)
      } else {
        cursor.close()
        promise.resolve(null)
      }
    } catch (e: Exception) {
      promise.reject("ECDICT_LOOKUP_ERROR", e.message, e)
    }
  }

  @ReactMethod
  fun isReady(promise: Promise) {
    promise.resolve(initialized)
  }

  private fun copyAsset(assetName: String, destRelativePath: String): File {
    val destFile = File(reactApplicationContext.filesDir, destRelativePath)
    if (!destFile.exists()) {
      destFile.parentFile?.mkdirs()
      reactApplicationContext.assets.open(assetName).use { input ->
        FileOutputStream(destFile).use { output ->
          input.copyTo(output)
        }
      }
    }
    return destFile
  }
}