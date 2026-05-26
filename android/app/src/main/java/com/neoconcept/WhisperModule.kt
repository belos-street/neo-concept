package com.neoconcept

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.RandomAccessFile
import java.util.UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class WhisperModule(reactContext: ReactApplicationContext) :
  ReactContextBaseJavaModule(reactContext) {

  private var initialized = false
  private var isRecording = false
  private var recordingJob: Job? = null
  private var audioRecord: AudioRecord? = null
  private var currentAudioFile: File? = null

  private val scope = CoroutineScope(Dispatchers.IO)

  override fun getName(): String = "WhisperModule"

  @ReactMethod
  fun init(promise: Promise) {
    try {
      if (initialized) {
        promise.resolve(initialized)
        return
      }
      copyAssets()
      initialized = true
      promise.resolve(initialized)
    } catch (e: Exception) {
      promise.reject("WHISPER_INIT_ERROR", e.message, e)
    }
  }

  @ReactMethod
  fun startRecording(promise: Promise) {
    if (isRecording) {
      promise.reject("WHISPER_ALREADY_RECORDING", "Recording already in progress")
      return
    }
    try {
      val sampleRate = 16000
      val audioFile = File(
        reactApplicationContext.cacheDir,
        "whisper_recording_${UUID.randomUUID()}.wav"
      )
      currentAudioFile = audioFile

      val bufferSize = AudioRecord.getMinBufferSize(
        sampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT
      )

      audioRecord = AudioRecord(
        MediaRecorder.AudioSource.MIC,
        sampleRate,
        AudioFormat.CHANNEL_IN_MONO,
        AudioFormat.ENCODING_PCM_16BIT,
        bufferSize
      )

      isRecording = true
      audioRecord!!.startRecording()

      recordingJob = scope.launch {
        val buffer = ByteArray(bufferSize)
        val pcmData = mutableListOf<Byte>()

        while (isRecording) {
          val bytesRead = audioRecord!!.read(buffer, 0, buffer.size)
          if (bytesRead > 0) {
            pcmData.addAll(buffer.take(bytesRead))
          }
        }

        audioRecord!!.stop()
        audioRecord!!.release()
        audioRecord = null

        writeWavFile(audioFile, pcmData.toByteArray(), sampleRate)
        promise.resolve(audioFile.absolutePath)
      }
    } catch (e: Exception) {
      isRecording = false
      promise.reject("WHISPER_RECORDING_ERROR", e.message, e)
    }
  }

  @ReactMethod
  fun stopRecording(promise: Promise) {
    isRecording = false
    promise.resolve(null)
  }

  @ReactMethod
  fun recognize(audioPath: String, promise: Promise) {
    if (!initialized) {
      promise.reject("WHISPER_NOT_READY", "ASR not initialized")
      return
    }
    try {
      val audioFile = File(audioPath)
      if (!audioFile.exists()) {
        promise.reject("WHISPER_FILE_NOT_FOUND", "Audio file not found: $audioPath")
        return
      }

      // TODO: Replace with actual Whisper.cpp JNI call
      // For now, return placeholder result
      val result = Arguments.createMap().apply {
        putString("text", "<whisper_pending>")
        putDouble("confidence", 0.0)
      }

      // Clean up temp file after recognition
      audioFile.delete()

      promise.resolve(result)
    } catch (e: Exception) {
      promise.reject("WHISPER_RECOGNIZE_ERROR", e.message, e)
    }
  }

  @ReactMethod
  fun isReady(promise: Promise) {
    promise.resolve(initialized)
  }

  @ReactMethod
  fun addListener(eventName: String) {}

  @ReactMethod
  fun removeListeners(count: Int) {}

  private fun writeWavFile(file: File, pcmData: ByteArray, sampleRate: Int) {
    RandomAccessFile(file, "rw").use { raf ->
      val dataSize = pcmData.size
      val fileSize = 36 + dataSize

      raf.write(intToByteArray(0x46464952)) // "RIFF"
      raf.write(intToByteArray(fileSize))
      raf.write(intToByteArray(0x45564157)) // "WAVE"
      raf.write(intToByteArray(0x20746D66)) // "fmt "
      raf.write(intToByteArray(16))         // chunk size
      raf.write(shortToByteArray(1))        // PCM format
      raf.write(shortToByteArray(1))        // mono
      raf.write(intToByteArray(sampleRate))
      raf.write(intToByteArray(sampleRate * 2)) // byte rate
      raf.write(shortToByteArray(2))        // block align
      raf.write(shortToByteArray(16))       // bits per sample
      raf.write(intToByteArray(0x61746164)) // "data"
      raf.write(intToByteArray(dataSize))
      raf.write(pcmData)
    }
  }

  private fun intToByteArray(value: Int): ByteArray {
    return byteArrayOf(
      (value and 0xFF).toByte(),
      ((value shr 8) and 0xFF).toByte(),
      ((value shr 16) and 0xFF).toByte(),
      ((value shr 24) and 0xFF).toByte()
    )
  }

  private fun shortToByteArray(value: Short): ByteArray {
    return byteArrayOf(
      (value.toInt() and 0xFF).toByte(),
      ((value.toInt() shr 8) and 0xFF).toByte()
    )
  }

  private fun copyAssets() {
    val whisperDir = File(reactApplicationContext.filesDir, "whisper")
    if (!whisperDir.exists()) {
      whisperDir.mkdirs()
    }
    try {
      val files = reactApplicationContext.assets.list("whisper")
      files?.forEach { fileName ->
        val destFile = File(whisperDir, fileName)
        if (!destFile.exists()) {
          reactApplicationContext.assets.open("whisper/$fileName").use { input ->
            FileOutputStream(destFile).use { output ->
              input.copyTo(output)
            }
          }
        }
      }
    } catch (_: Exception) {
      // assets/whisper/ may be empty or not exist yet
    }
  }
}