import { useState, useRef } from 'react'
import { View, Text, Pressable, ScrollView, StyleSheet } from 'react-native'
import { Button, Divider } from '@shared/components'
import { piper } from '@native/piper'
import { whisper } from '@native/whisper'
import { color, border, space, typography } from '@shared/theme'
import { wordDiff } from './editDistance'
import type { Lesson } from '@shared/types'
import type { WordDiff } from './editDistance'

interface SpeakingStepProps {
  lesson: Lesson
  onComplete: () => void
  onRetry?: () => void
}

type SentenceResult = {
  score: number
  diffs: WordDiff[]
  recognized: string
}

export function SpeakingStep({ lesson, onComplete, onRetry }: SpeakingStepProps) {
  const { sentences } = lesson.speaking

  const [sIdx, setSIdx] = useState(0)
  const [phase, setPhase] = useState<'ready' | 'recording' | 'result'>('ready')
  const [recognized, setRecognized] = useState('')
  const [result, setResult] = useState<SentenceResult | null>(null)
  const [allResults, setAllResults] = useState<SentenceResult[]>([])
  const [isPlaying, setIsPlaying] = useState(false)
  const recordingRef = useRef(false)

  const current = sentences[sIdx]

  const handleListen = async () => {
    if (isPlaying) {
      await piper.stop()
      setIsPlaying(false)
      return
    }
    setIsPlaying(true)
    try {
      await piper.init()
      await piper.speak(current.text)
    } catch (e) {
      console.warn('[TTS] speak listen error:', e)
    }
    setIsPlaying(false)
  }

  const handleRecordIn = async () => {
    recordingRef.current = true
    setPhase('recording')
    try {
      await whisper.startRecording()
    } catch {}
  }

  const handleRecordOut = async () => {
    if (!recordingRef.current) return
    recordingRef.current = false
    try {
      const audioPath = await whisper.stopRecording()
      if (!audioPath) {
        setPhase('ready')
        return
      }
      const res = await whisper.recognize(audioPath)
      const recognizedText = res.text ?? ''
      setRecognized(recognizedText)
      const diffResult = wordDiff(current.text, recognizedText)
      const fullResult = { ...diffResult, recognized: recognizedText }
      setResult(fullResult)
      setAllResults((prev) => [...prev, fullResult])
      setPhase('result')
    } catch {
      const mockText = current.text
      const diffResult = wordDiff(current.text, mockText)
      setRecognized(mockText)
      const fullResult = { ...diffResult, recognized: mockText }
      setResult(fullResult)
      setAllResults((prev) => [...prev, fullResult])
      setPhase('result')
    }
  }

  const handleNext = () => {
    if (sIdx < sentences.length - 1) {
      setSIdx((i) => i + 1)
      setPhase('ready')
      setRecognized('')
      setResult(null)
    }
  }

  const allDone = sIdx >= sentences.length - 1 && phase === 'result'

  const avgScore =
    allResults.length === 0
      ? 0
      : Math.round(
          allResults.reduce((sum, r) => sum + r.score, 0) / allResults.length
        )

  if (sentences.length === 0) {
    return (
      <View style={styles.empty}>
        <Text style={styles.emptyText}>No speaking sentences</Text>
        <Button title="COMPLETE" onPress={onComplete} />
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
          <Text style={styles.resultTitle}>SPEAKING COMPLETE</Text>
          <Text style={styles.resultAvgScore}>AVG SCORE: {avgScore}%</Text>
          <Divider />
          {sentences.map((s, i) => {
            const r = allResults[i]
            return (
              <View key={i} style={styles.summaryRow}>
                <Text style={styles.summaryIdx}>{i + 1}.</Text>
                <View style={styles.summaryBody}>
                  <Text style={styles.summarySentence} numberOfLines={1}>
                    {s.text}
                  </Text>
                  <Text
                    style={[
                      styles.summaryScore,
                      r.score >= 80 ? styles.scoreGood : styles.scoreLow
                    ]}
                  >
                    {r.score}%
                  </Text>
                </View>
              </View>
            )
          })}
          <Divider />
          <View style={styles.resultActions}>
            <Button title="REDO" variant="secondary" onPress={onRetry} />
            <View style={{ width: 8 }} />
            <Button title="COMPLETE" onPress={onComplete} />
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
          {sIdx + 1} / {sentences.length}
        </Text>

        <Pressable
          style={[styles.listenBtn, isPlaying && styles.listenBtnActive]}
          onPress={handleListen}
        >
          <Text style={[styles.listenText, isPlaying && styles.listenTextActive]}>
            {isPlaying ? 'STOP' : 'LISTEN'}
          </Text>
        </Pressable>

        <Text style={styles.targetText}>{current.text}</Text>

        {phase === 'result' && result ? (
          <View style={styles.resultBlock}>
            <Text style={styles.recognizedLabel}>YOU SAID</Text>
            <Text style={styles.recognizedText}>{recognized}</Text>

            <Divider />

            <View style={styles.diffRow}>
              {result.diffs.map((d, i) => (
                <Text
                  key={i}
                  style={[
                    styles.diffWord,
                    d.status === 'wrong' && styles.diffWrong,
                    d.status === 'missing' && styles.diffMissing
                  ]}
                >
                  {d.word}
                </Text>
              ))}
            </View>

            <Text
              style={[
                styles.scoreText,
                result.score >= 80 ? styles.scoreGood : styles.scoreLow
              ]}
            >
              {result.score}%
            </Text>
          </View>
        ) : null}
      </ScrollView>

      <View style={styles.footer}>
        {phase !== 'result' ? (
          <Pressable
            style={[
              styles.recordBtn,
              phase === 'recording' && styles.recordBtnActive
            ]}
            onPressIn={handleRecordIn}
            onPressOut={handleRecordOut}
          >
            <Text
              style={[
                styles.recordText,
                phase === 'recording' && styles.recordTextActive
              ]}
            >
              {phase === 'recording' ? 'LISTENING...' : 'HOLD TO SPEAK'}
            </Text>
          </Pressable>
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
  listenBtn: {
    borderWidth: border.width,
    borderColor: color.border,
    paddingVertical: space[3],
    alignItems: 'center',
    marginBottom: space[4],
    backgroundColor: color.bg
  },
  listenBtnActive: { backgroundColor: color.fg, borderColor: color.fg },
  listenText: { ...typography.labelLg, color: color.fg },
  listenTextActive: { color: color.bg },
  targetText: {
    ...typography.titleLg,
    fontSize: 20,
    lineHeight: 32,
    textAlign: 'center',
    marginBottom: space[6],
    textTransform: 'none'
  },
  resultBlock: { marginTop: space[2] },
  recognizedLabel: { ...typography.label, textAlign: 'center', marginBottom: space[1] },
  recognizedText: {
    ...typography.body,
    textAlign: 'center',
    marginBottom: space[3],
    opacity: 0.6
  },
  diffRow: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    justifyContent: 'center',
    marginTop: space[3],
    gap: space[1]
  },
  diffWord: {
    ...typography.body,
    paddingHorizontal: space[2],
    paddingVertical: space[1],
    borderWidth: 1,
    borderColor: color.success,
    color: color.success
  },
  diffWrong: {
    borderColor: color.error,
    color: color.error,
    textDecorationLine: 'line-through'
  },
  diffMissing: {
    borderColor: color.error,
    color: color.error,
    opacity: 0.5,
    borderStyle: 'dashed'
  },
  scoreText: {
    ...typography.titleLg,
    textAlign: 'center',
    marginTop: space[4]
  },
  scoreGood: { color: color.success },
  scoreLow: { color: color.error },
  resultTitle: { ...typography.title, textAlign: 'center', marginTop: space[8] },
  resultAvgScore: { ...typography.titleLg, textAlign: 'center', marginVertical: space[4] },
  summaryRow: {
    flexDirection: 'row',
    alignItems: 'center',
    paddingVertical: space[2],
    borderBottomWidth: 1,
    borderBottomColor: color.border
  },
  summaryIdx: { ...typography.labelLg, width: 28, color: color.fg },
  summaryBody: { flex: 1, flexDirection: 'row', alignItems: 'center', justifyContent: 'space-between' },
  summarySentence: { ...typography.body, flex: 1, marginRight: space[2] },
  summaryScore: { ...typography.labelLg },
  resultActions: { flexDirection: 'row', marginTop: space[6] },
  footer: {
    padding: space[4],
    borderTopWidth: border.width,
    borderTopColor: color.border,
    backgroundColor: color.bg
  },
  recordBtn: {
    borderWidth: border.width * 2,
    borderColor: color.accent,
    paddingVertical: space[4],
    alignItems: 'center',
    backgroundColor: color.bg
  },
  recordBtnActive: { backgroundColor: color.accent },
  recordText: { ...typography.labelLg, color: color.accent, fontWeight: '600' },
  recordTextActive: { color: color.bg }
})
