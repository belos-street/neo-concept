import { useState, useMemo } from 'react'
import { View, Text, Pressable, ScrollView, StyleSheet } from 'react-native'
import { Button, Divider } from '@shared/components'
import { color, border, space, typography } from '@shared/theme'
import type { Lesson } from '@shared/types'

interface ReadingStepProps {
  lesson: Lesson
  onComplete: () => void
  onRetry?: () => void
}

export function ReadingStep({ lesson, onComplete, onRetry }: ReadingStepProps) {
  const { questions } = lesson.reading
  const passage = lesson.passage.text

  const [qIdx, setQIdx] = useState(0)
  const [selected, setSelected] = useState<number | null>(null)
  const [submitted, setSubmitted] = useState(false)
  const [score, setScore] = useState(0)
  const [highlightedEvidence, setHighlightedEvidence] = useState<string | null>(null)

  const current = questions[qIdx]
  const isCorrect = selected === current?.answer

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
    setHighlightedEvidence(current.evidence)
  }

  const handleNext = () => {
    if (qIdx < questions.length - 1) {
      setQIdx((i) => i + 1)
      setSelected(null)
      setSubmitted(false)
      setHighlightedEvidence(null)
    }
  }

  const allDone = qIdx >= questions.length - 1 && submitted

  const passageParts = useMemo(() => {
    if (!highlightedEvidence) return [{ text: passage, highlight: false }]
    const idx = passage.indexOf(highlightedEvidence)
    if (idx === -1) return [{ text: passage, highlight: false }]
    return [
      { text: passage.slice(0, idx), highlight: false },
      { text: highlightedEvidence, highlight: true },
      { text: passage.slice(idx + highlightedEvidence.length), highlight: false }
    ]
  }, [passage, highlightedEvidence])

  if (questions.length === 0) {
    return (
      <View style={styles.empty}>
        <Text style={styles.emptyText}>No reading questions</Text>
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
          <Text style={styles.resultTitle}>READING COMPLETE</Text>
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

        <View style={styles.passageBox}>
          <Text style={styles.passageText}>
            {passageParts.map((part, i) => (
              <Text
                key={i}
                style={part.highlight ? styles.highlightedText : undefined}
              >
                {part.text}
              </Text>
            ))}
          </Text>
        </View>

        <Divider />

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

        {submitted ? (
          <View style={styles.evidenceBox}>
            <Text style={styles.evidenceLabel}>EVIDENCE</Text>
            <Text style={styles.evidenceText}>{current.evidence}</Text>
          </View>
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
            title={allDone ? 'COMPLETE' : 'NEXT →'}
            onPress={allDone ? onComplete : handleNext}
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
  passageBox: {
    borderWidth: border.width,
    borderColor: color.border,
    padding: space[4],
    marginBottom: space[4]
  },
  passageText: { ...typography.body, fontSize: 15, lineHeight: 24 },
  highlightedText: {
    backgroundColor: color.accent,
    color: color.bg,
    fontWeight: '600'
  },
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
  evidenceBox: {
    marginTop: space[3],
    borderLeftWidth: 3,
    borderLeftColor: color.accent,
    paddingLeft: space[3]
  },
  evidenceLabel: { ...typography.label, marginBottom: 2 },
  evidenceText: { ...typography.body, fontStyle: 'italic', opacity: 0.7 },
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
