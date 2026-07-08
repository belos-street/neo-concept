package com.neoconcept.ui.bookdetail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.neoconcept.model.progress.LessonStatus
import com.neoconcept.ui.theme.Black
import com.neoconcept.ui.theme.SuccessGreen
import com.neoconcept.ui.theme.SwissRed
import com.neoconcept.ui.theme.WarningOrange
import com.neoconcept.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    viewModel: BookDetailViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            BookDetailTopBar(
                title = bookDetailTitle(uiState),
                onBackClick = onBackClick,
            )
        },
        containerColor = White,
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
        ) {
            when (val state = uiState) {
                BookDetailUiState.Loading ->
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                is BookDetailUiState.Error ->
                    Text(
                        text = state.message,
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge,
                    )

                is BookDetailUiState.Success ->
                    LessonGroupedList(
                        groups = state.groups,
                        modifier = Modifier.fillMaxSize(),
                    )
            }
        }
    }
}

private fun bookDetailTitle(uiState: BookDetailUiState): String =
    when (uiState) {
        is BookDetailUiState.Success -> uiState.book.title.uppercase()
        else -> ""
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookDetailTopBar(
    title: String,
    onBackClick: () -> Unit,
) {
    TopAppBar(
        title = { Text(text = title) },
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
private fun LessonGroupedList(
    groups: List<LessonGroup>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        groups.forEach { group ->
            item(key = "header_${group.startIndex}") {
                GroupHeader(startIndex = group.startIndex, endIndex = group.endIndex)
            }
            group.lessons.forEach { lesson ->
                item(key = lesson.lessonId) {
                    LessonRowItem(lesson = lesson)
                }
                item(key = "divider_${lesson.lessonId}") {
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                        thickness = 1.dp,
                        color = androidx.compose.ui.graphics.Color(0xFFDDDDDD),
                    )
                }
            }
        }
    }
}

@Composable
private fun GroupHeader(
    startIndex: Int,
    endIndex: Int,
) {
    Text(
        text = "LESSONS ${startIndex.toString().padStart(2, '0')}-${endIndex.toString().padStart(2, '0')}",
        modifier =
            Modifier
                .fillMaxWidth()
                .background(White)
                .padding(horizontal = 20.dp, vertical = 12.dp),
        style = MaterialTheme.typography.labelLarge,
        color = SwissRed,
    )
}

@Composable
private fun LessonRowItem(lesson: LessonRow) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Text(
            text = lesson.displayNumber,
            style = MaterialTheme.typography.headlineSmall,
            color = Black,
            modifier = Modifier.width(44.dp),
        )
        Text(
            text = lesson.title,
            style = MaterialTheme.typography.bodyLarge,
            color = Black,
            modifier = Modifier.weight(1f),
        )
        LessonStatusIcon(status = lesson.status)
    }
}

@Composable
private fun LessonStatusIcon(status: LessonStatus) {
    Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
        when (status) {
            LessonStatus.NOT_STARTED -> {
                Box(
                    modifier =
                        Modifier
                            .size(14.dp)
                            .border(2.dp, Black, RoundedCornerShape(50)),
                )
            }

            LessonStatus.IN_PROGRESS -> {
                Box(
                    modifier =
                        Modifier
                            .size(12.dp)
                            .background(SwissRed, RoundedCornerShape(50)),
                )
            }

            LessonStatus.COMPLETED -> {
                Text(
                    text = "✓",
                    color = SuccessGreen,
                    style = MaterialTheme.typography.titleLarge,
                )
            }

            LessonStatus.SPEAKING_PENDING -> {
                Box {
                    Text(
                        text = "✓",
                        color = SuccessGreen,
                        style = MaterialTheme.typography.titleLarge,
                    )
                    Box(
                        modifier =
                            Modifier
                                .align(Alignment.TopEnd)
                                .size(8.dp)
                                .background(WarningOrange, RoundedCornerShape(50))
                                .border(1.dp, White, RoundedCornerShape(50)),
                    )
                }
            }
        }
    }
}
