package com.neoconcept.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.neoconcept.ui.theme.Black
import com.neoconcept.ui.theme.MutedGray
import com.neoconcept.ui.theme.SwissRed
import com.neoconcept.ui.theme.White

@Composable
fun BookCard(
    order: Int,
    title: String,
    totalLessons: Int,
    completedLessons: Int,
    modifier: Modifier = Modifier,
) {
    val isStarted = completedLessons > 0
    val progress = if (totalLessons == 0) 0f else completedLessons.toFloat() / totalLessons

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(2.dp, Black),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(18.dp),
        ) {
            Text(
                text = order.toString().padStart(2, '0'),
                style = MaterialTheme.typography.displayLarge,
                color = if (isStarted) SwissRed else Black,
                modifier = Modifier.width(64.dp),
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    color = Black,
                )
                Text(
                    text = "$totalLessons 课 · ${if (isStarted) "进行中" else "未开始"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Black.copy(alpha = 0.55f),
                    modifier = Modifier.padding(top = 4.dp, bottom = 10.dp),
                )

                BookProgressBar(progress = progress, isStarted = isStarted)

                Text(
                    text = "已完成 $completedLessons / $totalLessons",
                    style = MaterialTheme.typography.labelLarge,
                    color = Black.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 6.dp),
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = Black,
            )
        }
    }
}

@Composable
private fun BookProgressBar(
    progress: Float,
    isStarted: Boolean,
) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(6.dp)
                .border(1.dp, Black)
                .background(MutedGray)
                .clip(RoundedCornerShape(0.dp)),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .background(if (isStarted) SwissRed else MutedGray),
        )
    }
}
