export interface WhisperASR {
  recognize(audioPath: string): Promise<WhisperResult>
  isReady(): Promise<boolean>
}

export interface WhisperResult {
  text: string
  confidence: number
}
