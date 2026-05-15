import { View, Text, StyleSheet, ScrollView } from 'react-native'
import { useSettingsStore } from '@features/settings/store'
import { Card } from '@shared/components'

export function SettingsScreen() {
  const { settings, updateSetting } = useSettingsStore()

  return (
    <ScrollView style={styles.container}>
      <View style={styles.content}>
        <Text style={styles.heading}>设置</Text>

        <Card style={styles.section}>
          <Text style={styles.sectionTitle}>学习模式</Text>
          <Text style={styles.value}>
            当前：{settings.learningMode === 'linear' ? '线性模式' : '自由模式'}
          </Text>
        </Card>

        <Card style={styles.section}>
          <Text style={styles.sectionTitle}>TTS 朗读速度</Text>
          <Text style={styles.value}>当前：{settings.ttsSpeed}x</Text>
        </Card>

        <Card style={styles.section}>
          <Text style={styles.sectionTitle}>口语评分阈值</Text>
          <Text style={styles.value}>当前：{settings.speakingThreshold}%</Text>
        </Card>

        <Card style={styles.section}>
          <Text style={styles.sectionTitle}>课程仓库 URL</Text>
          <Text style={styles.value}>
            {settings.repoUrl || '未配置'}
          </Text>
        </Card>
      </View>
    </ScrollView>
  )
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#f9fafb'
  },
  content: {
    padding: 16
  },
  heading: {
    fontSize: 24,
    fontWeight: '700',
    color: '#1f2937',
    marginBottom: 16
  },
  section: {
    marginBottom: 12
  },
  sectionTitle: {
    fontSize: 14,
    fontWeight: '600',
    color: '#6b7280',
    marginBottom: 4
  },
  value: {
    fontSize: 16,
    color: '#1f2937'
  }
})
