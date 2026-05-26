import { View, Text, Pressable, StyleSheet } from 'react-native'
import { Button } from '@shared/components'
import { color, border, space, typography } from '@shared/theme'

interface ExitModalProps {
  visible: boolean
  onConfirm: () => void
  onCancel: () => void
}

export function ExitModal({ visible, onConfirm, onCancel }: ExitModalProps) {
  if (!visible) return null

  return (
    <Pressable style={styles.backdrop} onPress={onCancel}>
      <Pressable style={styles.card} onPress={() => {}}>
        <Text style={styles.icon}>⚠</Text>
        <Text style={styles.title}>LEAVE LESSON?</Text>
        <Text style={styles.subtitle}>
          Your progress will be saved. You can resume from where you left off.
        </Text>
        <View style={styles.actions}>
          <Button
            title="STAY"
            variant="secondary"
            onPress={onCancel}
          />
          <View style={{ width: 8 }} />
          <Button
            title="LEAVE"
            onPress={onConfirm}
          />
        </View>
      </Pressable>
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
  actions: {
    flexDirection: 'row',
    width: '100%'
  }
})
