import { useState } from 'react'
import { View, Text, Pressable, StyleSheet } from 'react-native'
import { piper } from '@native/piper'
import { color, border, space, typography } from '@shared/theme'
import type { VocabularyItem } from '@shared/types'

interface WordTooltipProps {
  item: VocabularyItem | null
  visible: boolean
  onClose: () => void
}

export function WordTooltip({ item, visible, onClose }: WordTooltipProps) {
  const [speaking, setSpeaking] = useState<'word' | 'example' | null>(null)

  if (!visible || !item) return null

  const speakWord = async () => {
    try {
      setSpeaking('word')
      await piper.init()
      await piper.speak(item.word)
    } catch (e) {
      console.warn('[TTS] word error:', e)
    } finally {
      setSpeaking(null)
    }
  }

  const speakExample = async () => {
    if (!item.example) return
    try {
      setSpeaking('example')
      await piper.init()
      await piper.speak(item.example)
    } catch (e) {
      console.warn('[TTS] example error:', e)
    } finally {
      setSpeaking(null)
    }
  }

  return (
    <Pressable style={styles.backdrop} onPress={onClose}>
      <Pressable style={styles.card} onPress={() => {}}>
        <View style={styles.header}>
          <View style={{ flex: 1 }}>
            <Text style={styles.word}>{item.word}</Text>
            <Text style={styles.phonetic}>{item.phonetic}</Text>
          </View>
          <Pressable
            style={[styles.ttsBtn, speaking === 'word' && styles.ttsBtnActive]}
            onPress={speakWord}
          >
            <Text style={[styles.ttsBtnText, speaking === 'word' && styles.ttsBtnTextActive]}>
              {speaking === 'word' ? '...' : 'WORD'}
            </Text>
          </Pressable>
        </View>

        {item.part_of_speech ? <Text style={styles.pos}>{item.part_of_speech}</Text> : null}
        {item.definition_cn ? (
          <Text style={styles.definition}>{item.definition_cn}</Text>
        ) : (
          <Text style={styles.notFound}>No definition found</Text>
        )}

        {item.example ? (
          <View style={styles.exampleBlock}>
            <Text style={styles.example}>{item.example}</Text>
            <Pressable
              style={[styles.ttsBtn, styles.ttsBtnSm, speaking === 'example' && styles.ttsBtnActive]}
              onPress={speakExample}
            >
              <Text style={[styles.ttsBtnText, speaking === 'example' && styles.ttsBtnTextActive]}>
                {speaking === 'example' ? '...' : 'EXAMPLE'}
              </Text>
            </Pressable>
          </View>
        ) : null}

        <Text style={styles.hint}>TAP OUTSIDE TO CLOSE</Text>
      </Pressable>
    </Pressable>
  )
}

const styles = StyleSheet.create({
  backdrop: {
    ...StyleSheet.absoluteFill,
    zIndex: 999,
    backgroundColor: 'rgba(0,0,0,0.4)',
    justifyContent: 'center',
    alignItems: 'center',
    padding: space[6]
  },
  card: {
    width: '100%',
    maxWidth: 300,
    backgroundColor: color.bg,
    borderWidth: border.widthThick,
    borderColor: color.fg,
    padding: space[4]
  },
  header: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    gap: space[2]
  },
  word: {
    ...typography.titleSm,
    fontSize: 18,
    textTransform: 'none'
  },
  phonetic: {
    ...typography.label,
    color: color.fg,
    opacity: 0.5,
    textTransform: 'none'
  },
  pos: {
    ...typography.label,
    marginTop: space[2]
  },
  definition: {
    ...typography.body,
    fontSize: 15,
    marginTop: space[1]
  },
  notFound: {
    ...typography.body,
    fontSize: 14,
    marginTop: space[1],
    fontStyle: 'italic',
    opacity: 0.4
  },
  exampleBlock: {
    marginTop: space[2],
    gap: space[1]
  },
  example: {
    ...typography.bodySm,
    fontStyle: 'italic',
    color: color.fg,
    opacity: 0.7
  },
  ttsBtn: {
    borderWidth: border.width,
    borderColor: color.border,
    paddingVertical: space[1],
    paddingHorizontal: space[2],
    backgroundColor: color.bg,
    alignSelf: 'flex-start'
  },
  ttsBtnSm: {
    marginTop: space[1]
  },
  ttsBtnActive: {
    backgroundColor: color.fg,
    borderColor: color.fg
  },
  ttsBtnText: {
    ...typography.labelSm,
    color: color.fg
  },
  ttsBtnTextActive: {
    color: color.bg
  },
  hint: {
    ...typography.labelSm,
    color: color.accent,
    marginTop: space[3],
    textAlign: 'center'
  }
})
