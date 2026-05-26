import { NativeModules } from 'react-native'

const { WhisperModule } = NativeModules

export interface WhisperResult {
  text: string
  confidence: number
}

export interface WhisperASR {
  init(): Promise<void>
  startRecording(): Promise<string>
  stopRecording(): Promise<string | null>
  recognize(audioPath: string): Promise<WhisperResult>
  isReady(): Promise<boolean>
}

export const whisper: WhisperASR = {
  init: () => WhisperModule.init(),
  startRecording: () => WhisperModule.startRecording(),
  stopRecording: () => WhisperModule.stopRecording(),
  recognize: (audioPath: string) => WhisperModule.recognize(audioPath),
  isReady: () => WhisperModule.isReady()
}