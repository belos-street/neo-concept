package com.neoconcept.features.lesson

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.neoconcept.data.model.LessonContent
import com.neoconcept.data.repository.EcdictRepository
import com.neoconcept.ui.components.GrammarCard
import com.neoconcept.ui.components.VocabularyCard
import com.neoconcept.ui.theme.SwissBorder
import com.neoconcept.ui.theme.SwissColor
import com.neoconcept.ui.theme.SwissSpace
import com.neoconcept.ui.theme.SwissTouch
import com.neoconcept.ui.theme.SwissTypography

@Composable
fun PassageStep(
    lesson: LessonContent,
    ecdictRepository: EcdictRepository,
    onComplete: () -> Unit
) {
    val scrollState = rememberScrollState()
    val passageTitle = remember { lesson.passage.title }
    val passageImages = remember { lesson.passage.images }
    val grammarPoints = remember { lesson.grammarPoints }
    val newVocabulary = remember { lesson.newVocabulary }

    val sentences = remember(lesson.passage.text) {
        lesson.passage.text.trim()
            .split(Regex("(?<=[.!?])\\s+"))
            .filter { it.isNotBlank() }
    }

    var tooltipWord by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SwissColor.bg)
                .verticalScroll(scrollState)
                .padding(SwissSpace.space4)
        ) {
            Text(
                text = passageTitle.uppercase(),
                style = SwissTypography.titleLg,
                color = SwissColor.fg
            )

            Spacer(modifier = Modifier.height(SwissSpace.space6))

            for (img in passageImages) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 3f)
                        .border(width = SwissBorder.width, color = SwissColor.border)
                ) {
                    AsyncImage(
                        model = img.url,
                        contentDescription = img.alt,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.height(SwissSpace.space3))
            }

            Spacer(modifier = Modifier.height(SwissSpace.space4))

            SentencePlayer(
                sentences = sentences,
                onWordClick = { word -> tooltipWord = word }
            )

            if (newVocabulary.isNotEmpty()) {
                Spacer(modifier = Modifier.height(SwissSpace.space6))
                VocabularyCard(vocabulary = newVocabulary)
            }

            if (grammarPoints.isNotEmpty()) {
                Spacer(modifier = Modifier.height(SwissSpace.space6))

                for (grammar in grammarPoints) {
                    GrammarCard(
                        name = grammar.name,
                        explanation = grammar.explanation,
                        examples = grammar.examples
                    )
                    Spacer(modifier = Modifier.height(SwissSpace.space3))
                }
            }

            Spacer(modifier = Modifier.height(SwissSpace.space6))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(SwissTouch.minHeight)
                    .background(SwissColor.fg)
                    .border(width = SwissBorder.width, color = SwissColor.border)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onComplete() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "下一步 →",
                    style = SwissTypography.labelLg,
                    color = SwissColor.bg
                )
            }

            Spacer(modifier = Modifier.height(SwissSpace.space10))
        }

        tooltipWord?.let { word ->
            WordTooltip(
                word = word,
                ecdictRepository = ecdictRepository,
                onDismiss = { tooltipWord = null }
            )
        }
    }
}
