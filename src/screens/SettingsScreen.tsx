import { View, Text, ScrollView, StyleSheet, Platform } from 'react-native'
import { useSafeAreaInsets } from 'react-native-safe-area-context'
import { ScreenHeader, Divider } from '@shared/components'
import { useSettingsStore } from '@features/settings/store'
import { color, space, typography } from '@shared/theme'

export function SettingsScreen() {
  const insets = useSafeAreaInsets()
  const { settings } = useSettingsStore()

  const learningModeLabel =
    settings.learningMode === 'linear' ? '线性模式' : '自由模式'

  return (
    <View style={[styles.container, { paddingTop: insets.top }]}>
      <ScreenHeader title="设置" />
      <ScrollView
        style={styles.scroll}
        contentContainerStyle={styles.scrollContent}
      >
        <Text style={styles.sectionTitle}>学习</Text>
        <SettingRow
          label="学习模式"
          value={learningModeLabel}
        />
        <SettingRow
          label="TTS 语速"
          value={`${settings.ttsSpeed}x`}
        />
        <SettingRow
          label="口语评分阈值"
          value={`${settings.speakingThreshold}%`}
        />

        <Divider />

        <Text style={styles.sectionTitle}>课程</Text>
        <SettingRow
          label="课程仓库 URL"
          value={settings.repoUrl || '未配置'}
          mono
        />

        <Divider />

        <Text style={styles.sectionTitle}>关于</Text>
        <SettingRow label="版本" value="0.1.0" />
      </ScrollView>
    </View>
  )
}

function SettingRow({
  label,
  value,
  mono,
}: {
  label: string
  value: string
  mono?: boolean
}) {
  return (
    <View style={styles.settingRow}>
      <Text style={styles.settingLabel}>{label}</Text>
      <Text style={[styles.settingValue, mono && styles.settingValueMono]}>
        {value}
      </Text>
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
    ...typography.label,
    color: color.accent,
    marginBottom: space[4],
  },
  settingRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    paddingVertical: space[3],
    borderBottomWidth: 1,
    borderBottomColor: color.muted,
  },
  settingLabel: {
    ...typography.body,
  },
  settingValue: {
    ...typography.bodySm,
    opacity: 0.6,
  },
  settingValueMono: {
    fontSize: 12,
    fontFamily: Platform.select({ ios: 'Menlo', android: 'monospace' }),
    maxWidth: '60%',
    textAlign: 'right',
  },
})