import { View, Text, ScrollView, StyleSheet } from 'react-native'
import { useSafeAreaInsets } from 'react-native-safe-area-context'
import { ScreenHeader, Divider, Button } from '@shared/components'
import { color, space, typography } from '@shared/theme'

export function CourseListScreen() {
  const insets = useSafeAreaInsets()

  return (
    <View style={[styles.container, { paddingTop: insets.top }]}>
      <ScreenHeader
        title="课程"
        rightAction={{ label: '检查更新', onPress: () => {} }}
      />
      <ScrollView
        style={styles.scroll}
        contentContainerStyle={styles.scrollContent}
      >
        <View style={styles.emptyState}>
          <Text style={styles.emptyIcon}>∅</Text>
          <Text style={styles.emptyTitle}>暂无课程</Text>
          <Text style={styles.emptyDesc}>
            请在设置中配置课程仓库 URL，{'\n'}
            或点击右上角「检查更新」获取课程列表
          </Text>
          <Divider />
          <Button
            title="配置仓库地址"
            variant="secondary"
            onPress={() => {}}
          />
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
    paddingVertical: space[8],
  },
  emptyState: {
    alignItems: 'center',
    paddingTop: 80,
  },
  emptyIcon: {
    fontSize: 48,
    color: color.fg,
    marginBottom: space[4],
    opacity: 0.2,
  },
  emptyTitle: {
    ...typography.title,
    marginBottom: space[3],
  },
  emptyDesc: {
    ...typography.bodySm,
    textAlign: 'center',
    lineHeight: 22,
    marginBottom: space[4],
    opacity: 0.6,
  },
})