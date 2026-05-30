package com.neoconcept.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.neoconcept.data.model.Book
import com.neoconcept.data.model.CourseUnit
import com.neoconcept.data.model.Lesson
import com.neoconcept.data.model.LessonStatus
import com.neoconcept.data.repository.ManifestRepository
import com.neoconcept.ui.theme.SwissBorder
import com.neoconcept.ui.theme.SwissColor
import com.neoconcept.ui.theme.SwissSpace
import com.neoconcept.ui.theme.SwissTypography

@Composable
fun CourseListScreen(
    manifestRepository: ManifestRepository,
    onLessonClick: (String) -> kotlin.Unit = {}
) {
    val manifest by manifestRepository.manifest.collectAsState()
    val isLoading by manifestRepository.isLoading.collectAsState()
    val error by manifestRepository.error.collectAsState()

    LaunchedEffect(Unit) {
        manifestRepository.loadManifest()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SwissColor.bg)
    ) {
        ScreenHeader(title = "COURSES")

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = SwissColor.fg
                    )
                }
                error != null -> {
                    Text(
                        text = error ?: "Unknown error",
                        modifier = Modifier.align(Alignment.Center),
                        color = SwissColor.accent
                    )
                }
                manifest != null -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        manifest?.books?.forEach { book ->
                            item {
                                BookRow(
                                    book = book,
                                    onLessonClick = onLessonClick
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScreenHeader(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .border(width = SwissBorder.width, color = SwissColor.border)
            .padding(horizontal = SwissSpace.space4),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = SwissTypography.titleSm,
            color = SwissColor.fg
        )
    }
}

@Composable
private fun BookRow(
    book: Book,
    onLessonClick: (String) -> kotlin.Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val lessonCount = book.units.sumOf { it.lessons.size }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clickable { expanded = !expanded }
                .padding(horizontal = SwissSpace.space4),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = book.title.uppercase(),
                style = SwissTypography.title,
                color = SwissColor.fg,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = lessonCount.toString(),
                    style = SwissTypography.label,
                    color = SwissColor.fg
                )
                Spacer(modifier = Modifier.width(SwissSpace.space2))
                Icon(
                    imageVector = if (expanded) Icons.Filled.KeyboardArrowDown
                    else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    modifier = Modifier.size(24.dp),
                    tint = SwissColor.fg
                )
            }
        }
        Divider()

        if (expanded) {
            book.units.forEach { unit ->
                UnitRow(
                    unit = unit,
                    onLessonClick = onLessonClick
                )
            }
        }
    }
}

@Composable
private fun UnitRow(
    unit: CourseUnit,
    onLessonClick: (String) -> kotlin.Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clickable { expanded = !expanded }
                .padding(start = 16.dp, end = SwissSpace.space4),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = unit.title.uppercase(),
                style = SwissTypography.titleSm,
                color = SwissColor.fg,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (expanded) Icons.Filled.KeyboardArrowDown
                else Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = if (expanded) "Collapse" else "Expand",
                modifier = Modifier.size(20.dp),
                tint = SwissColor.fg
            )
        }
        Divider()

        if (expanded) {
            unit.lessons.forEach { lesson ->
                LessonCard(
                    lesson = lesson,
                    onClick = { onLessonClick(lesson.id) }
                )
            }
        }
    }
}

@Composable
private fun LessonCard(
    lesson: Lesson,
    onClick: () -> kotlin.Unit
) {
    val isLocked = lesson.status == LessonStatus.LOCKED

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .padding(start = 32.dp, end = SwissSpace.space4, top = SwissSpace.space1)
            .then(
                if (isLocked) {
                    Modifier.background(SwissColor.muted)
                } else {
                    Modifier
                        .border(width = SwissBorder.width, color = SwissColor.border)
                        .clickable(onClick = onClick)
                }
            )
            .padding(horizontal = SwissSpace.space4),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        StatusDot(status = lesson.status)
        Spacer(modifier = Modifier.width(SwissSpace.space3))
        Text(
            text = lesson.title,
            style = SwissTypography.body,
            color = if (isLocked) SwissColor.fg.copy(alpha = 0.5f) else SwissColor.fg,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        if (lesson.status == LessonStatus.UPDATE_AVAILABLE) {
            Text(
                text = "UPDATE",
                style = SwissTypography.label,
                color = SwissColor.accent,
                modifier = Modifier.clickable { }
            )
        } else {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = if (isLocked) SwissColor.fg.copy(alpha = 0.5f) else SwissColor.fg
            )
        }
    }
}

@Composable
private fun StatusDot(status: LessonStatus) {
    val (backgroundColor, borderColor) = when (status) {
        LessonStatus.DOWNLOADED -> SwissColor.fg to SwissColor.fg
        LessonStatus.LOCKED -> SwissColor.muted to SwissColor.muted
        LessonStatus.UPDATE_AVAILABLE -> SwissColor.accent to SwissColor.accent
        else -> SwissColor.bg to SwissColor.fg.copy(alpha = 0.3f)
    }

    Box(
        modifier = Modifier
            .size(12.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(width = SwissBorder.width, color = borderColor, shape = CircleShape)
    )
}

@Composable
private fun Divider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(SwissBorder.width)
            .background(SwissColor.border)
    )
}
