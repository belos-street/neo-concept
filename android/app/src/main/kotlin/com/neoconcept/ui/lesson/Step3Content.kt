package com.neoconcept.ui.lesson

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neoconcept.model.content.Lesson
import com.neoconcept.model.content.VocabularyItem
import com.neoconcept.ui.theme.Black
import com.neoconcept.ui.theme.LockedGray
import com.neoconcept.ui.theme.MutedGray
import com.neoconcept.ui.theme.SuccessGreen
import com.neoconcept.ui.theme.SwissRed
import com.neoconcept.ui.theme.WarningOrange
import com.neoconcept.ui.theme.White

private const val TAG = "MockTTS"

@Composable
fun Step3Content(
    lesson: Lesson,
    onCanProceedChange: (Boolean) -> Unit,
    onStepComplete: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val vocabulary = lesson.vocabulary
    var currentIndex by remember { mutableIntStateOf(0) }
    var input by remember { mutableStateOf("") }
    var errorCount by remember { mutableIntStateOf(0) }
    var showAnswer by remember { mutableStateOf(false) }

    val currentItem = vocabulary.getOrNull(currentIndex)

    LaunchedEffect(currentIndex) {
        input = ""
        errorCount = 0
        showAnswer = false
    }

    fun onSubmit() {
        val item = currentItem ?: return
        val normalizedInput = input.trim()
        if (normalizedInput.isEmpty()) return

        if (showAnswer) {
            moveToNext(vocabulary, currentIndex) { currentIndex = it }
            return
        }

        if (normalizedInput.equals(item.word, ignoreCase = true)) {
            val nextIndex = currentIndex + 1
            if (nextIndex >= vocabulary.size) {
                onCanProceedChange(true)
                onStepComplete()
            } else {
                moveToNext(vocabulary, currentIndex) { currentIndex = it }
            }
        } else {
            errorCount++
            if (errorCount >= 2) {
                showAnswer = true
            }
        }
    }

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = "拼写练习",
            style = MaterialTheme.typography.labelLarge,
            color = SwissRed,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp),
        )
        Spacer(modifier = Modifier.height(12.dp))

        currentItem?.let { item ->
            SpellingCard(
                index = currentIndex + 1,
                total = vocabulary.size,
                item = item,
                input = input,
                onInputChange = { input = it },
                errorCount = errorCount,
                showAnswer = showAnswer,
                onPlayClick = { Log.d(TAG, "Play TTS: ${item.word}") },
                onSubmit = ::onSubmit,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        } ?: run {
            Text(
                text = "本课暂无拼写词汇",
                style = MaterialTheme.typography.bodyLarge,
                color = LockedGray,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

private fun moveToNext(
    vocabulary: List<VocabularyItem>,
    currentIndex: Int,
    setIndex: (Int) -> Unit,
) {
    val nextIndex = currentIndex + 1
    if (nextIndex < vocabulary.size) {
        setIndex(nextIndex)
    }
}

@Composable
private fun SpellingCard(
    index: Int,
    total: Int,
    item: VocabularyItem,
    input: String,
    onInputChange: (String) -> Unit,
    errorCount: Int,
    showAnswer: Boolean,
    onPlayClick: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val contextHint = remember(item.contextSentence, item.word) {
        blankWordInSentence(item.contextSentence, item.word)
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = MutedGray),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "单词 $index / $total",
                style = MaterialTheme.typography.labelLarge,
                color = LockedGray,
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = item.translation,
                style = MaterialTheme.typography.headlineSmall,
                color = Black,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable(onClick = onPlayClick),
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(32.dp)
                            .background(White)
                            .border(BorderStroke(2.dp, Black)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play pronunciation",
                        tint = Black,
                        modifier = Modifier.size(20.dp),
                    )
                }
                Text(
                    text = item.phonetic,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Black,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedTextField(
                value = input,
                onValueChange = onInputChange,
                label = { Text("请输入英文单词") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onSubmit() }),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(0.dp),
                colors =
                    OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = White,
                        unfocusedContainerColor = White,
                        focusedBorderColor = Black,
                        unfocusedBorderColor = Black,
                        focusedLabelColor = LockedGray,
                        unfocusedLabelColor = LockedGray,
                    ),
            )

            Spacer(modifier = Modifier.height(16.dp))

            val buttonLabel =
                when {
                    showAnswer -> "继续"
                    errorCount > 0 -> "再试一次"
                    else -> "提交"
                }
            Button(
                onClick = onSubmit,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = SwissRed,
                        contentColor = White,
                    ),
                border = BorderStroke(2.dp, Black),
                shape = MaterialTheme.shapes.extraSmall,
            ) {
                Text(
                    text = buttonLabel,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            if (errorCount == 1 && !showAnswer) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "上下文提示",
                    style = MaterialTheme.typography.labelLarge,
                    color = WarningOrange,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = contextHint,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Black,
                )
            }

            if (showAnswer) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "正确答案",
                    style = MaterialTheme.typography.labelLarge,
                    color = SwissRed,
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.word,
                    style = MaterialTheme.typography.headlineSmall,
                    color = SuccessGreen,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

private fun blankWordInSentence(
    sentence: String,
    word: String,
): String {
    val regex = Regex(Regex.escape(word), RegexOption.IGNORE_CASE)
    return regex.replaceFirst(sentence, "______")
}
