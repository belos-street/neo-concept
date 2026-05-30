package com.neoconcept.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.neoconcept.ui.theme.SwissBorder
import com.neoconcept.ui.theme.SwissColor
import com.neoconcept.ui.theme.SwissSpace
import com.neoconcept.ui.theme.SwissTypography

@Composable
fun StepProgressBar(
    current: Int,
    total: Int,
    completed: List<Int>,
    labels: List<String>,
    modifier: Modifier = Modifier,
    onStepPress: ((Int) -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(SwissColor.muted)
            .padding(vertical = SwissSpace.space3, horizontal = SwissSpace.space4),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Top
    ) {
        for (i in 0 until total) {
            val isCompleted = i in completed
            val isCurrent = i == current

            StepDot(
                index = i,
                label = labels.getOrElse(i) { "" },
                isCompleted = isCompleted,
                isCurrent = isCurrent,
                isLast = i == total - 1,
                onClick = { onStepPress?.invoke(i) }
            )
        }
    }
}

@Composable
private fun StepDot(
    index: Int,
    label: String,
    isCompleted: Boolean,
    isCurrent: Boolean,
    isLast: Boolean,
    onClick: () -> Unit
) {
    val dotSize = 12.dp
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val backgroundColor = when {
        isCompleted -> SwissColor.fg
        isCurrent -> SwissColor.bg
        else -> SwissColor.muted
    }
    val borderColor = when {
        isCompleted -> SwissColor.fg
        isCurrent -> SwissColor.accent
        else -> SwissColor.fg.copy(alpha = 0.2f)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .graphicsLayer {
                scaleX = if (isPressed) 0.97f else 1f
                scaleY = if (isPressed) 0.97f else 1f
            }
            .clickable(
                indication = null,
                interactionSource = interactionSource
            ) { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(dotSize)
                .clip(CircleShape)
                .background(backgroundColor)
                .border(
                    width = if (isCurrent) SwissBorder.width else 0.dp,
                    color = borderColor,
                    shape = CircleShape
                )
        )

        Text(
            text = label.uppercase(),
            style = SwissTypography.caption,
            color = if (isCompleted || isCurrent) SwissColor.fg else SwissColor.fg.copy(alpha = 0.4f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = SwissSpace.space1)
        )
    }
}
