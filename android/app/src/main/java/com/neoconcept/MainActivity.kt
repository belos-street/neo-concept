package com.neoconcept

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.neoconcept.audio.TTSManager
import com.neoconcept.audio.WhisperASR
import com.neoconcept.data.repository.EcdictRepository
import com.neoconcept.data.repository.ManifestRepository
import com.neoconcept.data.repository.ProgressRepository
import com.neoconcept.navigation.AppNavigation
import com.neoconcept.ui.theme.NeoConceptTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    lateinit var ecdictRepository: EcdictRepository
        private set
    lateinit var whisperASR: WhisperASR
        private set
    lateinit var manifestRepository: ManifestRepository
        private set
    lateinit var progressRepository: ProgressRepository
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ecdictRepository = EcdictRepository(this)
        whisperASR = WhisperASR(this)
        manifestRepository = ManifestRepository(this)
        progressRepository = ProgressRepository(this)
        TTSManager.init(this)

        lifecycleScope.launch {
            ecdictRepository.init()
            TTSManager.initModel()
            whisperASR.init()
        }

        enableEdgeToEdge()
        setContent {
            NeoConceptTheme {
                AppNavigation(
                    manifestRepository = manifestRepository,
                    progressRepository = progressRepository
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        TTSManager.release()
        whisperASR.release()
    }
}
