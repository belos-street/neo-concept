package com.neoconcept.ui.lesson

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neoconcept.model.content.Lesson
import com.neoconcept.model.content.Sentence
import com.neoconcept.ui.theme.Black
import com.neoconcept.ui.theme.LockedGray
import com.neoconcept.ui.theme.MutedGray
import com.neoconcept.ui.theme.SwissRed
import com.neoconcept.ui.theme.WarningOrange
import com.neoconcept.ui.theme.White

@Composable
fun Step5Content(
    lesson: Lesson,
    onCanProceedChange: (Boolean) -> Unit,
    onSkipSpeaking: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sentences = lesson.exercises.speaking.sentences
    var hasRecorded by remember { mutableStateOf(false) }
    var recordingIndex by remember { mutableIntStateOf(-1) }

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = "口语练习",
            style = MaterialTheme.typography.labelLarge,
            color = SwissRed,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp),
        )
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "跟读下面的句子，点击麦克风按钮开始录音（当前为模拟录音）。",
            style = MaterialTheme.typography.bodyMedium,
            color = LockedGray,
            modifier = Modifier.padding(horizontal = 20.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))

        sentences.forEachIndexed { index, sentence ->
            SpeakingSentenceCard(
                index = index + 1,
                sentence = sentence,
                isRecording = recordingIndex == index,
                onMicClick = {
                    recordingIndex = if (recordingIndex == index) {
                        hasRecorded = true
                        onCanProceedChange(true)
                        -1
                    } else {
                        index
                    }
                },
                modifier = Modifier.padding(horizontal = 20.dp),
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onSkipSpeaking,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(horizontal = 20.dp),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = White,
                    contentColor = Black,
                ),
            border = BorderStroke(2.dp, Black),
            shape = MaterialTheme.shapes.extraSmall,
        ) {
            Text(
                text = "跳过口语",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SpeakingSentenceCard(
    index: Int,
    sentence: Sentence,
    isRecording: Boolean,
    onMicClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = MutedGray),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "$index.",
                style = MaterialTheme.typography.bodyLarge,
                color = LockedGray,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 12.dp),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = sentence.text,
                    style = MaterialTheme.typography.titleMedium,
                    color = Black,
                    fontWeight = FontWeight.Bold,
                )
                sentence.normalizedText?.let { normalized ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = normalized,
                        style = MaterialTheme.typography.bodyMedium,
                        color = LockedGray,
                    )
                }
            }
            Box(
                modifier =
                    Modifier
                        .size(48.dp)
                        .background(
                            color = if (isRecording) WarningOrange else SwissRed,
                            shape = RoundedCornerShape(50),
                        )
                        .clickable(onClick = onMicClick),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = if (isRecording) "停止录音" else "开始录音",
                    tint = White,
                    modifier = Modifier.size(24.dp),
                )
            }
        }

        if (isRecording) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(SwissRed)
                        .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "录音中…",
                    style = MaterialTheme.typography.bodyMedium,
                    color = White,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}
