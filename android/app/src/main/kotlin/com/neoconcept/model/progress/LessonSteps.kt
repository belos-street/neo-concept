package com.neoconcept.model.progress

object LessonSteps {
    const val TEXT_READING = 1
    const val FILL_BLANKS = 2
    const val SPELLING = 3
    const val COMPREHENSION = 4
    const val SPEAKING = 5
    const val COMPLETION = 6

    fun label(step: Int): String =
        when (step) {
            TEXT_READING -> "课文阅读"
            FILL_BLANKS -> "填词练习"
            SPELLING -> "拼写练习"
            COMPREHENSION -> "阅读理解"
            SPEAKING -> "口语练习"
            COMPLETION -> "完成页"
            else -> ""
        }
}
