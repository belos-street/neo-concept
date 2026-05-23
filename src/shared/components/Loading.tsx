import { View, Text, ActivityIndicator, StyleSheet } from 'react-native'
import { color, typography } from '@shared/theme'

interface LoadingProps {
  message?: string
}

export function Loading({ message }: LoadingProps) {
  return (
    <View style={styles.container}>
      <ActivityIndicator size="large" color={color.fg} />
      {message ? <Text style={styles.message}>{message}</Text> : null}
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 32,
    backgroundColor: color.bg,
  },
  message: {
    marginTop: 12,
    ...typography.caption,
    color: color.fg,
  },
})