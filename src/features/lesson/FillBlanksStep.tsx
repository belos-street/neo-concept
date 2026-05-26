import { useState, useRef } from 'react'
import { View, Text, TextInput, ScrollView, StyleSheet } from 'react-native'
import { Button, Divider } from '@shared/components'
import { color, border, space, typography } from '@shared/theme'
import type { Lesson } from '@shared/types'

interface FillBlanksStepProps {
  lesson: Lesson
  onComplete: () => void
}

export function FillBlanksStep({ lesson, onComplete }: FillBlanksStepProps) {
  const { fill_blanks } = lesson
  const gapCount = fill_blanks.gaps.length

  const [inputs, setInputs] = useState<string[]>(
    Array.from({ length: gapCount }, () => '')
  )
  const [submitted, setSubmitted] = useState(false)
  const [score, setScore] = useState(0)
  const inputRefs = useRef<(TextInput | null)[]>([])

  const parts = fill_blanks.template.split('___')

  const handleChangeText = (index: number, text: string) => {
    const next = [...inputs]
    next[index] = text
    setInputs(next)
  }

  const handleSubmit = () => {
    let correct = 0
    inputs.forEach((val, i) => {
      if (val.trim().toLowerCase() === fill_blanks.gaps[i].word.toLowerCase()) {
        correct++
      }
    })
    setScore(correct)
    setSubmitted(true)
  }

  const handleReset = () => {
    setInputs(Array.from({ length: gapCount }, () => ''))
    setSubmitted(false)
    setScore(0)
  }

  return (
    <View style={styles.container}>
      <ScrollView
        style={styles.scroll}
        contentContainerStyle={styles.scrollContent}
      >
        <Text style={styles.title}>FILL IN THE BLANKS</Text>
        <Text style={styles.subtitle}>
          Fill each blank with the correct word. First letters are given as hints.
        </Text>

        <View style={styles.passageBox}>
          {parts.map((segment, i) => (
            <View key={i}>
              <Text style={styles.segmentText}>{segment}</Text>
              {i < gapCount ? (
                <View style={styles.gapWrapper}>
                  <Text style={styles.hintText}>
                    ({fill_blanks.gaps[i].initial}... — {fill_blanks.gaps[i].meaning_cn})
                  </Text>
                  <TextInput
                    ref={(ref) => { inputRefs.current[i] = ref }}
                    style={[
                      styles.gapInput,
                      submitted && styles.gapInputDisabled
                    ]}
                    value={inputs[i]}
                    onChangeText={(text) => handleChangeText(i, text)}
                    editable={!submitted}
                    autoCapitalize="none"
                    autoCorrect={false}
                    returnKeyType={i < gapCount - 1 ? 'next' : 'done'}
                    onSubmitEditing={() => {
                      if (i < gapCount - 1) {
                        inputRefs.current[i + 1]?.focus()
                      }
                    }}
                  />
                  {submitted ? (
                    <Text
                      style={[
                        styles.resultMark,
                        inputs[i].trim().toLowerCase() ===
                        fill_blanks.gaps[i].word.toLowerCase()
                          ? styles.correct
                          : styles.incorrect
                      ]}
                    >
                      {inputs[i].trim().toLowerCase() ===
                      fill_blanks.gaps[i].word.toLowerCase()
                        ? '✓'
                        : `✗ ${fill_blanks.gaps[i].word}`}
                    </Text>
                  ) : null}
                </View>
              ) : null}
            </View>
          ))}
        </View>

        {submitted ? (
          <View style={styles.resultBox}>
            <Text style={styles.resultText}>
              {score} / {gapCount} CORRECT
            </Text>
            <Divider />
            <View style={styles.resultActions}>
              <Button title="TRY AGAIN" variant="secondary" onPress={handleReset} />
              <View style={{ width: 8 }} />
              <Button title="NEXT STEP →" onPress={onComplete} />
            </View>
          </View>
        ) : null}
      </ScrollView>

      {!submitted ? (
        <View style={styles.footer}>
          <Button
            title="SUBMIT"
            onPress={handleSubmit}
            disabled={inputs.some((v) => !v.trim())}
          />
        </View>
      ) : null}
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: color.bg
  },
  scroll: {
    flex: 1
  },
  scrollContent: {
    padding: space[4],
    paddingBottom: space[10]
  },
  title: {
    ...typography.title
  },
  subtitle: {
    ...typography.body,
    opacity: 0.6,
    marginTop: space[1],
    marginBottom: space[4]
  },
  passageBox: {
    borderWidth: border.width,
    borderColor: color.border,
    padding: space[4]
  },
  segmentText: {
    ...typography.body,
    fontSize: 16,
    lineHeight: 26
  },
  gapWrapper: {
    marginVertical: space[1],
    gap: 2
  },
  hintText: {
    ...typography.labelSm,
    color: color.accent,
    marginBottom: 2
  },
  gapInput: {
    borderWidth: border.width,
    borderColor: color.fg,
    height: 40,
    paddingHorizontal: space[2],
    ...typography.body,
    fontSize: 16,
    color: color.fg,
    backgroundColor: color.bg
  },
  gapInputDisabled: {
    opacity: 0.5,
    borderColor: color.border
  },
  resultMark: {
    ...typography.labelLg,
    position: 'absolute',
    right: 0,
    top: 20
  },
  correct: {
    color: color.success
  },
  incorrect: {
    color: color.error
  },
  resultBox: {
    marginTop: space[4]
  },
  resultText: {
    ...typography.title,
    textAlign: 'center',
    marginVertical: space[4]
  },
  resultActions: {
    flexDirection: 'row',
    marginTop: space[4]
  },
  footer: {
    padding: space[4],
    borderTopWidth: border.width,
    borderTopColor: color.border,
    backgroundColor: color.bg
  }
})
