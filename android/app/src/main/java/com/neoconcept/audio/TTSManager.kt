package com.neoconcept.audio

import android.content.Context

object TTSManager {

    private var piperTTS: PiperTTS? = null

    fun init(context: Context) {
        if (piperTTS == null) {
            piperTTS = PiperTTS(context)
        }
    }

    suspend fun initModel() {
        piperTTS?.init()
    }

    fun isReady(): Boolean = piperTTS?.isReady() == true

    suspend fun speak(text: String, speed: Float = 1.0f) {
        piperTTS?.speak(text, speed)
    }

    fun stop() {
        piperTTS?.stop()
    }

    fun isSpeaking(): Boolean = piperTTS?.isSpeaking() == true

    fun release() {
        piperTTS?.release()
        piperTTS = null
    }
}
