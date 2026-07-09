package com.neoconcept.ui.intro

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.neoconcept.model.content.Lesson
import com.neoconcept.ui.theme.Black
import com.neoconcept.ui.theme.MutedGray
import com.neoconcept.ui.theme.SwissRed
import com.neoconcept.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntroScreen(
    viewModel: IntroViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onStartLearning: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    OutlinedIconButton(
                        onClick = onBackClick,
                        modifier = Modifier.padding(start = 8.dp).size(40.dp),
                        shape = MaterialTheme.shapes.extraSmall,
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
        },
        containerColor = White,
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
        ) {
            when (val state = uiState) {
                IntroUiState.Loading ->
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                is IntroUiState.Error ->
                    Text(
                        text = state.message,
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.bodyLarge,
                    )

                is IntroUiState.Success ->
                    IntroContent(
                        lesson = state.lesson,
                        onStartLearning = {
                            viewModel.startLearning {
                                onStartLearning()
                            }
                        },
                    )
            }
        }
    }
}

@Composable
private fun IntroContent(
    lesson: Lesson,
    onStartLearning: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
    ) {
        IntroBanner(banner = lesson.banner)

        Text(
            text = lesson.title.uppercase(),
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = (-0.5).sp,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        )

        Spacer(modifier = Modifier.height(20.dp))

        IntroSection(
            label = "知识点",
            items = lesson.introduction.knowledgePoints,
        )
        IntroSection(
            label = "口语场景",
            items = lesson.introduction.speakingScenarios,
        )
        IntroSection(
            label = "学习目标",
            items = lesson.introduction.learningObjectives,
        )

        Spacer(modifier = Modifier.height(32.dp))

        StartLearningButton(onClick = onStartLearning)

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun IntroBanner(banner: com.neoconcept.model.content.Banner) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(bottom = 16.dp),
    ) {
        SubcomposeAsyncImage(
            model = banner.remote,
            contentDescription = null,
            modifier = Modifier.fillMaxWidth().height(180.dp),
            contentScale = ContentScale.Crop,
        ) {
            when (painter.state) {
                is AsyncImagePainter.State.Loading,
                is AsyncImagePainter.State.Error,
                -> {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .background(MutedGray),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = banner.placeholder.uppercase(),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = Black.copy(alpha = 0.5f),
                        )
                    }
                }

                else -> {
                    SubcomposeAsyncImageContent()
                }
            }
        }
    }
}

@Composable
private fun StartLearningButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
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
            text = "开始学习",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun IntroSection(
    label: String,
    items: List<String>,
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 18.dp)) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp,
            color = SwissRed,
            modifier = Modifier.padding(bottom = 6.dp),
        )
        items.forEach { item ->
            Text(
                text = "•  $item",
                fontSize = 14.sp,
                color = Black,
                modifier = Modifier.padding(bottom = 4.dp),
            )
        }
    }
}
