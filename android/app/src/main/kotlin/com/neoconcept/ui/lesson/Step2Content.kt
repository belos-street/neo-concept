package com.neoconcept.ui.lesson

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.neoconcept.model.content.FillInBlank
import com.neoconcept.model.content.Lesson
import com.neoconcept.ui.theme.Black
import com.neoconcept.ui.theme.LockedGray
import com.neoconcept.ui.theme.MutedGray
import com.neoconcept.ui.theme.SuccessGreen
import com.neoconcept.ui.theme.SwissRed
import com.neoconcept.ui.theme.WarningOrange
import com.neoconcept.ui.theme.White

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Step2Content(
    lesson: Lesson,
    onCanProceedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val exercises = lesson.exercises.fillInBlanks
    val selectedAnswers = remember { mutableStateMapOf<String, String>() }
    val allCorrect =
        exercises.isNotEmpty() &&
            exercises.all { exercise ->
                selectedAnswers[exercise.id].equals(exercise.answer, ignoreCase = true)
            }

    LaunchedEffect(selectedAnswers.size, allCorrect) {
        onCanProceedChange(allCorrect)
    }

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = "填词练习",
            style = MaterialTheme.typography.labelLarge,
            color = SwissRed,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp),
        )
        Spacer(modifier = Modifier.height(12.dp))

        exercises.forEachIndexed { index, exercise ->
            ExerciseCard(
                index = index + 1,
                exercise = exercise,
                selectedAnswer = selectedAnswers[exercise.id],
                onOptionClick = { option ->
                    selectedAnswers[exercise.id] = option
                },
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ExerciseCard(
    index: Int,
    exercise: FillInBlank,
    selectedAnswer: String?,
    onOptionClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val parts = exercise.sentence.split("______")
    val isCorrect = selectedAnswer.equals(exercise.answer, ignoreCase = true)
    val isWrong = selectedAnswer != null && !isCorrect

    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        Text(
            text = "题目 $index",
            style = MaterialTheme.typography.labelLarge,
            color = LockedGray,
        )
        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
        ) {
            parts.forEachIndexed { partIndex, part ->
                if (part.isNotEmpty()) {
                    Text(
                        text = part,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Black,
                        modifier = Modifier.padding(vertical = 4.dp),
                    )
                }
                if (partIndex < parts.size - 1) {
                    BlankBox(
                        answer = selectedAnswer,
                        isCorrect = isCorrect,
                        isWrong = isWrong,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            exercise.options.forEach { option ->
                val optionSelected = option == selectedAnswer
                OptionChip(
                    option = option,
                    isSelected = optionSelected,
                    isCorrect = optionSelected && isCorrect,
                    isWrong = optionSelected && isWrong,
                    isAnswered = isCorrect,
                    onClick = { onOptionClick(option) },
                )
            }
        }

        if (isWrong) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "正确答案：${exercise.answer}",
                style = MaterialTheme.typography.bodyMedium,
                color = SuccessGreen,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun BlankBox(
    answer: String?,
    isCorrect: Boolean,
    isWrong: Boolean,
    modifier: Modifier = Modifier,
) {
    val backgroundColor =
        when {
            isCorrect -> SuccessGreen
            isWrong -> WarningOrange
            else -> MutedGray
        }
    val contentColor =
        when {
            answer != null -> White
            else -> LockedGray
        }

    Box(
        modifier =
            modifier
                .padding(horizontal = 4.dp, vertical = 4.dp)
                .background(backgroundColor)
                .padding(horizontal = 12.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = answer ?: "______",
            style = MaterialTheme.typography.bodyLarge,
            color = contentColor,
            fontWeight = if (answer != null) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center,
            minLines = 1,
        )
    }
}

@Composable
private fun OptionChip(
    option: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    isWrong: Boolean,
    isAnswered: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor =
        when {
            isCorrect -> SuccessGreen
            isWrong -> WarningOrange
            isSelected -> SwissRed
            isAnswered -> MutedGray
            else -> White
        }
    val contentColor =
        when {
            isCorrect || isWrong || isSelected -> White
            isAnswered -> LockedGray
            else -> Black
        }
    val borderColor =
        when {
            isCorrect -> SuccessGreen
            isWrong -> WarningOrange
            isSelected -> SwissRed
            else -> Black
        }

    Box(
        modifier =
            modifier
                .background(backgroundColor, RoundedCornerShape(0.dp))
                .border(BorderStroke(2.dp, borderColor), RoundedCornerShape(0.dp))
                .clickable(enabled = !isAnswered, onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = option,
            style = MaterialTheme.typography.bodyLarge,
            color = contentColor,
            fontWeight = FontWeight.Bold,
        )
    }
}
