import { View, Text, StyleSheet } from 'react-native'

export function DownloadScreen() {
  return (
    <View style={styles.container}>
      <Text style={styles.title}>下载课程</Text>
      <Text style={styles.subtitle}>下载进度将在此显示</Text>
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
