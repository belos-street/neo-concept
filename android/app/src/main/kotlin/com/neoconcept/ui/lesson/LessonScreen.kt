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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.neoconcept.model.content.Lesson
import com.neoconcept.ui.theme.Black
import com.neoconcept.ui.theme.LockedGray
import com.neoconcept.ui.theme.MutedGray
import com.neoconcept.ui.theme.SuccessGreen
import com.neoconcept.ui.theme.SwissRed
import com.neoconcept.ui.theme.White

private const val TOTAL_STEPS = 6

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(
    viewModel: LessonViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onComplete: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            LessonTopBar(
                uiState = uiState,
                onBackClick = onBackClick,
            )
        },
        bottomBar = {
            LessonBottomBar(
                uiState = uiState,
                onNextClick = {
                    val state = uiState as? LessonUiState.Success
                    if (state != null && state.currentStep >= TOTAL_STEPS) {
                        onComplete()
                    } else {
                        viewModel.moveToNextStep()
                    }
                },
                onStepClick = { viewModel.goToStep(it) },
            )
        },
        containerColor = White,
    ) { innerPadding ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(White),
        ) {
            when (val state = uiState) {
                LessonUiState.Loading ->
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                is LessonUiState.Error ->
                    Text(
                        text = state.message,
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge,
                    )

                is LessonUiState.Success ->
                    StepContent(
                        lesson = state.lesson,
                        currentStep = state.currentStep,
                        modifier = Modifier.fillMaxSize(),
                    )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LessonTopBar(
    uiState: LessonUiState,
    onBackClick: () -> Unit,
) {
    TopAppBar(
        title = { LessonTopBarTitle(uiState = uiState) },
        navigationIcon = {
            OutlinedIconButton(
                onClick = onBackClick,
                modifier = Modifier.padding(start = 8.dp).size(40.dp),
                shape = RoundedCornerShape(0.dp),
                border = BorderStroke(2.dp, Black),
                colors = IconButtonDefaults.outlinedIconButtonColors(containerColor = White),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Black,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = White),
    )
}

@Composable
private fun LessonTopBarTitle(uiState: LessonUiState) {
    when (uiState) {
        is LessonUiState.Success -> {
            Column {
                Text(
                    text = uiState.lesson.displayNumber.uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    color = SwissRed,
                )
                Text(
                    text = uiState.lesson.title.uppercase(),
                    style = MaterialTheme.typography.titleLarge,
                    color = Black,
                )
            }
        }

        else -> {}
    }
}

@Composable
private fun StepContent(
    lesson: Lesson,
    currentStep: Int,
    modifier: Modifier = Modifier,
) {
    if (currentStep == 1) {
        Step1Content(
            lesson = lesson,
            modifier = modifier,
        )
    } else {
        StepPlaceholder(
            lesson = lesson,
            currentStep = currentStep,
            modifier = modifier,
        )
    }
}

@Composable
private fun StepPlaceholder(
    lesson: Lesson,
    currentStep: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = lesson.displayNumber.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = LockedGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "STEP $currentStep",
            style = MaterialTheme.typography.headlineLarge,
            color = Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = lesson.title,
            style = MaterialTheme.typography.bodyLarge,
            color = Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "（Step $currentStep 内容占位，后续步骤实现）",
            style = MaterialTheme.typography.bodyMedium,
            color = LockedGray,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun LessonBottomBar(
    uiState: LessonUiState,
    onNextClick: () -> Unit,
    onStepClick: (Int) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(White)
                .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        if (uiState is LessonUiState.Success) {
            StepIndicator(
                currentStep = uiState.currentStep,
                onStepClick = onStepClick,
            )
            Spacer(modifier = Modifier.height(16.dp))
            NextStepButton(
                currentStep = uiState.currentStep,
                onClick = onNextClick,
            )
        }
    }
}

@Composable
private fun StepIndicator(
    currentStep: Int,
    onStepClick: (Int) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        for (step in 1..TOTAL_STEPS) {
            StepDot(
                step = step,
                state = stepState(step, currentStep),
                onClick = { onStepClick(step) },
            )
            if (step < TOTAL_STEPS) {
                Box(
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(2.dp)
                            .background(if (step < currentStep) Black else MutedGray),
                )
            }
        }
    }
}

private fun stepState(
    step: Int,
    currentStep: Int,
): StepDotState =
    when {
        step < currentStep -> StepDotState.Completed
        step == currentStep -> StepDotState.Current
        else -> StepDotState.Future
    }

private enum class StepDotState {
    Completed,
    Current,
    Future,
}

@Composable
private fun StepDot(
    step: Int,
    state: StepDotState,
    onClick: () -> Unit,
) {
    val backgroundColor =
        when (state) {
            StepDotState.Completed -> Black
            StepDotState.Current -> SwissRed
            StepDotState.Future -> White
        }
    val contentColor =
        when (state) {
            StepDotState.Completed -> White
            StepDotState.Current -> White
            StepDotState.Future -> LockedGray
        }
    val border =
        if (state == StepDotState.Future) {
            BorderStroke(2.dp, LockedGray)
        } else {
            null
        }
    val modifier =
        Modifier
            .size(32.dp)
            .background(backgroundColor, RoundedCornerShape(50))
            .let { if (border != null) it.border(border, RoundedCornerShape(50)) else it }
            .clickable(onClick = onClick)

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        when (state) {
            StepDotState.Completed ->
                Text(
                    text = "✓",
                    color = SuccessGreen,
                    style = MaterialTheme.typography.titleLarge,
                )

            else ->
                Text(
                    text = step.toString(),
                    color = contentColor,
                    style = MaterialTheme.typography.bodyLarge,
                )
        }
    }
}

@Composable
private fun NextStepButton(
    currentStep: Int,
    onClick: () -> Unit,
) {
    val label = if (currentStep < TOTAL_STEPS) "下一步" else "完成"
    Button(
        onClick = onClick,
        modifier =
            Modifier
                .fillMaxWidth()
                .height(52.dp),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = SwissRed,
                contentColor = White,
            ),
        border = BorderStroke(2.dp, Black),
        shape = MaterialTheme.shapes.extraSmall,
    ) {
        Text(
            text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}
