package com.neoconcept.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.neoconcept.ui.theme.SwissBorder
import com.neoconcept.ui.theme.SwissColor
import com.neoconcept.ui.theme.SwissSpace
import com.neoconcept.ui.theme.SwissTypography

@Composable
fun GrammarCard(
    name: String,
    explanation: String,
    examples: List<String>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(SwissColor.muted)
            .border(width = SwissBorder.width, color = SwissColor.border)
            .padding(SwissSpace.space4)
    ) {
        Text(
            text = name.uppercase(),
            style = SwissTypography.label,
            color = SwissColor.fg
        )

        Spacer(modifier = Modifier.height(SwissSpace.space2))

        Text(
            text = explanation,
            style = SwissTypography.bodySm,
            color = SwissColor.fg
        )

        if (examples.isNotEmpty()) {
            Spacer(modifier = Modifier.height(SwissSpace.space3))

            examples.forEach { example ->
                Text(
                    text = example,
                    style = SwissTypography.bodySm,
                    color = SwissColor.fg.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = SwissSpace.space1)
                )
            }
        }
    }
}
