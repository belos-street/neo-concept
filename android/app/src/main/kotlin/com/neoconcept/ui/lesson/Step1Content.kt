package com.neoconcept.ui.lesson

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.neoconcept.model.content.Lesson
import com.neoconcept.model.content.VocabularyItem
import com.neoconcept.ui.theme.Black
import com.neoconcept.ui.theme.LockedGray
import com.neoconcept.ui.theme.MutedGray
import com.neoconcept.ui.theme.SwissRed
import com.neoconcept.ui.theme.White

private const val TAG = "MockTTS"

@Composable
fun Step1Content(
    lesson: Lesson,
    modifier: Modifier = Modifier,
) {
    val vocabularyMap =
        remember(lesson.vocabulary) {
            lesson.vocabulary.associateBy { it.word.lowercase() }
        }
    var selectedWord by remember { mutableStateOf<SelectedWord?>(null) }

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
    ) {
        lesson.text.paragraphs.forEach { paragraph ->
            Text(
                text = "课文",
                style = MaterialTheme.typography.labelLarge,
                color = SwissRed,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp),
            )
            Spacer(modifier = Modifier.height(12.dp))
            paragraph.sentences.forEach { sentence ->
                Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                    SentenceText(
                        sentence = sentence.text,
                        vocabularyWords = vocabularyMap.keys,
                        onSentenceClick = { text ->
                            Log.d(TAG, "Play TTS: $text")
                        },
                        onWordClick = { word ->
                            selectedWord = SelectedWord(
                                word = word,
                                vocabulary = vocabularyMap[word.lowercase()],
                            )
                        },
                    )
                }
            }
        }

        HorizontalDivider(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            color = MutedGray,
        )

        VocabularySection(
            vocabulary = lesson.vocabulary,
            modifier = Modifier.padding(horizontal = 20.dp),
        )

        Spacer(modifier = Modifier.height(24.dp))
    }

    selectedWord?.let { word ->
        VocabularyDialog(
            selectedWord = word,
            onDismiss = { selectedWord = null },
        )
    }
}

private data class SelectedWord(
    val word: String,
    val vocabulary: VocabularyItem?,
)

private data class WordRange(val start: Int, val end: Int, val word: String)

@Composable
private fun SentenceText(
    sentence: String,
    vocabularyWords: Set<String>,
    onSentenceClick: (String) -> Unit,
    onWordClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val annotatedString =
        remember(sentence, vocabularyWords) {
            buildAnnotatedString {
                Regex("[a-zA-Z]+|[^a-zA-Z]+")
                    .findAll(sentence)
                    .map { it.value }
                    .filter { it.isNotEmpty() }
                    .forEach { token ->
                        if (token.matches(Regex("[a-zA-Z]+"))) {
                            val isVocabulary = vocabularyWords.contains(token.lowercase())
                            val style =
                                if (isVocabulary) {
                                    SpanStyle(
                                        color = SwissRed,
                                        fontWeight = FontWeight.Bold,
                                        textDecoration = TextDecoration.Underline,
                                    )
                                } else {
                                    SpanStyle(
                                        color = Black,
                                        textDecoration = TextDecoration.Underline,
                                    )
                                }
                            val link =
                                LinkAnnotation.Clickable(
                                    tag = token,
                                    styles = TextLinkStyles(style = style),
                                    linkInteractionListener = { onWordClick(token) },
                                )
                            withLink(link) { append(token) }
                        } else {
                            append(token)
                        }
                    }
            }
        }

    val wordRanges =
        remember(sentence, vocabularyWords) {
            val ranges = mutableListOf<WordRange>()
            var index = 0
            Regex("[a-zA-Z]+|[^a-zA-Z]+")
                .findAll(sentence)
                .map { it.value }
                .filter { it.isNotEmpty() }
                .forEach { token ->
                    val start = index
                    val end = index + token.length
                    if (token.matches(Regex("[a-zA-Z]+"))) {
                        ranges.add(WordRange(start, end, token))
                    }
                    index = end
                }
            ranges
        }

    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .pointerInput(sentence) {
                    detectTapGestures { offset ->
                        val layout = textLayoutResult ?: return@detectTapGestures
                        val textOffset = layout.getOffsetForPosition(offset)
                        val clickedWord = wordRanges.find { textOffset in it.start until it.end }
                        if (clickedWord != null) {
                            onWordClick(clickedWord.word)
                        } else {
                            onSentenceClick(sentence)
                        }
                    }
                },
    ) {
        Text(
            text = annotatedString,
            style =
                MaterialTheme.typography.bodyLarge.copy(
                    color = Black,
                    lineHeight = 28.sp,
                ),
            onTextLayout = { textLayoutResult = it },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun VocabularySection(
    vocabulary: List<VocabularyItem>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = "核心词汇",
            style = MaterialTheme.typography.labelLarge,
            color = SwissRed,
        )
        Spacer(modifier = Modifier.height(12.dp))
        vocabulary.forEach { item ->
            Card(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                shape = RoundedCornerShape(0.dp),
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
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "例：${item.example}",
                        style = MaterialTheme.typography.bodySmall,
                        color = LockedGray,
                    )
                }
            }
        }
    }
}

@Composable
private fun VocabularyDialog(
    selectedWord: SelectedWord,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(0.dp),
            colors = CardDefaults.cardColors(containerColor = White),
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = selectedWord.word,
                    style = MaterialTheme.typography.headlineSmall,
                    color = Black,
                    fontWeight = FontWeight.Bold,
                )

                selectedWord.vocabulary?.let { vocabulary ->
                    Text(
                        text = vocabulary.phonetic,
                        style = MaterialTheme.typography.bodyMedium,
                        color = LockedGray,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = vocabulary.translation,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Black,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "例句",
                        style = MaterialTheme.typography.labelLarge,
                        color = SwissRed,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = vocabulary.example,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Black,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "课文原句：${vocabulary.contextSentence}",
                        style = MaterialTheme.typography.bodySmall,
                        color = LockedGray,
                    )
                } ?: run {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "未找到释义",
                        style = MaterialTheme.typography.bodyLarge,
                        color = LockedGray,
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "关闭",
                    style = MaterialTheme.typography.bodyLarge,
                    color = SwissRed,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onDismiss() },
                )
            }
        }
    }
}
