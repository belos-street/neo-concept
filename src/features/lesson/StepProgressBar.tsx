import { View, Text, Pressable, ScrollView, StyleSheet } from 'react-native'
import { color, border, space, typography } from '@shared/theme'

interface StepProgressBarProps {
  current: number
  total: number
  completed: number[]
  labels: string[]
  onStepPress?: (stepIndex: number) => void
}

const DOT_SIZE = 34
const CELL_WIDTH = 64

export function StepProgressBar({
  current,
  total,
  completed,
  labels,
  onStepPress
}: StepProgressBarProps) {
  return (
    <View style={styles.container}>
      <ScrollView
        horizontal
        showsHorizontalScrollIndicator={false}
        contentContainerStyle={styles.scrollContent}
      >
        <View>
          <View style={styles.dotsRow}>
            {Array.from({ length: total }, (_, i) => {
              const isCompleted = completed.includes(i)
              const isCurrent = current === i
              const isLocked = !isCompleted && !isCurrent

              return (
                <View key={`dot-${i}`} style={[styles.dotCell, { width: CELL_WIDTH }]}>
                  <Pressable
                    style={[
                      styles.dot,
                      isCompleted && styles.dotCompleted,
                      isCurrent && styles.dotCurrent,
                      isLocked && styles.dotLocked
                    ]}
                    onPress={() => onStepPress?.(i)}
                    disabled={isLocked}
                  >
                    <Text
                      style={[
                        styles.dotText,
                        isCompleted && styles.dotTextCompleted,
                        isCurrent && styles.dotTextCurrent,
                        isLocked && styles.dotTextLocked
                      ]}
                    >
                      {isCompleted ? '✓' : String(i + 1)}
                    </Text>
                  </Pressable>
                </View>
              )
            })}
          </View>

          <View style={styles.labelsRow}>
            {labels.map((label, i) => {
              const isLocked = !completed.includes(i) && current !== i
              return (
                <Pressable
                  key={`lbl-${i}`}
                  style={[styles.labelCell, { width: CELL_WIDTH }]}
                  onPress={() => onStepPress?.(i)}
                  disabled={isLocked}
                >
                  <Text
                    style={[styles.label, isLocked && styles.labelLocked]}
                    numberOfLines={1}
                  >
                    {(label ?? '').toUpperCase()}
                  </Text>
                </Pressable>
              )
            })}
          </View>
        </View>
      </ScrollView>
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    backgroundColor: color.bg,
    borderBottomWidth: border.width,
    borderBottomColor: color.border,
    paddingVertical: space[3]
  },
  scrollContent: {
    paddingHorizontal: space[3]
  },
  dotsRow: {
    flexDirection: 'row'
  },
  dotCell: {
    alignItems: 'center'
  },
  dot: {
    width: DOT_SIZE,
    height: DOT_SIZE,
    borderRadius: DOT_SIZE / 2,
    borderWidth: border.width,
    borderColor: color.border,
    backgroundColor: color.bg,
    alignItems: 'center',
    justifyContent: 'center'
  },
  dotCompleted: {
    backgroundColor: color.fg,
    borderColor: color.fg
  },
  dotCurrent: {
    backgroundColor: color.bg,
    borderColor: color.accent
  },
  dotLocked: {
    opacity: 0.3
  },
  dotText: {
    ...typography.label,
    fontSize: 14,
    color: color.fg
  },
  dotTextCompleted: {
    color: color.bg
  },
  dotTextCurrent: {
    color: color.accent
  },
  dotTextLocked: {
    color: color.fg,
    opacity: 0.3
  },
  labelsRow: {
    flexDirection: 'row',
    marginTop: space[2]
  },
  labelCell: {
    alignItems: 'center'
  },
  label: {
    ...typography.labelSm,
    textAlign: 'center',
    fontSize: 11
  },
  labelLocked: {
    opacity: 0.25
  }
})
