package com.neoconcept.audio

import android.content.Context
import android.media.MediaRecorder
import android.util.Log
import com.neoconcept.util.AssetUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

class WhisperASR(private val context: Context) {

    companion object {
        private const val TAG = "WhisperASR"
        private const val ASSET_DIR = "whisper"
        private const val SAMPLE_RATE = 16000
        private const val MAX_DURATION_MS = 30_000L
    }

    private var isInitialized = false
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private var currentRecordingPath: String? = null

    suspend fun init() = withContext(Dispatchers.IO) {
        if (isInitialized) return@withContext

        try {
            val modelDir = File(context.filesDir, "whisper")
            if (!modelDir.exists() || modelDir.listFiles().isNullOrEmpty()) {
                AssetUtils.copyAssetToDir(context, ASSET_DIR, modelDir)
                Log.d(TAG, "Model files copied to ${modelDir.absolutePath}")
            } else {
                Log.d(TAG, "Model files already exist")
            }

            isInitialized = true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize", e)
        }
    }

    fun isReady(): Boolean = isInitialized

    fun startRecording(): String? {
        if (!isInitialized) {
            Log.w(TAG, "Not initialized")
            return null
        }

        val outputFile = File(context.cacheDir, "recording_${System.currentTimeMillis()}.wav")
        currentRecordingPath = outputFile.absolutePath

        mediaRecorder = MediaRecorder(context).apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioSamplingRate(SAMPLE_RATE)
            setAudioEncodingBitRate(64000)
            setOutputFile(outputFile.absolutePath)
            setMaxDuration(MAX_DURATION_MS.toInt())

            setOnInfoListener { _, what, _ ->
                if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                    Log.d(TAG, "Max duration reached")
                    stopRecording()
                }
            }

            try {
                prepare()
                start()
                isRecording = true
                Log.d(TAG, "Recording started: ${outputFile.absolutePath}")
                return outputFile.absolutePath
            } catch (e: IOException) {
                Log.e(TAG, "Failed to start recording", e)
                return null
            }
        }
    }

    fun stopRecording(): String? {
        if (!isRecording) {
            Log.w(TAG, "Not recording")
            return null
        }

        mediaRecorder?.apply {
            try {
                stop()
                release()
                Log.d(TAG, "Recording stopped")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to stop recording", e)
            }
        }
        mediaRecorder = null
        isRecording = false

        return currentRecordingPath
    }

    suspend fun recognize(audioPath: String): String = withContext(Dispatchers.IO) {
        if (!isInitialized) {
            Log.w(TAG, "Not initialized")
            return@withContext ""
        }

        val audioFile = File(audioPath)
        if (!audioFile.exists()) {
            Log.w(TAG, "Audio file not found: $audioPath")
            return@withContext ""
        }

        try {
            Log.d(TAG, "Processing audio: $audioPath")
            val result = processAudio(audioFile)
            audioFile.delete()
            Log.d(TAG, "Recognition result: $result")
            result
        } catch (e: Exception) {
            Log.e(TAG, "Recognition failed", e)
            audioFile.delete()
            ""
        }
    }

    private fun processAudio(audioFile: File): String {
        // TODO: Implement actual GGML Whisper inference
        // This requires native JNI integration with whisper.cpp
        // For now, return empty string as placeholder
        Log.d(TAG, "processAudio placeholder - needs GGML JNI implementation")
        return ""
    }

    fun isRecording(): Boolean = isRecording

    fun release() {
        if (isRecording) {
            stopRecording()
        }
        isInitialized = false
        Log.d(TAG, "Released")
    }
}
