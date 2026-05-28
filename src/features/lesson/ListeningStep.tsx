import { useState } from 'react'
import { View, Text, Pressable, ScrollView, StyleSheet } from 'react-native'
import { Button, Divider } from '@shared/components'
import { piper } from '@native/piper'
import { color, border, space, typography } from '@shared/theme'
import type { Lesson } from '@shared/types'

interface ListeningStepProps {
  lesson: Lesson
  onComplete: () => void
  onRetry?: () => void
}

export function ListeningStep({ lesson, onComplete, onRetry }: ListeningStepProps) {
  const { questions } = lesson.listening

  const [qIdx, setQIdx] = useState(0)
  const [selected, setSelected] = useState<number | null>(null)
  const [submitted, setSubmitted] = useState(false)
  const [score, setScore] = useState(0)
  const [isPlaying, setIsPlaying] = useState(false)

  const current = questions[qIdx]
  const isCorrect = selected === current?.answer

  const handlePlay = async () => {
    if (isPlaying) {
      await piper.stop()
      setIsPlaying(false)
      return
    }
    setIsPlaying(true)
    try {
      await piper.init()
      await piper.speak(current.audio_segment)
    } catch (e) {
      console.warn('[TTS] listen error:', e)
    }
    setIsPlaying(false)
  }

  const handleSelect = (optIdx: number) => {
    if (submitted) return
    setSelected(optIdx)
  }

  const handleSubmit = () => {
    if (selected === null) return
    setSubmitted(true)
    if (selected === current.answer) {
      setScore((s) => s + 1)
    }
  }

  const handleNext = () => {
    if (qIdx < questions.length - 1) {
      setQIdx((i) => i + 1)
      setSelected(null)
      setSubmitted(false)
    }
  }

  const totalScore = score + (submitted && isCorrect ? 0 : 0)
  const isLast = qIdx >= questions.length - 1 && submitted
  const allDone = qIdx >= questions.length - 1 && submitted

  if (questions.length === 0) {
    return (
      <View style={styles.empty}>
        <Text style={styles.emptyText}>No listening questions</Text>
        <Button title="NEXT STEP →" onPress={onComplete} />
      </View>
    )
  }

  if (allDone) {
    return (
      <View style={styles.container}>
        <ScrollView
          style={styles.scroll}
          contentContainerStyle={styles.scrollContent}
        >
          <Text style={styles.resultTitle}>LISTENING COMPLETE</Text>
          <Text style={styles.resultScore}>
            {score} / {questions.length} CORRECT
          </Text>
          <Divider />
          <View style={styles.resultActions}>
            <Button title="REDO" variant="secondary" onPress={onRetry} />
            <View style={{ width: 8 }} />
            <Button title="NEXT STEP →" onPress={onComplete} />
          </View>
        </ScrollView>
      </View>
    )
  }

  return (
    <View style={styles.container}>
      <ScrollView
        style={styles.scroll}
        contentContainerStyle={styles.scrollContent}
      >
        <Text style={styles.progress}>
          {qIdx + 1} / {questions.length}
        </Text>

        <Pressable
          style={[styles.playBtn, isPlaying && styles.playBtnActive]}
          onPress={handlePlay}
        >
          <Text style={[styles.playText, isPlaying && styles.playTextActive]}>
            {isPlaying ? 'STOP AUDIO' : 'PLAY AUDIO'}
          </Text>
        </Pressable>

        <Text style={styles.question}>{current.question}</Text>

        {current.options.map((opt, i) => {
          const isSelected = selected === i
          const isAnswer = submitted && i === current.answer
          const isWrong = submitted && isSelected && i !== current.answer

          return (
            <Pressable
              key={i}
              style={[
                styles.option,
                isSelected && styles.optionSelected,
                isAnswer && styles.optionCorrect,
                isWrong && styles.optionWrong
              ]}
              onPress={() => handleSelect(i)}
              disabled={submitted}
            >
              <Text
                style={[
                  styles.optionLabel,
                  isSelected && styles.optionLabelSelected,
                  isAnswer && styles.optionLabelCorrect,
                  isWrong && styles.optionLabelWrong
                ]}
              >
                {String.fromCharCode(65 + i)}.
              </Text>
              <Text
                style={[
                  styles.optionText,
                  isSelected && styles.optionTextSelected,
                  isAnswer && styles.optionTextCorrect,
                  isWrong && styles.optionTextWrong
                ]}
              >
                {opt}
              </Text>
            </Pressable>
          )
        })}

        {submitted && !isCorrect ? (
          <Text style={styles.feedback}>
            CORRECT ANSWER: {String.fromCharCode(65 + current.answer)}.{' '}
            {current.options[current.answer]}
          </Text>
        ) : null}
      </ScrollView>

      <View style={styles.footer}>
        {!submitted ? (
          <Button
            title="SUBMIT"
            onPress={handleSubmit}
            disabled={selected === null}
          />
        ) : (
          <Button
            title={isLast ? 'COMPLETE' : 'NEXT →'}
            onPress={isLast ? onComplete : handleNext}
          />
        )}
      </View>
    </View>
  )
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: color.bg },
  scroll: { flex: 1 },
  scrollContent: { padding: space[4], paddingBottom: space[10] },
  empty: { flex: 1, justifyContent: 'center', alignItems: 'center', padding: space[6] },
  emptyText: { ...typography.body, opacity: 0.5, marginBottom: space[4] },
  progress: { ...typography.labelLg, textAlign: 'center', marginBottom: space[4] },
  playBtn: {
    borderWidth: border.width,
    borderColor: color.border,
    paddingVertical: space[3],
    alignItems: 'center',
    marginBottom: space[4],
    backgroundColor: color.bg
  },
  playBtnActive: { backgroundColor: color.fg, borderColor: color.fg },
  playText: { ...typography.labelLg, color: color.fg },
  playTextActive: { color: color.bg },
  question: { ...typography.title, marginBottom: space[3], textTransform: 'none' },
  option: {
    flexDirection: 'row',
    alignItems: 'center',
    borderWidth: border.width,
    borderColor: color.border,
    padding: space[3],
    marginBottom: space[2]
  },
  optionSelected: { backgroundColor: color.fg, borderColor: color.fg },
  optionCorrect: { borderColor: color.success },
  optionWrong: { borderColor: color.error, backgroundColor: color.error },
  optionLabel: { ...typography.labelLg, width: 28, color: color.fg },
  optionLabelSelected: { color: color.bg },
  optionLabelCorrect: { color: color.success },
  optionLabelWrong: { color: color.bg },
  optionText: { ...typography.body, flex: 1, color: color.fg },
  optionTextSelected: { color: color.bg },
  optionTextCorrect: { color: color.success },
  optionTextWrong: { color: color.bg },
  feedback: { ...typography.body, color: color.accent, marginTop: space[3] },
  resultTitle: { ...typography.title, textAlign: 'center', marginTop: space[8] },
  resultScore: { ...typography.titleLg, textAlign: 'center', marginVertical: space[4] },
  resultActions: { flexDirection: 'row', marginTop: space[6] },
  footer: {
    padding: space[4],
    borderTopWidth: border.width,
    borderTopColor: color.border,
    backgroundColor: color.bg
  }
})
