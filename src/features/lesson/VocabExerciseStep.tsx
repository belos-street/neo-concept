import { useState, useMemo, useEffect } from 'react'
import { View, Text, TextInput, Pressable, ScrollView, StyleSheet } from 'react-native'
import { Button, Divider } from '@shared/components'
import { WordTooltip } from '@features/lesson/WordTooltip'
import { resolveVocab } from '@features/lesson/resolveVocab'
import { color, border, space, typography } from '@shared/theme'
import type { Lesson, VocabularyItem } from '@shared/types'

type SubMode = 'flashcards' | 'spelling' | 'matching'

interface VocabExerciseStepProps {
  lesson: Lesson
  onComplete: () => void
}

export function VocabExerciseStep({ lesson, onComplete }: VocabExerciseStepProps) {
  const { new_vocabulary, vocabulary_exercises } = lesson

  const [resolvedVocab, setResolvedVocab] = useState<VocabularyItem[]>(() =>
    new_vocabulary.map((v) =>
      typeof v === 'string'
        ? { word: v, phonetic: '', definition_cn: '', part_of_speech: '', example: '' }
        : v
    )
  )

  useEffect(() => {
    if (new_vocabulary.length === 0) return
    if (typeof new_vocabulary[0] !== 'string') return
    resolveVocab(new_vocabulary as string[]).then(setResolvedVocab)
  }, [new_vocabulary])

  const [subMode, setSubMode] = useState<SubMode | null>(null)
  const [flashcardIdx, setFlashcardIdx] = useState(0)
  const [flipped, setFlipped] = useState(false)
  const [spellInputs, setSpellInputs] = useState<string[]>(
    Array.from({ length: vocabulary_exercises.spelling.length }, () => '')
  )
  const [spellSubmitted, setSpellSubmitted] = useState(false)
  const [spellScore, setSpellScore] = useState(0)
  const [matchAnswers, setMatchAnswers] = useState<(number | null)[]>(
    Array.from({ length: vocabulary_exercises.matching.length }, () => null)
  )
  const [matchSubmitted, setMatchSubmitted] = useState(false)
  const [matchScore, setMatchScore] = useState(0)
  const [tooltip, setTooltip] = useState<VocabularyItem | null>(null)

  const currentWord = resolvedVocab[vocabulary_exercises.flashcards[flashcardIdx]]
  const flashcardCount = vocabulary_exercises.flashcards.length

  const handleWordPress = (item: VocabularyItem) => {
    setTooltip(item)
  }

  const handleFlashcardFlip = () => {
    setFlipped(!flipped)
  }

  const handleFlashcardNext = () => {
    if (flashcardIdx < flashcardCount - 1) {
      setFlashcardIdx(flashcardIdx + 1)
      setFlipped(false)
    }
  }

  const handleSpellSubmit = () => {
    let correct = 0
    vocabulary_exercises.spelling.forEach((wordIdx, i) => {
      if (spellInputs[i].trim().toLowerCase() === resolvedVocab[wordIdx].word.toLowerCase()) {
        correct++
      }
    })
    setSpellScore(correct)
    setSpellSubmitted(true)
  }

  const handleMatchSelect = (qIdx: number, optIdx: number) => {
    const next = [...matchAnswers]
    next[qIdx] = optIdx
    setMatchAnswers(next)
  }

  const handleMatchSubmit = () => {
    let correct = 0
    vocabulary_exercises.matching.forEach((q, i) => {
      if (matchAnswers[i] === q.answer) correct++
    })
    setMatchScore(correct)
    setMatchSubmitted(true)
  }

  const allMatchAnswered = matchAnswers.every((a) => a !== null)

  const renderSubModeSelector = () => (
    <View style={styles.modeSelector}>
      <Text style={styles.modeTitle}>VOCABULARY EXERCISES</Text>
      <Text style={styles.modeSubtitle}>
        Choose a drill mode. Complete all three to finish this step.
      </Text>
      <Divider />
      <Pressable
        style={styles.modeBtn}
        onPress={() => setSubMode('flashcards')}
      >
        <Text style={styles.modeLabel}>1. FLASHCARDS</Text>
        <Text style={styles.modeDesc}>
          View words and their meanings. Tap to flip.
        </Text>
      </Pressable>
      <Pressable
        style={styles.modeBtn}
        onPress={() => setSubMode('spelling')}
      >
        <Text style={styles.modeLabel}>2. SPELLING</Text>
        <Text style={styles.modeDesc}>
          Write the correct spelling of each word.
        </Text>
      </Pressable>
      <Pressable
        style={styles.modeBtn}
        onPress={() => setSubMode('matching')}
      >
        <Text style={styles.modeLabel}>3. MATCHING</Text>
        <Text style={styles.modeDesc}>
          Match English words with their Chinese meanings.
        </Text>
      </Pressable>
    </View>
  )

  const renderFlashcards = () => (
    <View style={styles.modeContainer}>
      <Text style={styles.progressLabel}>
        {flashcardIdx + 1} / {flashcardCount}
      </Text>
      <Pressable
        style={[styles.flashcard, flipped && styles.flashcardFlipped]}
        onPress={handleFlashcardFlip}
      >
        {!flipped ? (
          <>
            <Text style={styles.flashcardWord}>{currentWord.word}</Text>
            <Text style={styles.flashcardPhonetic}>{currentWord.phonetic}</Text>
            <Text style={styles.flashcardHint}>TAP TO REVEAL</Text>
          </>
        ) : (
          <>
            <Text style={styles.flashcardDef}>{currentWord.definition_cn}</Text>
            <Text style={styles.flashcardPos}>{currentWord.part_of_speech}</Text>
            <Text style={styles.flashcardExample}>
              {currentWord.example}
            </Text>
          </>
        )}
      </Pressable>
      <View style={styles.modeActions}>
        <Button
          title="BACK"
          variant="secondary"
          onPress={() => {
            setSubMode(null)
            setFlashcardIdx(0)
            setFlipped(false)
          }}
        />
        <View style={{ width: 8 }} />
        {flashcardIdx < flashcardCount - 1 ? (
          <Button title="NEXT →" onPress={handleFlashcardNext} />
        ) : (
          <Button title="DONE" onPress={() => { setSubMode(null); setFlashcardIdx(0); setFlipped(false) }} />
        )}
      </View>
    </View>
  )

  const renderSpelling = () => (
    <View style={styles.modeContainer}>
      <Text style={styles.modeTitle}>SPELLING DICTATION</Text>
      {vocabulary_exercises.spelling.map((wordIdx, i) => {
        const word = new_vocabulary[wordIdx]
        return (
          <View key={i} style={styles.spellRow}>
            <Text style={styles.spellHint}>
              {word.definition_cn} ({word.part_of_speech})
            </Text>
            <TextInput
              style={[
                styles.spellInput,
                spellSubmitted && styles.spellInputDisabled
              ]}
              value={spellInputs[i]}
              onChangeText={(text) => {
                const next = [...spellInputs]
                next[i] = text
                setSpellInputs(next)
              }}
              editable={!spellSubmitted}
              autoCapitalize="none"
              autoCorrect={false}
            />
            {spellSubmitted ? (
              <Text
                style={[
                  styles.resultMark,
                  spellInputs[i].trim().toLowerCase() === word.word.toLowerCase()
                    ? styles.correct
                    : styles.incorrect
                ]}
              >
                {spellInputs[i].trim().toLowerCase() === word.word.toLowerCase()
                  ? '✓'
                  : `✗ ${word.word}`}
              </Text>
            ) : null}
          </View>
        )
      })}
      {spellSubmitted ? (
        <View style={styles.resultBox}>
          <Text style={styles.resultText}>
            {spellScore} / {vocabulary_exercises.spelling.length} CORRECT
          </Text>
          <Button
            title="BACK TO MODES"
            variant="secondary"
            onPress={() => {
              setSubMode(null)
              setSpellInputs(Array.from({ length: vocabulary_exercises.spelling.length }, () => ''))
              setSpellSubmitted(false)
              setSpellScore(0)
            }}
          />
        </View>
      ) : (
        <View style={styles.modeActions}>
          <Button
            title="BACK"
            variant="secondary"
            onPress={() => {
              setSubMode(null)
              setSpellInputs(Array.from({ length: vocabulary_exercises.spelling.length }, () => ''))
              setSpellSubmitted(false)
            }}
          />
          <View style={{ width: 8 }} />
          <Button
            title="SUBMIT"
            onPress={handleSpellSubmit}
            disabled={spellInputs.some((v) => !v.trim())}
          />
        </View>
      )}
    </View>
  )

  const renderMatching = () => (
    <View style={styles.modeContainer}>
      <Text style={styles.modeTitle}>MATCHING</Text>
      {vocabulary_exercises.matching.map((q, i) => {
        const word = resolvedVocab[q.en_index]
        return (
          <View key={i} style={styles.matchRow}>
            <Text style={styles.matchWord}>{word.word}</Text>
            <View style={styles.matchOptions}>
              {q.cn_options.map((opt, optIdx) => {
                const isSelected = matchAnswers[i] === optIdx
                const isCorrectAnswer = matchSubmitted && q.answer === optIdx
                const isWrongSelected = matchSubmitted && isSelected && q.answer !== optIdx

                return (
                  <Pressable
                    key={optIdx}
                    style={[
                      styles.matchOption,
                      isSelected && styles.matchOptionSelected,
                      isCorrectAnswer && styles.matchOptionCorrect,
                      isWrongSelected && styles.matchOptionWrong
                    ]}
                    onPress={() => !matchSubmitted && handleMatchSelect(i, optIdx)}
                    disabled={matchSubmitted}
                  >
                    <Text
                      style={[
                        styles.matchOptionText,
                        isSelected && styles.matchOptionTextSelected
                      ]}
                    >
                      {opt}
                    </Text>
                  </Pressable>
                )
              })}
            </View>
          </View>
        )
      })}
      {matchSubmitted ? (
        <View style={styles.resultBox}>
          <Text style={styles.resultText}>
            {matchScore} / {vocabulary_exercises.matching.length} CORRECT
          </Text>
          <Button
            title="BACK TO MODES"
            variant="secondary"
            onPress={() => {
              setSubMode(null)
              setMatchAnswers(
                Array.from({ length: vocabulary_exercises.matching.length }, () => null)
              )
              setMatchSubmitted(false)
              setMatchScore(0)
            }}
          />
        </View>
      ) : (
        <View style={styles.modeActions}>
          <Button
            title="BACK"
            variant="secondary"
            onPress={() => {
              setSubMode(null)
              setMatchAnswers(
                Array.from({ length: vocabulary_exercises.matching.length }, () => null)
              )
              setMatchSubmitted(false)
            }}
          />
          <View style={{ width: 8 }} />
          <Button
            title="SUBMIT"
            onPress={handleMatchSubmit}
            disabled={!allMatchAnswered}
          />
        </View>
      )}
    </View>
  )

  return (
    <View style={styles.container}>
      <ScrollView
        style={styles.scroll}
        contentContainerStyle={styles.scrollContent}
      >
        {!subMode
          ? renderSubModeSelector()
          : subMode === 'flashcards'
            ? renderFlashcards()
            : subMode === 'spelling'
              ? renderSpelling()
              : renderMatching()}
      </ScrollView>

      {!subMode ? (
        <View style={styles.footer}>
          <Button title="COMPLETE STEP →" onPress={onComplete} />
        </View>
      ) : null}

      <WordTooltip
        item={tooltip}
        visible={!!tooltip}
        onClose={() => setTooltip(null)}
      />
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
  modeSelector: {},
  modeTitle: {
    ...typography.title
  },
  modeSubtitle: {
    ...typography.body,
    opacity: 0.6,
    marginTop: space[1],
    marginBottom: space[3]
  },
  modeBtn: {
    borderWidth: border.width,
    borderColor: color.border,
    padding: space[3],
    marginTop: space[2]
  },
  modeLabel: {
    ...typography.labelLg
  },
  modeDesc: {
    ...typography.body,
    opacity: 0.6,
    marginTop: 2
  },
  modeContainer: {
    flex: 1
  },
  progressLabel: {
    ...typography.labelLg,
    textAlign: 'center',
    marginBottom: space[4]
  },
  flashcard: {
    borderWidth: border.widthThick,
    borderColor: color.fg,
    alignItems: 'center',
    justifyContent: 'center',
    paddingVertical: space[10],
    paddingHorizontal: space[4],
    minHeight: 220,
    backgroundColor: color.bg
  },
  flashcardFlipped: {
    backgroundColor: color.muted
  },
  flashcardWord: {
    ...typography.titleLg,
    fontSize: 32
  },
  flashcardPhonetic: {
    ...typography.label,
    opacity: 0.5,
    marginTop: space[2]
  },
  flashcardHint: {
    ...typography.labelSm,
    color: color.accent,
    marginTop: space[6]
  },
  flashcardDef: {
    ...typography.title,
    fontSize: 24,
    textTransform: 'none'
  },
  flashcardPos: {
    ...typography.label,
    opacity: 0.5,
    marginTop: space[2]
  },
  flashcardExample: {
    ...typography.body,
    fontStyle: 'italic',
    marginTop: space[4],
    textAlign: 'center'
  },
  modeActions: {
    flexDirection: 'row',
    marginTop: space[4]
  },
  spellRow: {
    marginTop: space[3]
  },
  spellHint: {
    ...typography.body,
    opacity: 0.7
  },
  spellInput: {
    borderWidth: border.width,
    borderColor: color.fg,
    height: 44,
    paddingHorizontal: space[2],
    ...typography.body,
    fontSize: 18,
    color: color.fg,
    backgroundColor: color.bg,
    marginTop: 4
  },
  spellInputDisabled: {
    opacity: 0.5,
    borderColor: color.border
  },
  resultMark: {
    ...typography.labelLg,
    position: 'absolute',
    right: 0,
    top: 32
  },
  correct: {
    color: color.success
  },
  incorrect: {
    color: color.error
  },
  resultBox: {
    marginTop: space[4],
    alignItems: 'center'
  },
  resultText: {
    ...typography.title,
    textAlign: 'center',
    marginVertical: space[4]
  },
  matchRow: {
    borderWidth: border.width,
    borderColor: color.border,
    padding: space[3],
    marginTop: space[2]
  },
  matchWord: {
    ...typography.titleSm,
    marginBottom: space[2]
  },
  matchOptions: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: space[1]
  },
  matchOption: {
    borderWidth: border.width,
    borderColor: color.border,
    paddingVertical: space[1] + 2,
    paddingHorizontal: space[3],
    backgroundColor: color.bg
  },
  matchOptionSelected: {
    backgroundColor: color.fg,
    borderColor: color.fg
  },
  matchOptionCorrect: {
    borderColor: color.success
  },
  matchOptionWrong: {
    borderColor: color.error,
    backgroundColor: color.error
  },
  matchOptionText: {
    ...typography.body,
    color: color.fg
  },
  matchOptionTextSelected: {
    color: color.bg
  },
  footer: {
    padding: space[4],
    borderTopWidth: border.width,
    borderTopColor: color.border,
    backgroundColor: color.bg
  }
})
