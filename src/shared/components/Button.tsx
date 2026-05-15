import {
  TouchableOpacity,
  Text,
  StyleSheet,
  type TouchableOpacityProps,
  type StyleProp,
  type ViewStyle,
  type TextStyle
} from 'react-native'

interface ButtonProps extends TouchableOpacityProps {
  title: string
  variant?: 'primary' | 'secondary' | 'ghost'
  size?: 'sm' | 'md' | 'lg'
  style?: StyleProp<ViewStyle>
  textStyle?: StyleProp<TextStyle>
}

export function Button({
  title,
  variant = 'primary',
  size = 'md',
  style,
  textStyle,
  disabled,
  ...rest
}: ButtonProps) {
  return (
    <TouchableOpacity
      style={[
        styles.base,
        styles[variant],
        styles[size],
        disabled && styles.disabled,
        style
      ]}
      disabled={disabled}
      activeOpacity={0.7}
      {...rest}>
      <Text
        style={[
          styles.text,
          styles[`${variant}Text` as keyof typeof styles],
          styles[`${size}Text` as keyof typeof styles],
          disabled && styles.disabledText,
          textStyle
        ]}>
        {title}
      </Text>
    </TouchableOpacity>
  )
}

const styles = StyleSheet.create({
  base: {
    borderRadius: 8,
    alignItems: 'center',
    justifyContent: 'center'
  },
  primary: {
    backgroundColor: '#2563eb'
  },
  secondary: {
    backgroundColor: '#e5e7eb'
  },
  ghost: {
    backgroundColor: 'transparent'
  },
  sm: {
    paddingHorizontal: 12,
    paddingVertical: 6
  },
  md: {
    paddingHorizontal: 16,
    paddingVertical: 10
  },
  lg: {
    paddingHorizontal: 24,
    paddingVertical: 14
  },
  disabled: {
    opacity: 0.5
  },
  text: {
    fontWeight: '600'
  },
  primaryText: {
    color: '#ffffff'
  },
  secondaryText: {
    color: '#1f2937'
  },
  ghostText: {
    color: '#2563eb'
  },
  smText: {
    fontSize: 13
  },
  mdText: {
    fontSize: 15
  },
  lgText: {
    fontSize: 17
  },
  disabledText: {
    opacity: 0.5
  }
})
