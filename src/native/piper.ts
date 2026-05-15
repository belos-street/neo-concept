export interface PiperTTS {
  init(): Promise<void>
  speak(text: string, speed?: number): Promise<void>
  stop(): Promise<void>
  isReady(): Promise<boolean>
}
