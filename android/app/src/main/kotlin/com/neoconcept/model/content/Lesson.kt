package com.neoconcept.model.content

import kotlinx.serialization.Serializable

@Serializable
data class Lesson(
    val id: String,
    val bookId: String,
    val displayNumber: String,
    val title: String,
    val subtitle: String,
    val banner: Banner,
    val introduction: Introduction,
    val text: Text,
    val vocabulary: List<VocabularyItem>,
    val exercises: Exercises,
)

@Serializable
data class Banner(
    val local: String? = null,
    val remote: String? = null,
    val placeholder: String,
)

@Serializable
data class Introduction(
    val knowledgePoints: List<String>,
    val speakingScenarios: List<String>,
    val learningObjectives: List<String>,
)

@Serializable
data class Text(
    val paragraphs: List<Paragraph>,
)

@Serializable
data class Paragraph(
    val id: String,
    val sentences: List<Sentence>,
)

@Serializable
data class Sentence(
    val id: String,
    val text: String,
    val normalizedText: String? = null,
)

@Serializable
data class VocabularyItem(
    val id: String,
    val word: String,
    val phonetic: String,
    val translation: String,
    val example: String,
    val contextSentence: String,
)

@Serializable
data class Exercises(
    val fillInBlanks: List<FillInBlank>,
    val spelling: List<Spelling>,
    val comprehension: Comprehension,
    val speaking: Speaking,
)

@Serializable
data class FillInBlank(
    val id: String,
    val sentence: String,
    val answer: String,
    val options: List<String>,
)

@Serializable
data class Spelling(
    val id: String,
    val vocabularyId: String,
)

@Serializable
data class Comprehension(
    val questions: List<ComprehensionQuestion>,
)

@Serializable
data class ComprehensionQuestion(
    val id: String,
    val question: String,
    val options: List<String>,
    val answer: Int,
    val explanation: String,
)

@Serializable
data class Speaking(
    val sentences: List<Sentence>,
)
