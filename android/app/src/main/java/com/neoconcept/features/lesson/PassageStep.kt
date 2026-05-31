package com.neoconcept.features.lesson

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.neoconcept.data.model.LessonContent
import com.neoconcept.ui.components.GrammarCard
import com.neoconcept.ui.theme.SwissBorder
import com.neoconcept.ui.theme.SwissColor
import com.neoconcept.ui.theme.SwissSpace
import com.neoconcept.ui.theme.SwissTypography

@Composable
fun PassageStep(
    lesson: LessonContent,
    onComplete: () -> Unit
) {
    val scrollState = rememberScrollState()
    val passageTitle = remember { lesson.passage.title }
    val passageText = remember { lesson.passage.text }
    val passageImages = remember { lesson.passage.images }
    val grammarPoints = remember { lesson.grammarPoints }

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

        Text(
            text = passageText.trim(),
            style = SwissTypography.body,
            color = SwissColor.fg,
            lineHeight = SwissTypography.body.fontSize * 1.5
        )

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

        Spacer(modifier = Modifier.height(SwissSpace.space10))
    }
}
