package com.neoconcept.data.repository

import android.content.Context
import com.neoconcept.data.model.*
import org.yaml.snakeyaml.Yaml
import java.io.InputStream

class LessonRepository(private val context: Context) {

    private val yaml = Yaml()

    fun getLesson(lessonId: String): LessonContent? {
        return try {
            val inputStream: InputStream = context.assets.open("mock/$lessonId.yaml")
            val data: Map<String, Any> = yaml.load(inputStream)
            parseLesson(data)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseLesson(data: Map<String, Any>): LessonContent {
        return LessonContent(
            id = data["id"] as String,
            title = data["title"] as String,
            bookId = data["book_id"] as String,
            unitId = data["unit_id"] as String,
            grammarPoints = parseGrammarPoints(data["grammar_points"] as List<Map<String, Any>>?),
            newVocabulary = parseVocabulary(data["new_vocabulary"] as List<Map<String, Any>>?),
            reviewVocabulary = (data["review_vocabulary"] as List<String>?) ?: emptyList(),
            passage = parsePassage(data["passage"] as Map<String, Any>?),
            fillBlanks = parseFillBlanks(data["fill_blanks"] as Map<String, Any>?),
            vocabularyExercises = parseVocabExercises(data["vocabulary_exercises"] as Map<String, Any>?),
            listening = parseListening(data["listening"] as Map<String, Any>?),
            reading = parseReading(data["reading"] as Map<String, Any>?),
            speaking = parseSpeaking(data["speaking"] as Map<String, Any>?)
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseGrammarPoints(data: List<Map<String, Any>>?): List<GrammarPoint> {
        if (data == null) return emptyList()
        return data.map { item ->
            GrammarPoint(
                name = item["name"] as String,
                explanation = item["explanation"] as String,
                examples = (item["examples"] as List<String>?) ?: emptyList()
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseVocabulary(data: List<Map<String, Any>>?): List<VocabularyItem> {
        if (data == null) return emptyList()
        return data.map { item ->
            VocabularyItem(
                word = item["word"] as String,
                phonetic = item["phonetic"] as String,
                definitionCn = item["definition_cn"] as String,
                partOfSpeech = item["part_of_speech"] as String,
                example = item["example"] as String
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun parsePassage(data: Map<String, Any>?): Passage {
        if (data == null) return Passage("", "", 0, emptyList())
        val images = (data["images"] as List<Map<String, Any>>?)?.map { img ->
            PassageImage(
                url = img["url"] as String,
                alt = img["alt"] as String
            )
        } ?: emptyList()
        return Passage(
            title = data["title"] as String,
            text = data["text"] as String,
            wordCount = (data["word_count"] as Number?)?.toInt() ?: 0,
            images = images
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseFillBlanks(data: Map<String, Any>?): FillBlanks {
        if (data == null) return FillBlanks("", emptyList())
        val gaps = (data["gaps"] as List<Map<String, Any>>?)?.map { gap ->
            FillBlankGap(
                gapIndex = (gap["gap_index"] as Number).toInt(),
                word = gap["word"] as String,
                initial = gap["initial"] as String,
                pos = gap["pos"] as String,
                meaningCn = gap["meaning_cn"] as String
            )
        } ?: emptyList()
        return FillBlanks(
            template = data["template"] as String,
            gaps = gaps
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseVocabExercises(data: Map<String, Any>?): VocabularyExercises {
        if (data == null) return VocabularyExercises(emptyList(), emptyList(), emptyList())
        val flashcards = (data["flashcards"] as List<Number>?)?.map { it.toInt() } ?: emptyList()
        val spelling = (data["spelling"] as List<Number>?)?.map { it.toInt() } ?: emptyList()
        val matching = (data["matching"] as List<Map<String, Any>>?)?.map { item ->
            MatchingItem(
                enIndex = (item["en_index"] as Number).toInt(),
                cnOptions = (item["cn_options"] as List<String>),
                answer = (item["answer"] as Number).toInt()
            )
        } ?: emptyList()
        return VocabularyExercises(flashcards, spelling, matching)
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseListening(data: Map<String, Any>?): Listening {
        if (data == null) return Listening(emptyList())
        val questions = (data["questions"] as List<Map<String, Any>>?)?.map { q ->
            ListeningQuestion(
                type = q["type"] as String,
                audioSegment = q["audio_segment"] as String,
                question = q["question"] as String,
                options = (q["options"] as List<String>),
                answer = (q["answer"] as Number).toInt()
            )
        } ?: emptyList()
        return Listening(questions)
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseReading(data: Map<String, Any>?): Reading {
        if (data == null) return Reading(emptyList())
        val questions = (data["questions"] as List<Map<String, Any>>?)?.map { q ->
            ReadingQuestion(
                type = q["type"] as String,
                question = q["question"] as String,
                options = (q["options"] as List<String>),
                answer = (q["answer"] as Number).toInt(),
                evidence = q["evidence"] as String? ?: ""
            )
        } ?: emptyList()
        return Reading(questions)
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseSpeaking(data: Map<String, Any>?): Speaking {
        if (data == null) return Speaking(emptyList())
        val sentences = (data["sentences"] as List<Map<String, Any>>?)?.map { s ->
            SpeakingSentence(
                text = s["text"] as String,
                difficulty = (s["difficulty"] as Number).toInt()
            )
        } ?: emptyList()
        return Speaking(sentences)
    }
}
