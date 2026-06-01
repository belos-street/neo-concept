package com.neoconcept.features.lesson

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.neoconcept.audio.TTSManager
import com.neoconcept.ui.theme.SwissBorder
import com.neoconcept.ui.theme.SwissColor
import com.neoconcept.ui.theme.SwissSpace
import com.neoconcept.ui.theme.SwissTypography
import kotlinx.coroutines.launch

enum class PlaybackState {
    IDLE, PLAYING, PAUSED
}

@Composable
fun SentencePlayer(
    sentences: List<String>,
    onWordClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var playbackState by remember { mutableStateOf(PlaybackState.IDLE) }
    var currentSentenceIndex by remember { mutableIntStateOf(-1) }

    fun playNext(fromIndex: Int) {
        if (fromIndex >= sentences.size) {
            playbackState = PlaybackState.IDLE
            currentSentenceIndex = -1
            return
        }
        currentSentenceIndex = fromIndex
        playbackState = PlaybackState.PLAYING
        scope.launch {
            TTSManager.speak(sentences[fromIndex])
            if (playbackState == PlaybackState.PLAYING) {
                playNext(fromIndex + 1)
            }
        }
    }

    fun togglePlayback() {
        when (playbackState) {
            PlaybackState.IDLE -> {
                playNext(0)
            }
            PlaybackState.PLAYING -> {
                TTSManager.stop()
                playbackState = PlaybackState.PAUSED
            }
            PlaybackState.PAUSED -> {
                playNext(currentSentenceIndex)
            }
        }
    }

    fun stopPlayback() {
        TTSManager.stop()
        playbackState = PlaybackState.IDLE
        currentSentenceIndex = -1
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SwissColor.muted)
                .border(width = SwissBorder.width, color = SwissColor.border)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { togglePlayback() }
                .padding(SwissSpace.space3),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = when (playbackState) {
                    PlaybackState.PLAYING -> "⏸"
                    PlaybackState.PAUSED -> "▶"
                    PlaybackState.IDLE -> "▶"
                },
                style = SwissTypography.title,
                color = SwissColor.fg
            )
            Spacer(modifier = Modifier.width(SwissSpace.space2))
            Text(
                text = when (playbackState) {
                    PlaybackState.PLAYING -> "PAUSE"
                    PlaybackState.PAUSED -> "CONTINUE"
                    PlaybackState.IDLE -> "PLAY SENTENCES"
                },
                style = SwissTypography.labelLg,
                color = SwissColor.fg
            )

            if (playbackState != PlaybackState.IDLE) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "■  STOP",
                    style = SwissTypography.label,
                    color = SwissColor.accent,
                    modifier = Modifier.clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { stopPlayback() }
                )
            }
        }

        Spacer(modifier = Modifier.height(SwissSpace.space3))

        for ((index, sentence) in sentences.withIndex()) {
            val isCurrent = index == currentSentenceIndex && playbackState != PlaybackState.IDLE

            SentenceText(
                sentence = sentence,
                isHighlighted = isCurrent,
                onWordClick = onWordClick
            )
            Spacer(modifier = Modifier.height(SwissSpace.space1))
        }
    }
}

@Composable
private fun SentenceText(
    sentence: String,
    isHighlighted: Boolean,
    onWordClick: (String) -> Unit
) {
    val words = remember(sentence) { sentence.split(Regex("\\s+")).filter { it.isNotBlank() } }
    val bgColor = if (isHighlighted) SwissColor.muted else SwissColor.bg

    val annotatedString = remember(sentence, isHighlighted) {
        buildAnnotatedString {
            for ((i, word) in words.withIndex()) {
                val cleanWord = word.replace(Regex("[^a-zA-Z'-]"), "")
                if (cleanWord.isNotBlank()) {
                    pushStringAnnotation(tag = "WORD", annotation = cleanWord)
                    withStyle(
                        style = SpanStyle(
                            color = if (isHighlighted) SwissColor.accent else SwissColor.fg,
                            fontWeight = if (isHighlighted) androidx.compose.ui.text.font.FontWeight.SemiBold
                                else androidx.compose.ui.text.font.FontWeight.Normal
                        )
                    ) {
                        append(word)
                    }
                    pop()
                } else {
                    withStyle(
                        style = SpanStyle(
                            color = if (isHighlighted) SwissColor.accent else SwissColor.fg
                        )
                    ) {
                        append(word)
                    }
                }
                if (i < words.size - 1) append(" ")
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .padding(vertical = SwissSpace.space1, horizontal = SwissSpace.space2)
    ) {
        ClickableText(
            text = annotatedString,
            style = SwissTypography.body.copy(
                lineHeight = SwissTypography.body.fontSize * 1.5
            ),
            onClick = { offset ->
                annotatedString.getStringAnnotations(
                    tag = "WORD",
                    start = offset,
                    end = offset
                ).firstOrNull()?.let { annotation ->
                    onWordClick(annotation.item)
                }
            }
        )
    }
}
