import { View, Text, StyleSheet } from 'react-native'

export function CourseListScreen() {
  return (
    <View style={styles.container}>
      <Text style={styles.title}>课程列表</Text>
      <Text style={styles.subtitle}>Book → Unit → Lesson 将在此展开</Text>
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
