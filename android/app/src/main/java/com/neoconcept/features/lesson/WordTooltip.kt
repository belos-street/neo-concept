package com.neoconcept.features.lesson

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.neoconcept.data.model.VocabularyItem
import com.neoconcept.data.repository.EcdictRepository
import com.neoconcept.ui.theme.SwissBorder
import com.neoconcept.ui.theme.SwissColor
import com.neoconcept.ui.theme.SwissSpace
import com.neoconcept.ui.theme.SwissTypography
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun WordTooltip(
    word: String,
    ecdictRepository: EcdictRepository,
    onDismiss: () -> Unit
) {
    var entry by remember { mutableStateOf<VocabularyItem?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(word) {
        isLoading = true
        entry = withContext(Dispatchers.IO) {
            ecdictRepository.lookup(word.lowercase().trim())
        }
        isLoading = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SwissColor.fg.copy(alpha = 0.4f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SwissSpace.space6)
                .background(SwissColor.bg)
                .border(width = SwissBorder.widthThick, color = SwissColor.fg)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { }
                .padding(SwissSpace.space5)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = word.lowercase(),
                    style = SwissTypography.titleSm.copy(fontSize = SwissTypography.titleSm.fontSize),
                    color = SwissColor.fg,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "✕",
                    style = SwissTypography.title,
                    color = SwissColor.fg,
                    modifier = Modifier.clickable { onDismiss() }
                )
            }

            Spacer(modifier = Modifier.height(SwissSpace.space2))

            if (isLoading) {
                Text(
                    text = "...",
                    style = SwissTypography.body,
                    color = SwissColor.fg.copy(alpha = 0.4f)
                )
            } else if (entry != null) {
                if (entry!!.phonetic.isNotBlank()) {
                    Text(
                        text = "/${entry!!.phonetic.trim('/')}/",
                        style = SwissTypography.bodySm,
                        color = SwissColor.fg.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(SwissSpace.space2))
                }

                if (entry!!.partOfSpeech.isNotBlank()) {
                    Text(
                        text = entry!!.partOfSpeech,
                        style = SwissTypography.label,
                        color = SwissColor.fg.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(SwissSpace.space2))
                }

                Text(
                    text = entry!!.definitionCn,
                    style = SwissTypography.body,
                    color = SwissColor.fg
                )

                if (entry!!.example.isNotBlank()) {
                    Spacer(modifier = Modifier.height(SwissSpace.space3))
                    Text(
                        text = entry!!.example,
                        style = SwissTypography.bodySm,
                        color = SwissColor.fg.copy(alpha = 0.7f)
                    )
                }
            } else {
                Text(
                    text = "未找到释义",
                    style = SwissTypography.bodySm,
                    color = SwissColor.fg.copy(alpha = 0.4f)
                )
            }

            Spacer(modifier = Modifier.height(SwissSpace.space4))

            Text(
                text = "点击空白处关闭",
                style = SwissTypography.caption,
                color = SwissColor.accent,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
