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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neoconcept.ui.theme.SwissBorder
import com.neoconcept.ui.theme.SwissColor
import com.neoconcept.ui.theme.SwissSpace
import com.neoconcept.ui.theme.SwissTouch
import com.neoconcept.ui.theme.SwissTypography

@Composable
fun ExitModal(
    visible: Boolean,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    if (!visible) return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.4f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onCancel() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SwissColor.bg)
                .border(width = SwissBorder.width, color = SwissColor.border)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { }
                .padding(SwissSpace.space5),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "✕",
                    style = SwissTypography.title,
                    color = SwissColor.fg,
                    modifier = Modifier.clickable { onCancel() }
                )
            }

            Spacer(modifier = Modifier.height(SwissSpace.space4))

            Text(
                text = "LEAVE LESSON?",
                style = SwissTypography.titleLg,
                color = SwissColor.fg,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(SwissSpace.space3))

            Text(
                text = "Progress will be saved automatically",
                style = SwissTypography.body,
                color = SwissColor.fg.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(SwissSpace.space6))

            SwissButton(
                text = "LEAVE",
                onClick = onConfirm,
                isPrimary = true
            )

            Spacer(modifier = Modifier.height(SwissSpace.space3))

            SwissButton(
                text = "STAY",
                onClick = onCancel,
                isPrimary = false
            )
        }
    }
}

@Composable
private fun SwissButton(
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
