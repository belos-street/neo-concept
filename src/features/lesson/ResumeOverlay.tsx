import { View, Text, Pressable, StyleSheet } from 'react-native'
import { Button } from '@shared/components'
import { color, border, space, typography } from '@shared/theme'

interface ResumeOverlayProps {
  visible: boolean
  completedSteps: number
  totalSteps: number
  lastAccessedAt: string
  onResume: () => void
  onRestart: () => void
}

export function ResumeOverlay({
  visible,
  completedSteps,
  totalSteps,
  lastAccessedAt,
  onResume,
  onRestart
}: ResumeOverlayProps) {
  if (!visible) return null

  const formattedDate = new Date(lastAccessedAt).toLocaleDateString('zh-CN', {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })

  return (
    <Pressable style={styles.backdrop} onPress={() => {}}>
      <View style={styles.card}>
        <Text style={styles.icon}>⟳</Text>
        <Text style={styles.title}>RESUME LESSON?</Text>
        <Text style={styles.subtitle}>
          You have unfinished progress from {formattedDate}.
        </Text>
        <View style={styles.progressInfo}>
          <View style={styles.progressBarBg}>
            <View
              style={[
                styles.progressBarFill,
                { width: `${totalSteps > 0 ? (completedSteps / totalSteps) * 100 : 0}%` }
              ]}
            />
          </View>
          <Text style={styles.progressLabel}>
            {completedSteps} / {totalSteps} STEPS COMPLETED
          </Text>
        </View>
        <View style={styles.actions}>
          <Button title="RESUME" onPress={onResume} />
          <View style={{ height: 8 }} />
          <Button
            title="START OVER"
            variant="text"
            onPress={onRestart}
          />
        </View>
      </View>
    </Pressable>
  )
}

const styles = StyleSheet.create({
  backdrop: {
    ...StyleSheet.absoluteFill,
    backgroundColor: 'rgba(0,0,0,0.5)',
    justifyContent: 'center',
    alignItems: 'center',
    zIndex: 100,
    padding: space[6]
  },
  card: {
    backgroundColor: color.bg,
    borderWidth: border.widthThick,
    borderColor: color.fg,
    padding: space[5],
    width: '100%',
    maxWidth: 320,
    alignItems: 'center'
  },
  icon: {
    fontSize: 40,
    color: color.fg,
    marginBottom: space[3]
  },
  title: {
    ...typography.title,
    textAlign: 'center',
    marginBottom: space[2]
  },
  subtitle: {
    ...typography.body,
    opacity: 0.6,
    textAlign: 'center',
    marginBottom: space[4]
  },
  progressInfo: {
    width: '100%',
    marginBottom: space[4]
  },
  progressBarBg: {
    height: 4,
    backgroundColor: color.muted,
    borderWidth: border.width,
    borderColor: color.border
  },
  progressBarFill: {
    height: '100%',
    backgroundColor: color.fg
  },
  progressLabel: {
    ...typography.labelSm,
    textAlign: 'center',
    marginTop: space[2],
    opacity: 0.5
  },
  actions: {
    width: '100%'
  }
})
