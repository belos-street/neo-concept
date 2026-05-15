import { View, Text, StyleSheet } from 'react-native'

export function LessonScreen() {
  return (
    <View style={styles.container}>
      <Text style={styles.title}>学习页面</Text>
      <Text style={styles.subtitle}>Step 1-6 将在此展示</Text>
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#f9fafb'
  },
  title: {
    fontSize: 20,
    fontWeight: '700',
    color: '#1f2937'
  },
  subtitle: {
    marginTop: 8,
    fontSize: 14,
    color: '#9ca3af'
  }
})
