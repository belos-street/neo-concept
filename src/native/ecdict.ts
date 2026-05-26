import { NativeModules } from 'react-native'

const { EcdictModule } = NativeModules

export interface WordEntry {
  word: string
  phonetic: string
  definition: string
  pos: string
  exchange: string
}

export interface ECDICT {
  init(): Promise<void>
  lookup(word: string): Promise<WordEntry | null>
  isReady(): Promise<boolean>
}

export const ecdict: ECDICT = {
  init: () => EcdictModule.init(),
  lookup: (word: string) => EcdictModule.lookup(word),
  isReady: () => EcdictModule.isReady()
}