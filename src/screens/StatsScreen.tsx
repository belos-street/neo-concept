import { View, Text, StyleSheet } from 'react-native'

export function StatsScreen() {
  return (
    <View style={styles.container}>
      <Text style={styles.title}>学习统计</Text>
      <Text style={styles.subtitle}>学习时长、完成课数将在此显示</Text>
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
