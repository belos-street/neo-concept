import { useState, useRef } from 'react'
import { View, Text, ScrollView, Pressable, Image, StyleSheet } from 'react-native'
import { Button, Divider } from '@shared/components'
import { WordTooltip } from '@features/lesson/WordTooltip'
import { piper } from '@native/piper'
import { color, border, space, typography } from '@shared/theme'
import type { Lesson, VocabularyItem } from '@shared/types'

type TTSMode = 'idle' | 'full' | 'sentence'

interface PassageStepProps {
  lesson: Lesson
  onComplete: () => void
}

export function PassageStep({ lesson, onComplete }: PassageStepProps) {
  const [tooltip, setTooltip] = useState<{
    item: VocabularyItem
    pos: { top: number; left: number }
  } | null>(null)
  const [ttsMode, setTtsMode] = useState<TTSMode>('idle')
  const [sentenceIdx, setSentenceIdx] = useState(0)
  const isSpeakingRef = useRef(false)

  const sentences = lesson.passage.text
    .split(/(?<=[.!?])\s+/)
    .filter((s) => s.trim().length > 0)

  const handleWordPress = (
    item: VocabularyItem,
    event: { nativeEvent: { pageY: number; pageX: number } }
  ) => {
    setTooltip({
      item,
      pos: { top: event.nativeEvent.pageY, left: event.nativeEvent.pageX }
    })
  }

  const handleFullRead = async () => {
    if (ttsMode === 'full') {
      await piper.stop()
      setTtsMode('idle')
      isSpeakingRef.current = false
      return
    }
    setTtsMode('full')
    isSpeakingRef.current = true
    try {
      await piper.init()
      await piper.speak(lesson.passage.text)
    } catch (e) {
      console.warn('[TTS] full read error:', e)
    }
    if (isSpeakingRef.current) {
      setTtsMode('idle')
    }
  }

  const handleSentenceRead = async () => {
    if (ttsMode === 'sentence') {
      await piper.stop()
      setTtsMode('idle')
      setSentenceIdx(0)
      isSpeakingRef.current = false
      return
    }
    setTtsMode('sentence')
    isSpeakingRef.current = true
    try {
      await piper.init()
    } catch (e) {
      console.warn('[TTS] init error:', e)
      setTtsMode('idle')
      return
    }
    for (let i = 0; i < sentences.length; i++) {
      if (!isSpeakingRef.current) break
      setSentenceIdx(i)
      try {
        await piper.speak(sentences[i])
      } catch (e) {
        console.warn('[TTS] sentence error:', i, e)
        break
      }
    }
    if (isSpeakingRef.current) {
      setTtsMode('idle')
      setSentenceIdx(0)
      isSpeakingRef.current = false
    }
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
          <Pressable
            style={[
              styles.ttsBtn,
              ttsMode === 'full' && styles.ttsBtnActive
            ]}
            onPress={handleFullRead}
          >
            <Text
              style={[
                styles.ttsBtnText,
                ttsMode === 'full' && styles.ttsBtnTextActive
              ]}
            >
              {ttsMode === 'full' ? '■ STOP' : '▶ FULL'}
            </Text>
          </Pressable>
          <Pressable
            style={[
              styles.ttsBtn,
              ttsMode === 'sentence' && styles.ttsBtnActive
            ]}
            onPress={handleSentenceRead}
          >
            <Text
              style={[
                styles.ttsBtnText,
                ttsMode === 'sentence' && styles.ttsBtnTextActive
              ]}
            >
              {ttsMode === 'sentence'
                ? `■ STOP (${sentenceIdx + 1}/${sentences.length})`
                : '▶ SENTENCE'}
            </Text>
          </Pressable>
        </View>

        <Text style={styles.passageText}>
          {ttsMode === 'sentence'
            ? sentences.map((s, i) => (
                <Text
                  key={i}
                  style={i === sentenceIdx ? styles.highlightedSentence : undefined}
                >
                  {s}{' '}
                </Text>
              ))
            : lesson.passage.text}
        </Text>

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

        {lesson.new_vocabulary.length > 0 ? (
          <>
            <Divider />
            <Text style={styles.sectionTitle}>NEW VOCABULARY</Text>
            {lesson.new_vocabulary.map((vocab, i) => (
              <Pressable
                key={i}
                style={styles.vocabRow}
                onPress={(e) => handleWordPress(vocab, e)}
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
        item={tooltip?.item ?? null}
        visible={!!tooltip}
        position={tooltip?.pos ?? { top: 0, left: 0 }}
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
    marginBottom: space[3]
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
  passageText: {
    ...typography.body,
    fontSize: 16,
    lineHeight: 26
  },
  highlightedSentence: {
    backgroundColor: color.accent,
    color: color.bg,
    fontWeight: '600'
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
