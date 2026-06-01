package com.neoconcept.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.neoconcept.data.repository.SettingsRepository
import com.neoconcept.ui.theme.SwissBorder
import com.neoconcept.ui.theme.SwissColor
import com.neoconcept.ui.theme.SwissSpace
import com.neoconcept.ui.theme.SwissTouch
import com.neoconcept.ui.theme.SwissTypography
import kotlinx.coroutines.launch

private data class SpeedOption(val label: String, val value: Float)

private val speedOptions = listOf(
    SpeedOption("0.75×", 0.75f),
    SpeedOption("1.0×", 1.0f),
    SpeedOption("1.25×", 1.25f),
    SpeedOption("1.5×", 1.5f)
)

@Composable
fun SettingsScreen(
    settingsRepository: SettingsRepository
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val ttsSpeed by settingsRepository.ttsSpeed.collectAsState(initial = 1.0f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SwissColor.bg)
            .verticalScroll(scrollState)
            .padding(SwissSpace.space4)
    ) {
        Text(
            text = "设置",
            style = SwissTypography.titleLg,
            color = SwissColor.fg
        )

        Spacer(modifier = Modifier.height(SwissSpace.space6))

        Text(
            text = "TTS 语速",
            style = SwissTypography.labelLg,
            color = SwissColor.fg
        )

        Spacer(modifier = Modifier.height(SwissSpace.space2))

        Row(modifier = Modifier.fillMaxWidth()) {
            for ((index, option) in speedOptions.withIndex()) {
                val isSelected = ttsSpeed == option.value

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(SwissTouch.minHeight)
                        .background(if (isSelected) SwissColor.fg else SwissColor.bg)
                        .border(width = SwissBorder.width, color = SwissColor.border)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            scope.launch {
                                settingsRepository.setTtsSpeed(option.value)
                            }
                        }
                        .padding(SwissSpace.space2),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Text(
                        text = option.label,
                        style = SwissTypography.label,
                        color = if (isSelected) SwissColor.bg else SwissColor.fg
                    )
                }
            }
        }
    }
}
