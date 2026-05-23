import { View, Pressable, StyleSheet, type ViewProps, type PressableProps, type StyleProp, type ViewStyle } from 'react-native'
import { color, border } from '@shared/theme'

interface CardProps extends ViewProps {
  padded?: boolean
}

interface PressableCardProps extends PressableProps {
  padded?: boolean
  style?: StyleProp<ViewStyle>
}

export function Card({ children, padded = true, style, ...rest }: CardProps) {
  return (
    <View style={[styles.base, padded && styles.padded, style]} {...rest}>
      {children}
    </View>
  )
}

export function PressableCard({ children, padded = true, style, disabled, ...rest }: PressableCardProps) {
  return (
    <Pressable
      style={({ pressed }) => [
        styles.base,
        padded && styles.padded,
        pressed && !disabled && styles.pressed,
        disabled && styles.disabled,
        style,
      ]}
      disabled={disabled}
      {...rest}
    >
      {children}
    </Pressable>
  )
}

const styles = StyleSheet.create({
  base: {
    backgroundColor: color.bg,
    borderWidth: border.width,
    borderColor: color.border,
    borderRadius: border.radius,
  },
  padded: {
    padding: 20,
  },
  pressed: {
    backgroundColor: color.fg,
  },
  disabled: {
    backgroundColor: color.muted,
    borderColor: color.muted,
    opacity: 0.5,
  },
})