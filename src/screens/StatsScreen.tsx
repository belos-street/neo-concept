import { View, Text, ScrollView, StyleSheet } from 'react-native'
import { useSafeAreaInsets } from 'react-native-safe-area-context'
import { ScreenHeader, Divider, Card } from '@shared/components'
import { color, space, typography } from '@shared/theme'

export function StatsScreen() {
  const insets = useSafeAreaInsets()

  return (
    <View style={[styles.container, { paddingTop: insets.top }]}>
      <ScreenHeader title="统计" />
      <ScrollView
        style={styles.scroll}
        contentContainerStyle={styles.scrollContent}
      >
        <Text style={styles.sectionTitle}>学习概况</Text>

        <Card padded style={styles.statRow}>
          <Text style={styles.statLabel}>已完成课程</Text>
          <Text style={styles.statValue}>0</Text>
        </Card>

        <Card padded style={styles.statRow}>
          <Text style={styles.statLabel}>学习总时长</Text>
          <Text style={styles.statValue}>0 min</Text>
        </Card>

        <Card padded style={styles.statRow}>
          <Text style={styles.statLabel}>已掌握词汇</Text>
          <Text style={styles.statValue}>0</Text>
        </Card>

        <Divider />

        <Text style={styles.sectionTitle}>学习进度</Text>
        <View style={styles.placeholder}>
          <Text style={styles.placeholderText}>完成第一课后开始统计</Text>
        </View>
      </ScrollView>
    </View>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: color.bg,
  },
  scroll: {
    flex: 1,
  },
  scrollContent: {
    paddingHorizontal: space[4],
    paddingVertical: space[6],
  },
  sectionTitle: {
    ...typography.titleSm,
    marginBottom: space[4],
  },
  statRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginBottom: space[3],
  },
  statLabel: {
    ...typography.bodySm,
  },
  statValue: {
    ...typography.titleSm,
  },
  placeholder: {
    paddingVertical: space[10],
    alignItems: 'center',
  },
  placeholderText: {
    ...typography.bodySm,
    opacity: 0.4,
  },
})