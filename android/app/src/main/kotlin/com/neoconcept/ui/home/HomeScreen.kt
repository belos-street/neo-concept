package com.neoconcept.ui.home

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.neoconcept.R
import com.neoconcept.model.content.IndexedBook
import com.neoconcept.ui.components.BookCard
import com.neoconcept.ui.components.ContinueCard
import com.neoconcept.ui.theme.Black
import com.neoconcept.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onSettingsClick: () -> Unit = {},
    onContinueClick: (ContinueProgress) -> Unit = {},
    onBookClick: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState) {
        val success = uiState as? HomeUiState.Success ?: return@LaunchedEffect
        if (success.hasCorruptedEntries) {
            Toast.makeText(
                context,
                R.string.content_partially_unavailable,
                Toast.LENGTH_LONG,
            ).show()
        }
    }

    Scaffold(
        topBar = { HomeTopBar(onSettingsClick = onSettingsClick) },
        containerColor = White,
    ) { innerPadding ->
        HomeContent(
            uiState = uiState,
            onContinueClick = onContinueClick,
            onBookClick = onBookClick,
            modifier = Modifier.padding(innerPadding),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeTopBar(onSettingsClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "Neo Concept",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
            )
        },
        navigationIcon = {
            OutlinedIconButton(
                onClick = onSettingsClick,
                modifier =
                    Modifier
                        .padding(start = 8.dp)
                        .size(40.dp),
                shape = RoundedCornerShape(0.dp),
                border = BorderStroke(2.dp, Black),
                colors = IconButtonDefaults.outlinedIconButtonColors(containerColor = White),
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = Black,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = White),
    )
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onContinueClick: (ContinueProgress) -> Unit,
    onBookClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        when (uiState) {
            HomeUiState.Loading ->
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

            is HomeUiState.Error ->
                Text(
                    text = uiState.message,
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge,
                )

            is HomeUiState.Success ->
                BookList(
                    books = uiState.books,
                    continueProgress = uiState.continueProgress,
                    onContinueClick = onContinueClick,
                    onBookClick = onBookClick,
                    modifier = Modifier.fillMaxSize(),
                )
        }
    }
}

@Composable
private fun BookList(
    books: List<IndexedBook>,
    continueProgress: ContinueProgress?,
    onContinueClick: (ContinueProgress) -> Unit,
    onBookClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        continueProgress?.let { progress ->
            item(key = "continue") {
                ContinueCard(
                    bookTitle = progress.bookTitle,
                    lessonTitle = progress.lessonTitle,
                    stepLabel = progress.stepLabel,
                    completedLessons = 0,
                    totalLessons = progress.totalLessons,
                    onClick = { onContinueClick(progress) },
                    modifier = Modifier.padding(bottom = 14.dp),
                )
            }
        }

        items(
            items = books,
            key = { it.book.id },
        ) { indexedBook ->
            BookCard(
                order = indexedBook.book.order,
                title = indexedBook.book.title,
                totalLessons = indexedBook.book.totalLessons,
                completedLessons = 0,
                onClick = { onBookClick(indexedBook.book.id) },
                modifier = Modifier.padding(bottom = 14.dp),
            )
        }
    }
}
