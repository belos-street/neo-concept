import { View, Text, Pressable, StyleSheet } from 'react-native'
import { color, border, touch, typography } from '@shared/theme'

interface ScreenHeaderProps {
  title: string
  onBack?: () => void
  rightAction?: {
    label: string
    onPress: () => void
  }
}

export function ScreenHeader({ title, onBack, rightAction }: ScreenHeaderProps) {
  return (
    <View style={styles.container}>
      <View style={styles.left}>
        {onBack ? (
          <Pressable onPress={onBack} style={styles.backBtn}>
            <Text style={styles.backArrow}>←</Text>
          </Pressable>
        ) : null}
        <Text style={styles.title}>{title.toUpperCase()}</Text>
      </View>
      {rightAction ? (
        <Pressable onPress={rightAction.onPress} style={styles.actionBtn}>
          <Text style={styles.actionLabel}>{rightAction.label.toUpperCase()}</Text>
        </Pressable>
      ) : (
        <View style={styles.actionPlaceholder} />
      )}
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    height: 48,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: 16,
    backgroundColor: color.bg,
    borderBottomWidth: border.width,
    borderBottomColor: color.border,
  },
  left: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
  },
  backBtn: {
    width: 44,
    height: 44,
    alignItems: 'center',
    justifyContent: 'center',
    marginLeft: -12,
  },
  backArrow: {
    fontSize: 20,
    color: color.fg,
  },
  title: {
    ...typography.titleSm,
  },
  actionBtn: {
    height: touch.minHeight,
    justifyContent: 'center',
    paddingHorizontal: 4,
  },
  actionLabel: {
    ...typography.label,
    color: color.fg,
  },
  actionPlaceholder: {
    width: 44,
  },
})