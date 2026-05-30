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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.neoconcept.ui.theme.SwissBorder
import com.neoconcept.ui.theme.SwissColor
import com.neoconcept.ui.theme.SwissSpace
import com.neoconcept.ui.theme.SwissTouch
import com.neoconcept.ui.theme.SwissTypography
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ResumeOverlay(
    visible: Boolean,
    completedSteps: Int,
    totalSteps: Int,
    lastAccessedAt: Long,
    onResume: () -> Unit,
    onRestart: () -> Unit
) {
    if (!visible) return

    val dateFormat = SimpleDateFormat("MM/dd HH:mm", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(lastAccessedAt))
    val progress = if (totalSteps > 0) completedSteps.toFloat() / totalSteps else 0f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f)),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SwissColor.bg)
                .border(width = SwissBorder.width, color = SwissColor.border)
                .padding(SwissSpace.space5),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "RESUME?",
                style = SwissTypography.titleLg,
                color = SwissColor.fg,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(SwissSpace.space3))

            Text(
                text = "Last accessed: $formattedDate",
                style = SwissTypography.bodySm,
                color = SwissColor.fg.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(SwissSpace.space4))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(SwissBorder.width)
                    .background(SwissColor.muted)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(SwissBorder.width)
                        .background(SwissColor.fg)
                )
            }

            Text(
                text = "$completedSteps / $totalSteps STEPS",
                style = SwissTypography.label,
                color = SwissColor.fg.copy(alpha = 0.5f),
                modifier = Modifier.padding(top = SwissSpace.space2)
            )

            Spacer(modifier = Modifier.height(SwissSpace.space6))

            SwissResumeButton(
                text = "CONTINUE",
                onClick = onResume,
                isPrimary = true
            )

            Spacer(modifier = Modifier.height(SwissSpace.space3))

            SwissResumeButton(
                text = "RESTART",
                onClick = onRestart,
                isPrimary = false
            )
        }
    }
}

@Composable
private fun SwissResumeButton(
    text: String,
    onClick: () -> Unit,
    isPrimary: Boolean
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val backgroundColor = when {
        isPressed -> SwissColor.accent
        isPrimary -> SwissColor.fg
        else -> SwissColor.bg
    }
    val borderColor = when {
        isPressed -> SwissColor.accent
        else -> SwissColor.border
    }
    val textColor = when {
        isPrimary -> SwissColor.bg
        isPressed -> SwissColor.bg
        else -> SwissColor.fg
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(SwissTouch.minHeight)
            .background(backgroundColor)
            .border(width = SwissBorder.width, color = borderColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = SwissTypography.labelLg,
            color = textColor
        )
    }
}
