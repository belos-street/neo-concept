export interface Settings {
  learningMode: 'linear' | 'free'
  ttsSpeed: number
  speakingThreshold: number
  repoUrl: string
}

export const DEFAULT_SETTINGS: Settings = {
  learningMode: 'linear',
  ttsSpeed: 1.0,
  speakingThreshold: 60,
  repoUrl: ''
}
