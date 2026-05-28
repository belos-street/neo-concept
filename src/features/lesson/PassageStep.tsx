import { useState, useRef, useCallback, useEffect } from 'react'
import { View, Text, ScrollView, Pressable, Image, StyleSheet } from 'react-native'
import { Button, Divider } from '@shared/components'
import { WordTooltip } from '@features/lesson/WordTooltip'
import { piper } from '@native/piper'
import { resolveVocab, lookupWord } from '@features/lesson/resolveVocab'
import { color, border, space, typography } from '@shared/theme'
import type { Lesson, VocabularyItem } from '@shared/types'

type TTSMode = 'idle' | 'sentence'

interface PassageStepProps {
  lesson: Lesson
  onComplete: () => void
}

interface ParagraphData {
  sentences: string[]
}

function buildParagraphs(text: string): ParagraphData[] {
  return text
    .split(/\n\n+/)
    .filter((p) => p.trim().length > 0)
    .map((p) => ({
      sentences: p
        .split(/(?<=[.!?])\s+/)
        .filter((s) => s.trim().length > 0)
    }))
}

const WORD_RE = /^([A-Za-z'\u2019-]+)(.*)$/

function tokenize(sentence: string): { text: string; isWord: boolean }[] {
  const tokens: { text: string; isWord: boolean }[] = []
  let remaining = sentence
  while (remaining.length > 0) {
    if (/^[A-Za-z]/.test(remaining)) {
      const m = remaining.match(WORD_RE)
      if (m) {
        tokens.push({ text: m[1], isWord: true })
        remaining = m[2]
        continue
      }
    }
    tokens.push({ text: remaining[0], isWord: false })
    remaining = remaining.slice(1)
  }
  return tokens
}

export function PassageStep({ lesson, onComplete }: PassageStepProps) {
  const [tooltip, setTooltip] = useState<VocabularyItem | null>(null)
  const [ttsMode, setTtsMode] = useState<TTSMode>('idle')
  const [paused, setPaused] = useState(false)
  const [paraIdx, setParaIdx] = useState(0)
  const [sentIdx, setSentIdx] = useState(0)
  const isSpeakingRef = useRef(false)
  const [resolvedVocab, setResolvedVocab] = useState<VocabularyItem[]>(() =>
    lesson.new_vocabulary.map((v) =>
      typeof v === 'string'
        ? { word: v, phonetic: '', definition_cn: '', part_of_speech: '', example: '' }
        : v
    )
  )

  useEffect(() => {
    const raw = lesson.new_vocabulary
    if (raw.length === 0) return
    if (typeof raw[0] !== 'string') return
    resolveVocab(raw as string[]).then(setResolvedVocab)
  }, [lesson.new_vocabulary])

  const paragraphs = buildParagraphs(lesson.passage.text)
  const allSentences = paragraphs.flatMap((p) => p.sentences)
  const totalCount = allSentences.length

  const vocabMap = new Map<string, VocabularyItem>()
  for (const v of resolvedVocab) {
    vocabMap.set(v.word.toLowerCase(), v)
  }

  const flatIndex = useCallback(
    (pi: number, si: number) => {
      let idx = 0
      for (let p = 0; p < pi; p++) idx += paragraphs[p].sentences.length
      return idx + si
    },
    [paragraphs]
  )

  const handleWordTap = async (word: string) => {
    const lower = word.toLowerCase()
    const vocab = vocabMap.get(lower)
    if (vocab) {
      setTooltip(vocab)
      return
    }
    const item = await lookupWord(lower)
    setTooltip(item)
  }

  const runSentenceLoop = async (startPI: number, startSI: number) => {
    isSpeakingRef.current = true
    for (let pi = startPI; pi < paragraphs.length; pi++) {
      for (let si = pi === startPI ? startSI : 0; si < paragraphs[pi].sentences.length; si++) {
        if (!isSpeakingRef.current) return
        setParaIdx(pi)
        setSentIdx(si)
        try {
          await piper.speak(paragraphs[pi].sentences[si])
        } catch (e) {
          console.warn('[TTS] sentence error:', pi, si, e)
          return
        }
      }
    }
    if (isSpeakingRef.current) {
      setTtsMode('idle')
      setPaused(false)
      setParaIdx(0)
      setSentIdx(0)
      isSpeakingRef.current = false
    }
  }

  const handleSentenceRead = async () => {
    if (ttsMode === 'sentence' && !paused) {
      await piper.stop()
      setPaused(true)
      isSpeakingRef.current = false
      return
    }
    if (ttsMode === 'sentence' && paused) {
      setPaused(false)
      const nextSI = sentIdx + 1
      if (nextSI < paragraphs[paraIdx].sentences.length) {
        await runSentenceLoop(paraIdx, nextSI)
      } else {
        const nextPI = paraIdx + 1
        if (nextPI < paragraphs.length) {
          await runSentenceLoop(nextPI, 0)
        } else {
          setTtsMode('idle')
          setPaused(false)
          setParaIdx(0)
          setSentIdx(0)
        }
      }
      return
    }
    setTtsMode('sentence')
    setPaused(false)
    try {
      await piper.init()
    } catch (e) {
      console.warn('[TTS] init error:', e)
      setTtsMode('idle')
      return
    }
    await runSentenceLoop(0, 0)
  }

  const handleStop = async () => {
    await piper.stop()
    setTtsMode('idle')
    setPaused(false)
    setParaIdx(0)
    setSentIdx(0)
    isSpeakingRef.current = false
  }

  const currentFlat = ttsMode === 'sentence' ? flatIndex(paraIdx, sentIdx) + 1 : 0

  const renderSentence = (sentence: string, isActive: boolean, onWordTap: (w: string) => void, key: string) => {
    const tokens = tokenize(sentence)
    return (
      <Text key={key} style={[styles.sentence, isActive && styles.sentenceActive]}>
        {tokens.map((t, ti) =>
          t.isWord ? (
            <Text
              key={ti}
              style={styles.wordTappable}
              onPress={() => onWordTap(t.text)}
              suppressHighlighting
            >
              {t.text}
            </Text>
          ) : (
            <Text key={ti}>{t.text}</Text>
          )
        )}{' '}
      </Text>
    )
  }

  return (
    <View style={styles.container}>
      <ScrollView
        style={styles.scroll}
        contentContainerStyle={styles.scrollContent}
      >
        {lesson.passage.images.map((img, i) => (
          <Image
            key={i}
            source={{ uri: img.url }}
            style={styles.image}
            resizeMode="cover"
          />
        ))}

        <Text style={styles.passageTitle}>
          {lesson.passage.title.toUpperCase()}
        </Text>

        <View style={styles.ttsBar}>
          {ttsMode === 'sentence' ? (
            <>
              <Pressable
                style={[styles.ttsBtn, styles.ttsBtnActive]}
                onPress={handleSentenceRead}
              >
                <Text style={[styles.ttsBtnText, styles.ttsBtnTextActive]}>
                  {paused ? 'RESUME' : 'PAUSE'}
                </Text>
              </Pressable>
              <Pressable style={styles.ttsBtn} onPress={handleStop}>
                <Text style={styles.ttsBtnText}>STOP</Text>
              </Pressable>
              <Text style={styles.ttsCounter}>
                {currentFlat}/{totalCount}
              </Text>
            </>
          ) : (
            <Pressable style={styles.ttsBtn} onPress={handleSentenceRead}>
              <Text style={styles.ttsBtnText}>SENTENCE</Text>
            </Pressable>
          )}
        </View>

        {paragraphs.map((para, pi) => (
          <View key={pi} style={pi > 0 ? styles.paragraphGap : undefined}>
            {para.sentences.map((s, si) => {
              const isActive = ttsMode === 'sentence' && pi === paraIdx && si === sentIdx
              return renderSentence(s, isActive, handleWordTap, `${pi}-${si}`)
            })}
          </View>
        ))}

        {lesson.grammar_points.length > 0 ? (
          <>
            <Divider />
            <Text style={styles.sectionTitle}>GRAMMAR POINTS</Text>
            {lesson.grammar_points.map((gp, i) => (
              <View key={i} style={styles.grammarCard}>
                <Text style={styles.grammarName}>{gp.name}</Text>
                <Text style={styles.grammarExplanation}>
                  {gp.explanation}
                </Text>
                {gp.examples.map((ex, j) => (
                  <Text key={j} style={styles.grammarExample}>
                    {ex}
                  </Text>
                ))}
              </View>
            ))}
          </>
        ) : null}

        {resolvedVocab.length > 0 ? (
          <>
            <Divider />
            <Text style={styles.sectionTitle}>NEW VOCABULARY</Text>
            {resolvedVocab.map((vocab, i) => (
              <Pressable
                key={i}
                style={styles.vocabRow}
                onPress={() => handleWordTap(vocab.word)}
              >
                <Text style={styles.vocabWord}>{vocab.word}</Text>
                <Text style={styles.vocabPhonetic}>{vocab.phonetic}</Text>
                <Text style={styles.vocabDef}>{vocab.definition_cn}</Text>
              </Pressable>
            ))}
          </>
        ) : null}

        <View style={styles.bottomPadding} />
      </ScrollView>

      <View style={styles.footer}>
        <Button title="NEXT STEP →" onPress={onComplete} />
      </View>

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
    paddingHorizontal: space[4]
  },
  image: {
    width: '100%',
    height: 200,
    borderWidth: border.width,
    borderColor: color.border,
    marginTop: space[4]
  },
  passageTitle: {
    ...typography.title,
    marginTop: space[4],
    marginBottom: space[2]
  },
  ttsBar: {
    flexDirection: 'row',
    gap: space[2],
    marginBottom: space[3],
    alignItems: 'center'
  },
  ttsBtn: {
    borderWidth: border.width,
    borderColor: color.border,
    paddingVertical: space[1] + 2,
    paddingHorizontal: space[3],
    backgroundColor: color.bg
  },
  ttsBtnActive: {
    backgroundColor: color.fg,
    borderColor: color.fg
  },
  ttsBtnText: {
    ...typography.label,
    color: color.fg
  },
  ttsBtnTextActive: {
    color: color.bg
  },
  ttsCounter: {
    ...typography.labelSm,
    color: color.fg,
    opacity: 0.5
  },
  sentence: {
    ...typography.body,
    fontSize: 16,
    lineHeight: 26,
    marginBottom: space[1]
  },
  sentenceActive: {
    backgroundColor: color.accent,
    color: color.bg
  },
  wordTappable: {
    color: color.fg
  },
  paragraphGap: {
    marginTop: space[3]
  },
  sectionTitle: {
    ...typography.labelLg,
    marginTop: space[4],
    marginBottom: space[3]
  },
  grammarCard: {
    borderWidth: border.width,
    borderColor: color.border,
    padding: space[3],
    marginBottom: space[2]
  },
  grammarName: {
    ...typography.titleSm,
    marginBottom: space[1]
  },
  grammarExplanation: {
    ...typography.body,
    opacity: 0.7,
    marginBottom: space[1]
  },
  grammarExample: {
    ...typography.body,
    fontStyle: 'italic',
    color: color.accent
  },
  vocabRow: {
    flexDirection: 'row',
    alignItems: 'baseline',
    paddingVertical: space[1],
    gap: space[2],
    borderBottomWidth: 0.5,
    borderBottomColor: color.border
  },
  vocabWord: {
    ...typography.body,
    fontWeight: '600',
    minWidth: 80
  },
  vocabPhonetic: {
    ...typography.labelSm,
    opacity: 0.5,
    minWidth: 80
  },
  vocabDef: {
    ...typography.body,
    flex: 1
  },
  footer: {
    padding: space[4],
    borderTopWidth: border.width,
    borderTopColor: color.border,
    backgroundColor: color.bg
  },
  bottomPadding: {
    height: space[4]
  }
})
