import { View, Text, Pressable, StyleSheet } from 'react-native'
import { color, border, space, typography } from '@shared/theme'
import type { VocabularyItem } from '@shared/types'

interface WordTooltipProps {
  item: VocabularyItem | null
  visible: boolean
  position: { top: number; left: number }
  onClose: () => void
}

export function WordTooltip({
  item,
  visible,
  position,
  onClose
}: WordTooltipProps) {
  if (!visible || !item) return null

  return (
    <Pressable style={styles.backdrop} onPress={onClose}>
      <View
        style={[
          styles.tooltip,
          { top: position.top - 10, left: Math.max(16, position.left - 80) }
        ]}
      >
        <Text style={styles.word}>{item.word}</Text>
        <Text style={styles.phonetic}>{item.phonetic}</Text>
        <Text style={styles.pos}>{item.part_of_speech}</Text>
        <Text style={styles.definition}>{item.definition_cn}</Text>
        {item.example ? <Text style={styles.example}>{item.example}</Text> : null}
        <Text style={styles.hint}>TAP TO CLOSE</Text>
      </View>
    </Pressable>
  )
}

const styles = StyleSheet.create({
  backdrop: {
    ...StyleSheet.absoluteFill,
    zIndex: 999
  },
  tooltip: {
    position: 'absolute',
    width: 200,
    backgroundColor: color.bg,
    borderWidth: border.width,
    borderColor: color.fg,
    padding: space[3],
    gap: 2
  },
  word: {
    ...typography.titleSm,
    fontSize: 18
  },
  phonetic: {
    ...typography.label,
    color: color.fg,
    opacity: 0.5
  },
  pos: {
    ...typography.label,
    marginTop: space[1]
  },
  definition: {
    ...typography.body,
    fontSize: 15
  },
  example: {
    ...typography.label,
    color: color.fg,
    opacity: 0.6,
    marginTop: space[1],
    fontStyle: 'italic'
  },
  hint: {
    ...typography.labelSm,
    color: color.accent,
    marginTop: space[2],
    textAlign: 'right'
  }
})
