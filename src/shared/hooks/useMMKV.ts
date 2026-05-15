import { MMKV } from 'react-native-mmkv'

export const mmkvStorage = new MMKV({ id: 'neo-concept-storage' })

export function useMMKV() {
  return {
    getString: (key: string) => mmkvStorage.getString(key) ?? null,
    setString: (key: string, value: string) => mmkvStorage.set(key, value),
    getObject: <T>(key: string): T | null => {
      const raw = mmkvStorage.getString(key)
      if (!raw) return null
      try {
        return JSON.parse(raw) as T
      } catch {
        return null
      }
    },
    setObject: (key: string, value: unknown) =>
      mmkvStorage.set(key, JSON.stringify(value)),
    getNumber: (key: string) => mmkvStorage.getNumber(key) ?? null,
    setNumber: (key: string, value: number) => mmkvStorage.set(key, value),
    getBoolean: (key: string) => mmkvStorage.getBoolean(key) ?? false,
    setBoolean: (key: string, value: boolean) => mmkvStorage.set(key, value),
    delete: (key: string) => mmkvStorage.delete(key),
    clearAll: () => mmkvStorage.clearAll()
  }
}
