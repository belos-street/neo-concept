package com.neoconcept.ui.lesson

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neoconcept.model.content.ComprehensionQuestion
import com.neoconcept.model.content.Lesson
import com.neoconcept.ui.theme.Black
import com.neoconcept.ui.theme.LockedGray
import com.neoconcept.ui.theme.MutedGray
import com.neoconcept.ui.theme.SuccessGreen
import com.neoconcept.ui.theme.SwissRed
import com.neoconcept.ui.theme.WarningOrange
import com.neoconcept.ui.theme.White

@Composable
fun Step4Content(
    lesson: Lesson,
    onCanProceedChange: (Boolean) -> Unit,
    onStepComplete: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val questions = lesson.exercises.comprehension.questions
    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableIntStateOf(-1) }
    var answeredCorrectly by remember { mutableStateOf(false) }

    val currentQuestion = questions.getOrNull(currentIndex)

    LaunchedEffect(currentIndex) {
        selectedOption = -1
        answeredCorrectly = false
    }

    fun onOptionClick(index: Int) {
        if (answeredCorrectly) return
        selectedOption = index
        if (index == currentQuestion?.answer) {
            answeredCorrectly = true
            val nextIndex = currentIndex + 1
            if (nextIndex >= questions.size) {
                onCanProceedChange(true)
            }
        }
    }

    fun onNext() {
        if (!answeredCorrectly) return
        val nextIndex = currentIndex + 1
        if (nextIndex >= questions.size) {
            onStepComplete()
        } else {
            currentIndex = nextIndex
        }
    }

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = "阅读理解",
            style = MaterialTheme.typography.labelLarge,
            color = SwissRed,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp),
        )
        Spacer(modifier = Modifier.height(12.dp))

        currentQuestion?.let { question ->
            QuestionCard(
                index = currentIndex + 1,
                total = questions.size,
                question = question,
                selectedOption = selectedOption,
                answeredCorrectly = answeredCorrectly,
                onOptionClick = ::onOptionClick,
                onNext = ::onNext,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        } ?: run {
            Text(
                text = "本课暂无阅读理解题",
                style = MaterialTheme.typography.bodyLarge,
                color = LockedGray,
                modifier = Modifier.padding(horizontal = 20.dp),
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun QuestionCard(
    index: Int,
    total: Int,
    question: ComprehensionQuestion,
    selectedOption: Int,
    answeredCorrectly: Boolean,
    onOptionClick: (Int) -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isWrong = selectedOption != -1 && selectedOption != question.answer && !answeredCorrectly

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = MutedGray),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "题目 $index / $total",
                style = MaterialTheme.typography.labelLarge,
                color = LockedGray,
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = question.question,
                style = MaterialTheme.typography.titleMedium,
                color = Black,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(16.dp))

            question.options.forEachIndexed { optionIndex, option ->
                OptionItem(
                    option = option,
                    index = optionIndex,
                    selectedOption = selectedOption,
                    correctAnswer = question.answer,
                    answeredCorrectly = answeredCorrectly,
                    onClick = { onOptionClick(optionIndex) },
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (isWrong) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "答错了，请重新选择",
                    style = MaterialTheme.typography.bodyMedium,
                    color = WarningOrange,
                    fontWeight = FontWeight.Bold,
                )
            }

            if (answeredCorrectly) {
                Spacer(modifier = Modifier.height(16.dp))
                val isLast = index == total
                Button(
                    onClick = onNext,
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
                        text = if (isLast) "完成" else "下一题",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun OptionItem(
    option: String,
    index: Int,
    selectedOption: Int,
    correctAnswer: Int,
    answeredCorrectly: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isSelected = index == selectedOption
    val isCorrectOption = index == correctAnswer
    val showCorrect = answeredCorrectly && isCorrectOption
    val showWrong = isSelected && !isCorrectOption && !answeredCorrectly

    val backgroundColor =
        when {
            showCorrect -> SuccessGreen
            showWrong -> WarningOrange
            isSelected -> SwissRed
            else -> White
        }
    val contentColor =
        when {
            showCorrect || showWrong || isSelected -> White
            else -> Black
        }
    val borderColor =
        when {
            showCorrect -> SuccessGreen
            showWrong -> WarningOrange
            isSelected -> SwissRed
            else -> Black
        }

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .border(BorderStroke(2.dp, borderColor))
                .clickable(enabled = !answeredCorrectly, onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier =
                Modifier
                    .padding(end = 12.dp)
                    .size(28.dp)
                    .background(White)
                    .border(BorderStroke(2.dp, borderColor)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = ('A' + index).toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = borderColor,
                fontWeight = FontWeight.Bold,
            )
        }
        Text(
            text = option,
            style = MaterialTheme.typography.bodyLarge,
            color = contentColor,
            fontWeight = if (isSelected || showCorrect) FontWeight.Bold else FontWeight.Normal,
        )
    }
}
