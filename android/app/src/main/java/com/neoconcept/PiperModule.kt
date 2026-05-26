package com.neoconcept

import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

class PiperModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext),
  TextToSpeech.OnInitListener {

  private var tts: TextToSpeech? = null
  private var engineReady = false
  private var initPromise: Promise? = null

  override fun getName(): String = "PiperModule"

  @ReactMethod
  fun init(promise: Promise) {
    try {
      if (engineReady) {
        promise.resolve(true)
        return
      }
      if (tts != null) {
        initPromise = promise
        return
      }
      copyAssets()
      initPromise = promise
      tts = TextToSpeech(reactApplicationContext, this)
    } catch (e: Exception) {
      promise.reject("PIPER_INIT_ERROR", e.message, e)
    }
  }

  override fun onInit(status: Int) {
    engineReady = status == TextToSpeech.SUCCESS
    if (engineReady) {
      tts?.language = Locale.US
    }
    initPromise?.resolve(engineReady)
    initPromise = null
  }

  @ReactMethod
  fun speak(text: String, speed: Double, promise: Promise) {
    if (!engineReady || tts == null) {
      promise.reject("PIPER_NOT_READY", "TTS engine not initialized (engineReady=$engineReady, tts=${tts != null})")
      return
    }
    try {
      val safeSpeed = speed.coerceIn(0.5, 1.5).toFloat()
      tts!!.setSpeechRate(safeSpeed)
      tts!!.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
        override fun onStart(utteranceId: String?) {}
        override fun onDone(utteranceId: String?) {
          promise.resolve(null)
        }
        override fun onError(utteranceId: String?) {
          promise.reject("PIPER_SPEAK_ERROR", "TTS playback failed")
        }
      })
      val utteranceId = java.util.UUID.randomUUID().toString()
      val result = tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
      if (result == TextToSpeech.ERROR) {
        promise.reject("PIPER_SPEAK_ERROR", "TTS speak() returned ERROR code")
      }
    } catch (e: Exception) {
      promise.reject("PIPER_SPEAK_ERROR", e.message, e)
    }
  }

  @ReactMethod
  fun stop(promise: Promise) {
    try {
      tts?.stop()
      promise.resolve(null)
    } catch (e: Exception) {
      promise.reject("PIPER_STOP_ERROR", e.message, e)
    }
  }

  @ReactMethod
  fun isReady(promise: Promise) {
    promise.resolve(engineReady)
  }

  @ReactMethod
  fun addListener(eventName: String) {}

  @ReactMethod
  fun removeListeners(count: Int) {}

  private fun copyAssets() {
    val piperDir = File(reactApplicationContext.filesDir, "piper")
    if (!piperDir.exists()) piperDir.mkdirs()
    try {
      val assets = reactApplicationContext.assets.list("piper") ?: return
      for (fileName in assets) {
        val outFile = File(piperDir, fileName)
        if (outFile.exists()) continue
        reactApplicationContext.assets.open("piper/$fileName").use { input ->
          FileOutputStream(outFile).use { output ->
            input.copyTo(output)
          }
        }
      }
    } catch (_: Exception) {}
  }
}
