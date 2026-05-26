import { View, Text, Pressable, StyleSheet } from 'react-native'
import { color, border, space, typography } from '@shared/theme'

interface StepProgressBarProps {
  current: number
  total: number
  completed: number[]
  labels: string[]
  onStepPress?: (stepIndex: number) => void
}

export function StepProgressBar({
  current,
  total,
  completed,
  labels,
  onStepPress
}: StepProgressBarProps) {
  return (
    <View style={styles.container}>
      <View style={styles.dotsRow}>
        {Array.from({ length: total }, (_, i) => {
          const isCompleted = completed.includes(i)
          const isCurrent = current === i
          const isLocked = !isCompleted && !isCurrent

          return (
            <View key={i} style={styles.dotCell}>
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

      <View style={styles.connectorRow}>
        {Array.from({ length: total }, (_, i) => {
          const isCompleted = completed.includes(i)
          return (
            <View key={i} style={styles.connectorCell}>
              {i < total - 1 ? (
                <View
                  style={[
                    styles.connector,
                    isCompleted && styles.connectorCompleted
                  ]}
                />
              ) : null}
            </View>
          )
        })}
      </View>

      <View style={styles.labelsRow}>
        {labels.map((label, i) => {
          const isLocked = !completed.includes(i) && current !== i
          return (
            <Pressable
              key={i}
              style={styles.labelCell}
              onPress={() => onStepPress?.(i)}
              disabled={isLocked}
            >
              <Text
                style={[styles.label, isLocked && styles.labelLocked]}
                numberOfLines={1}
              >
                {label.toUpperCase()}
              </Text>
            </Pressable>
          )
        })}
      </View>
    </View>
  )
}

const DOT_SIZE = 28

const styles = StyleSheet.create({
  container: {
    paddingHorizontal: space[4],
    paddingTop: space[3],
    paddingBottom: space[3],
    backgroundColor: color.bg,
    borderBottomWidth: border.width,
    borderBottomColor: color.border
  },
  dotsRow: {
    flexDirection: 'row',
    alignItems: 'center'
  },
  dotCell: {
    flex: 1,
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
    ...typography.labelSm,
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
  connectorRow: {
    flexDirection: 'row',
    marginTop: -DOT_SIZE / 2,
    zIndex: -1
  },
  connectorCell: {
    flex: 1,
    alignItems: 'center',
    height: DOT_SIZE
  },
  connector: {
    position: 'absolute',
    top: DOT_SIZE / 2 - 1,
    left: '50%',
    right: '-50%',
    height: border.width,
    backgroundColor: color.border
  },
  connectorCompleted: {
    backgroundColor: color.fg
  },
  labelsRow: {
    flexDirection: 'row',
    marginTop: space[1]
  },
  labelCell: {
    flex: 1,
    alignItems: 'center'
  },
  label: {
    ...typography.labelSm,
    textAlign: 'center'
  },
  labelLocked: {
    opacity: 0.25
  }
})
