package com.neoconcept.ui.completion

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.neoconcept.model.content.BookLessonRef
import com.neoconcept.model.content.VocabularyItem
import com.neoconcept.ui.theme.Black
import com.neoconcept.ui.theme.LockedGray
import com.neoconcept.ui.theme.MutedGray
import com.neoconcept.ui.theme.SuccessGreen
import com.neoconcept.ui.theme.SwissRed
import com.neoconcept.ui.theme.White

@Composable
fun CompletionScreen(
    viewModel: CompletionViewModel = hiltViewModel(),
    onBackToBook: () -> Unit = {},
    onNextLesson: (BookLessonRef) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = White,
    ) { innerPadding ->
        when (val state = uiState) {
            CompletionUiState.Loading ->
                CircularProgressIndicator(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(24.dp),
                )

            is CompletionUiState.Error ->
                Text(
                    text = state.message,
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(24.dp),
                    style = MaterialTheme.typography.bodyLarge,
                )

            is CompletionUiState.Success ->
                CompletionContent(
                    state = state,
                    onBackToBook = onBackToBook,
                    onNextLesson = onNextLesson,
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(horizontal = 20.dp, vertical = 24.dp),
                )
        }
    }
}

@Composable
private fun CompletionContent(
    state: CompletionUiState.Success,
    onBackToBook: () -> Unit,
    onNextLesson: (BookLessonRef) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "✓",
            color = SuccessGreen,
            style = MaterialTheme.typography.displayLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "完成学习",
            style = MaterialTheme.typography.headlineMedium,
            color = Black,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "${state.displayNumber} ${state.title}",
            style = MaterialTheme.typography.bodyLarge,
            color = LockedGray,
            textAlign = TextAlign.Center,
        )

        if (state.vocabulary.isNotEmpty()) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "重点词汇",
                style = MaterialTheme.typography.labelLarge,
                color = SwissRed,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(12.dp))
            state.vocabulary.forEach { item ->
                VocabularyCard(item = item)
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(24.dp))

        state.nextLesson?.let { nextLesson ->
            CompletionButton(
                text = "下一课：${nextLesson.displayNumber} ${nextLesson.title}",
                containerColor = SwissRed,
                contentColor = White,
                onClick = { onNextLesson(nextLesson) },
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        CompletionButton(
            text = "返回目录",
            containerColor = White,
            contentColor = Black,
            onClick = onBackToBook,
        )
    }
}

@Composable
private fun VocabularyCard(
    item: VocabularyItem,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = MutedGray),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = item.word,
                style = MaterialTheme.typography.titleMedium,
                color = Black,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = item.phonetic,
                style = MaterialTheme.typography.bodyMedium,
                color = LockedGray,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = item.translation,
                style = MaterialTheme.typography.bodyMedium,
                color = Black,
            )
        }
    }
}

@Composable
private fun CompletionButton(
    text: String,
    containerColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier =
            modifier
                .fillMaxWidth()
                .height(52.dp),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = containerColor,
                contentColor = contentColor,
            ),
        border = BorderStroke(2.dp, Black),
        shape = MaterialTheme.shapes.extraSmall,
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}
