package com.neoconcept.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.neoconcept.ui.theme.SwissRed
import com.neoconcept.ui.theme.White

@Composable
fun ContinueCard(
    bookTitle: String,
    lessonTitle: String,
    stepLabel: String,
    completedLessons: Int,
    totalLessons: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val progress = if (totalLessons == 0) 0f else completedLessons.toFloat() / totalLessons

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.cardColors(containerColor = SwissRed),
        border = BorderStroke(2.dp, SwissRed),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "继续学习",
                style = MaterialTheme.typography.titleLarge,
                color = White,
            )
            Text(
                text = "$bookTitle · $lessonTitle · $stepLabel",
                style = MaterialTheme.typography.bodyMedium,
                color = White.copy(alpha = 0.85f),
                modifier = Modifier.padding(top = 4.dp, bottom = 10.dp),
            )

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .border(1.dp, White.copy(alpha = 0.4f))
                        .background(White.copy(alpha = 0.25f))
                        .clip(RoundedCornerShape(0.dp)),
            ) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress)
                            .background(White),
                )
            }

            Text(
                text = "已完成 $completedLessons / $totalLessons 课",
                style = MaterialTheme.typography.labelLarge,
                color = White.copy(alpha = 0.9f),
                modifier = Modifier.padding(top = 6.dp, bottom = 12.dp),
            )

            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(0.dp),
                colors = ButtonDefaults.buttonColors(containerColor = White, contentColor = SwissRed),
                border = BorderStroke(2.dp, White),
            ) {
                Text(
                    text = "继续学习",
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}
