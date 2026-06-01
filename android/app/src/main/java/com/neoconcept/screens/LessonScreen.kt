package com.neoconcept.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.neoconcept.data.model.LessonContent
import com.neoconcept.data.repository.EcdictRepository
import com.neoconcept.data.repository.LessonRepository
import com.neoconcept.data.repository.ManifestRepository
import com.neoconcept.data.repository.ProgressRepository
import com.neoconcept.features.lesson.PassageStep
import com.neoconcept.ui.components.ExitModal
import com.neoconcept.ui.components.ResumeOverlay
import com.neoconcept.ui.components.ScreenHeader
import com.neoconcept.ui.components.StepProgressBar
import kotlinx.coroutines.launch

private const val TOTAL_STEPS = 6
private val STEP_LABELS = listOf("课文", "填空", "词汇", "听力", "阅读", "口语")

@Composable
fun LessonScreen(
    lessonId: String,
    manifestRepository: ManifestRepository,
    progressRepository: ProgressRepository,
    lessonRepository: LessonRepository,
    ecdictRepository: EcdictRepository,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val progressMap by progressRepository.progressMap.collectAsState(emptyMap())
    val progress = progressMap[lessonId]

    var showResume by remember { mutableStateOf(false) }
    var showExit by remember { mutableStateOf(false) }
    var initialized by remember { mutableStateOf(false) }
    var lessonData by remember { mutableStateOf<LessonContent?>(null) }

    val lesson = remember { manifestRepository.findLessonById(lessonId) }

    LaunchedEffect(lessonId) {
        lessonData = lessonRepository.getLesson(lessonId)
        if (progress != null && !progress.finished) {
            showResume = true
        } else if (progress == null) {
            progressRepository.startLesson(lessonId, TOTAL_STEPS)
        }
        initialized = true
    }

    val currentStep = progress?.currentStep ?: 0
    val completedSteps = progress?.completedSteps ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ScreenHeader(
            title = lesson?.title ?: "课程",
            onBackClick = { showExit = true }
        )

        StepProgressBar(
            current = currentStep,
            total = TOTAL_STEPS,
            completed = completedSteps,
            labels = STEP_LABELS,
            onStepPress = { stepIndex ->
                if (stepIndex in completedSteps || stepIndex == currentStep) {
                    scope.launch {
                        progressRepository.goToStep(lessonId, stepIndex)
                    }
                }
            }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            when {
                !initialized -> {
                    CircularProgressIndicator()
                }
                lesson == null || lessonData == null -> {
                    Text(
                        text = "课程未找到",
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else -> {
                    when (currentStep) {
                        0 -> PassageStep(
                            lesson = lessonData!!,
                            ecdictRepository = ecdictRepository,
                            onComplete = {
                                scope.launch {
                                    progressRepository.completeStep(lessonId, 0)
                                }
                            }
                        )
                        else -> {
                            Text(
                                text = "Step ${currentStep + 1}: ${STEP_LABELS.getOrElse(currentStep) { "" }}",
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }
                    }
                }
            }
        }
    }

    ResumeOverlay(
        visible = showResume,
        completedSteps = completedSteps.size,
        totalSteps = TOTAL_STEPS,
        lastAccessedAt = progress?.lastAccessedAt ?: System.currentTimeMillis(),
        onResume = {
            showResume = false
            scope.launch {
                progressRepository.goToStep(lessonId, progress?.currentStep ?: 0)
            }
        },
        onRestart = {
            showResume = false
            scope.launch {
                progressRepository.resetLesson(lessonId)
                progressRepository.startLesson(lessonId, TOTAL_STEPS)
            }
        }
    )

    ExitModal(
        visible = showExit,
        onConfirm = {
            showExit = false
            onBack()
        },
        onCancel = { showExit = false }
    )
}
