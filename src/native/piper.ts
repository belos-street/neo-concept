import { NativeModules } from 'react-native'

const { PiperModule } = NativeModules

export interface PiperTTS {
  init(): Promise<void>
  speak(text: string, speed?: number): Promise<void>
  stop(): Promise<void>
  isReady(): Promise<boolean>
}

export const piper: PiperTTS = {
  init: () => PiperModule.init(),
  speak: (text: string, speed = 1.0) => PiperModule.speak(text, speed),
  stop: () => PiperModule.stop(),
  isReady: () => PiperModule.isReady()
}