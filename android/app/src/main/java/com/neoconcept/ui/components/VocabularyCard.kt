package com.neoconcept.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.neoconcept.data.model.VocabularyItem
import com.neoconcept.ui.theme.SwissBorder
import com.neoconcept.ui.theme.SwissColor
import com.neoconcept.ui.theme.SwissSpace
import com.neoconcept.ui.theme.SwissTypography

@Composable
fun VocabularyCard(
    vocabulary: List<VocabularyItem>,
    modifier: Modifier = Modifier
) {
    if (vocabulary.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(SwissColor.bg)
            .border(width = SwissBorder.width, color = SwissColor.border)
            .padding(SwissSpace.space4)
    ) {
        Text(
            text = "NEW WORDS",
            style = SwissTypography.label,
            color = SwissColor.fg
        )

        Spacer(modifier = Modifier.height(SwissSpace.space3))

        for ((index, item) in vocabulary.withIndex()) {
            VocabularyRow(item = item)
            if (index < vocabulary.size - 1) {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(SwissBorder.width)
                        .background(SwissColor.muted)
                )
            }
        }
    }
}

@Composable
private fun VocabularyRow(item: VocabularyItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = SwissSpace.space2),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = item.word,
            style = SwissTypography.body.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold),
            color = SwissColor.fg,
            modifier = Modifier.width(SwissSpace.space10 + SwissSpace.space2)
        )

        if (item.phonetic.isNotBlank()) {
            Text(
                text = "/${item.phonetic.trim('/')}/",
                style = SwissTypography.caption,
                color = SwissColor.fg.copy(alpha = 0.5f),
                modifier = Modifier.width(SwissSpace.space10 + SwissSpace.space2)
            )
        }

        Text(
            text = item.definitionCn,
            style = SwissTypography.bodySm,
            color = SwissColor.fg,
            modifier = Modifier.weight(1f)
        )
    }
}
