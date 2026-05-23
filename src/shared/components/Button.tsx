import {
  Pressable,
  Text,
  StyleSheet,
  type PressableProps,
  type StyleProp,
  type ViewStyle,
} from 'react-native'
import { color, border, touch, typography } from '@shared/theme'

interface ButtonProps extends PressableProps {
  title: string
  variant?: 'primary' | 'secondary' | 'text'
  style?: StyleProp<ViewStyle>
}

export function Button({
  title,
  variant = 'primary',
  style,
  disabled,
  ...rest
}: ButtonProps) {
  return (
    <Pressable
      style={({ pressed }) => [
        styles.base,
        variant === 'primary' && styles.primary,
        variant === 'secondary' && styles.secondary,
        variant === 'text' && styles.text,
        pressed && variant === 'primary' && styles.primaryPressed,
        pressed && variant === 'secondary' && styles.secondaryPressed,
        pressed && variant === 'text' && styles.textPressed,
        disabled && styles.disabled,
        style,
      ]}
      disabled={disabled}
      {...rest}
    >
      {({ pressed }) => (
        <Text
          style={[
            styles.label,
            variant === 'primary' && styles.primaryLabel,
            variant === 'secondary' && styles.secondaryLabel,
            variant === 'text' && styles.textLabel,
            pressed && variant === 'text' && styles.textPressedLabel,
            disabled && styles.disabledLabel,
          ]}
        >
          {title.toUpperCase()}
        </Text>
      )}
    </Pressable>
  )
}

const styles = StyleSheet.create({
  base: {
    height: 52,
    paddingHorizontal: 24,
    alignItems: 'center',
    justifyContent: 'center',
    borderWidth: border.width,
    borderColor: color.border,
    borderRadius: border.radius,
    minHeight: touch.minHeight,
  },
  primary: {
    backgroundColor: color.fg,
  },
  primaryPressed: {
    backgroundColor: color.accent,
    borderColor: color.accent,
  },
  secondary: {
    backgroundColor: color.bg,
  },
  secondaryPressed: {
    backgroundColor: color.bg,
    borderColor: color.accent,
  },
  text: {
    backgroundColor: 'transparent',
    borderWidth: 0,
    minWidth: undefined,
    paddingHorizontal: 0,
    height: touch.minHeight,
  },
  textPressed: {
    backgroundColor: 'transparent',
    borderWidth: 0,
  },
  disabled: {
    backgroundColor: color.muted,
    borderColor: color.muted,
  },
  label: {
    ...typography.labelLg,
  },
  primaryLabel: {
    color: color.bg,
  },
  secondaryLabel: {
    color: color.fg,
  },
  textLabel: {
    ...typography.label,
    textDecorationLine: 'underline',
    textDecorationColor: color.accent,
  },
  textPressedLabel: {
    color: color.accent,
  },
  disabledLabel: {
    opacity: 0.5,
  },
})