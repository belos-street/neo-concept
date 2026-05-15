export interface ECDICT {
  lookup(word: string): Promise<WordEntry | null>
  isReady(): Promise<boolean>
}

export interface WordEntry {
  word: string
  phonetic: string
  pos: string
  meaning: string
  sentence: string
}
