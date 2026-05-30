package com.neoconcept.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import com.neoconcept.util.AssetUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.FloatBuffer

class PiperTTS(private val context: Context) {

    companion object {
        private const val TAG = "PiperTTS"
        private const val ASSET_DIR = "piper"
        private const val MODEL_FILE = "model.onnx"
        private const val SAMPLE_RATE = 22050
    }

    private var isInitialized = false
    private var isSpeaking = false
    private var audioTrack: AudioTrack? = null
    private var currentJob: Job? = null
    private var ortSession: ai.onnxruntime.OrtSession? = null
    private var ortEnv: ai.onnxruntime.OrtEnvironment? = null

    suspend fun init() = withContext(Dispatchers.IO) {
        if (isInitialized) return@withContext

        try {
            val modelDir = File(context.filesDir, "piper")
            if (!modelDir.exists() || modelDir.listFiles().isNullOrEmpty()) {
                AssetUtils.copyAssetToDir(context, ASSET_DIR, modelDir)
            }

            val modelFile = File(modelDir, MODEL_FILE)
            if (modelFile.exists()) {
                ortEnv = ai.onnxruntime.OrtEnvironment.getEnvironment()
                ortSession = ortEnv?.createSession(modelFile.absolutePath)
                Log.d(TAG, "ONNX model loaded successfully")
            } else {
                Log.w(TAG, "Model file not found: ${modelFile.absolutePath}")
            }

            isInitialized = true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize", e)
        }
    }

    fun isReady(): Boolean = isInitialized

    suspend fun speak(text: String, speed: Float = 1.0f) = coroutineScope {
        if (!isInitialized || text.isBlank()) return@coroutineScope

        stop()
        isSpeaking = true

        currentJob = launch(Dispatchers.IO) {
            try {
                val pcmData = synthesize(text, speed.coerceIn(0.5f, 1.5f))
                if (pcmData != null && pcmData.isNotEmpty()) {
                    playAudio(pcmData)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Synthesis failed", e)
            } finally {
                isSpeaking = false
            }
        }
    }

    private fun synthesize(text: String, speed: Float): FloatArray? {
        val session = ortSession ?: return null
        val env = ortEnv ?: return null

        try {
            val textTensor = ai.onnxruntime.OnnxTensor.createTensor(env, arrayOf(text))
            val speedTensor = ai.onnxruntime.OnnxTensor.createTensor(env, floatArrayOf(speed))

            val inputs = mapOf("text" to textTensor, "speed" to speedTensor)
            val results = session.run(inputs)

            @Suppress("UNCHECKED_CAST")
            val audioTensor = results[0].value as Array<FloatArray>
            return audioTensor[0]
        } catch (e: Exception) {
            Log.e(TAG, "ONNX inference failed", e)
            return null
        }
    }

    private fun playAudio(pcmData: FloatArray) {
        val shortBuffer = ShortArray(pcmData.size) { i ->
            (pcmData[i] * 32767f).toInt().coerceIn(-32768, 32767).toShort()
        }

        val bufferSize = shortBuffer.size * 2
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .build()

        val audioFormat = AudioFormat.Builder()
            .setSampleRate(SAMPLE_RATE)
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .build()

        audioTrack = AudioTrack.Builder()
            .setAudioAttributes(audioAttributes)
            .setAudioFormat(audioFormat)
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        audioTrack?.write(shortBuffer, 0, shortBuffer.size)
        audioTrack?.play()

        while (audioTrack?.playState == AudioTrack.PLAYSTATE_PLAYING) {
            Thread.sleep(50)
        }
    }

    fun stop() {
        currentJob?.cancel()
        audioTrack?.stop()
        audioTrack?.release()
        audioTrack = null
        isSpeaking = false
    }

    fun isSpeaking(): Boolean = isSpeaking

    fun release() {
        stop()
        ortSession?.close()
        ortEnv?.close()
        isInitialized = false
    }
}
