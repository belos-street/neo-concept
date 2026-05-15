import { create } from 'zustand'
import { Settings, DEFAULT_SETTINGS } from '@shared/types/settings'
import { mmkvStorage } from '@shared/hooks/useMMKV'

const SETTINGS_KEY = 'settings'

function loadSettings(): Settings {
  const raw = mmkvStorage.getString(SETTINGS_KEY)
  if (!raw) return DEFAULT_SETTINGS
  try {
    return { ...DEFAULT_SETTINGS, ...JSON.parse(raw) }
  } catch {
    return DEFAULT_SETTINGS
  }
}

function saveSettings(settings: Settings) {
  mmkvStorage.set(SETTINGS_KEY, JSON.stringify(settings))
}

export const useSettingsStore = create<{
  settings: Settings
  updateSetting: <K extends keyof Settings>(key: K, value: Settings[K]) => void
  resetSettings: () => void
}>((set) => ({
  settings: loadSettings(),

  updateSetting: (key, value) =>
    set((state) => {
      const next = { ...state.settings, [key]: value }
      saveSettings(next)
      return { settings: next }
    }),

  resetSettings: () => {
    saveSettings(DEFAULT_SETTINGS)
    set({ settings: DEFAULT_SETTINGS })
  }
}))
