import { View, StyleSheet, type ViewProps } from 'react-native'

interface CardProps extends ViewProps {
  padded?: boolean
}

export function Card({ children, style, padded = true, ...rest }: CardProps) {
  return (
    <View
      style={[styles.base, padded && styles.padded, style]}
      {...rest}>
      {children}
    </View>
  )
}

const styles = StyleSheet.create({
  base: {
    backgroundColor: '#ffffff',
    borderRadius: 12,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.08,
    shadowRadius: 4,
    elevation: 2
  },
  padded: {
    padding: 16
  }
})
