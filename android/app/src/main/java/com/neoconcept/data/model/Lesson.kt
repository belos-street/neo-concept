package com.neoconcept.data.model

data class LessonContent(
    val id: String,
    val title: String,
    val bookId: String,
    val unitId: String,
    val grammarPoints: List<GrammarPoint>,
    val newVocabulary: List<VocabularyItem>,
    val reviewVocabulary: List<String>,
    val passage: Passage,
    val fillBlanks: FillBlanks,
    val vocabularyExercises: VocabularyExercises,
    val listening: Listening,
    val reading: Reading,
    val speaking: Speaking
)

data class GrammarPoint(
    val name: String,
    val explanation: String,
    val examples: List<String>
)

data class Passage(
    val title: String,
    val text: String,
    val wordCount: Int,
    val images: List<PassageImage>
)

data class PassageImage(
    val url: String,
    val alt: String
)

data class FillBlanks(
    val template: String,
    val gaps: List<FillBlankGap>
)

data class FillBlankGap(
    val gapIndex: Int,
    val word: String,
    val initial: String,
    val pos: String,
    val meaningCn: String
)

data class VocabularyExercises(
    val flashcards: List<Int>,
    val spelling: List<Int>,
    val matching: List<MatchingItem>
)

data class MatchingItem(
    val enIndex: Int,
    val cnOptions: List<String>,
    val answer: Int
)

data class Listening(
    val questions: List<ListeningQuestion>
)

data class ListeningQuestion(
    val type: String,
    val audioSegment: String,
    val question: String,
    val options: List<String>,
    val answer: Int
)

data class Reading(
    val questions: List<ReadingQuestion>
)

data class ReadingQuestion(
    val type: String,
    val question: String,
    val options: List<String>,
    val answer: Int,
    val evidence: String
)

data class Speaking(
    val sentences: List<SpeakingSentence>
)

data class SpeakingSentence(
    val text: String,
    val difficulty: Int
)
