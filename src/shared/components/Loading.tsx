import { ActivityIndicator, View, Text, StyleSheet } from 'react-native'

interface LoadingProps {
  message?: string
}

export function Loading({ message }: LoadingProps) {
  return (
    <View style={styles.container}>
      <ActivityIndicator size="large" color="#2563eb" />
      {message ? <Text style={styles.message}>{message}</Text> : null}
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 32
  },
  message: {
    marginTop: 12,
    fontSize: 14,
    color: '#6b7280'
  }
})
